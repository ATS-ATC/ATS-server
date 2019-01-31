package com.alucn.weblab.disarray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import com.alucn.casemanager.server.common.CaseConfigurationCache;
import com.alucn.casemanager.server.common.ConfigProperites;
import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.util.Fiforeader;
import com.alucn.casemanager.server.common.util.Fifowriter;
import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.casemanager.server.common.util.SendMail;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DistriButeCaseToLab {
	public static Logger logger = Logger.getLogger(DistriButeCaseToLab.class);

	String specialRelease = "28A,28C,28F,28G,28H";
	int counterUninsRS=0;
	Map<String, Integer> idleNum = new HashMap<String, Integer>();
	List<String> idleThreadSign = new ArrayList<String>();
	private List<Object> listParams = new ArrayList<Object>();
	private DistriButeCaseToLab(){};
	static DistriButeCaseToLab instance = null;

	public static DistriButeCaseToLab getDistriButeCaseToLab() {
		if (instance == null) {
			instance = new DistriButeCaseToLab();
		}
		return instance;
	}
	
	private JSONArray removeRelease(JSONArray list) {
		JSONArray jsonList = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			String temp = String.valueOf(list.get(i)).trim().replaceAll("\\d+\\w?$", "").replace(".*", "").replace("RTDB ", "").toUpperCase();
			for (int j = 0; j < specialRelease.split(",").length; j++) {
				if (temp.endsWith(specialRelease.split(",")[j])) {
					temp = temp.substring(temp.indexOf(specialRelease.split(",")[j]), temp.length());
				}
			}
			jsonList.add(temp);
		}
		return jsonList;
	}

	private JSONArray genCaseListToLab(String serverName) throws Exception {
		JSONArray caseList = new JSONArray();
		JdbcUtil jdbc_cf = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
		String querySecDataSql = "SELECT D.base_data, D.feature_number, T.server, C.SecData, COUNT(C.SecData) as num From toDistributeCases T LEFT JOIN CaseDepends C ON T.case_name = C.case_name LEFT JOIN DailyCase D ON C.case_name = D.case_name WHERE T.server LIKE ? AND T.case_name NOT IN (SELECT case_name FROM DistributedCaseTbl) GROUP BY C.SecData, D.base_data, D.feature_number ORDER BY num DESC;";
		listParams.clear();
		listParams.add("%"+serverName+"%");
		List<Map<String, Object>> secDataList = jdbc_cf.findModeResult(querySecDataSql, listParams);
		int readyDistributeCaseNum = 0;
		for(int i=0; i<secDataList.size(); i++){
			listParams.add(secDataList.get(i).get("SecData"));
			listParams.add(secDataList.get(i).get("base_data"));
			listParams.add(secDataList.get(i).get("feature_number"));
			String queryToDistributeCaseSql = "SELECT D.base_data, D.feature_number,T.case_name, T.server, C.SecData From toDistributeCases T LEFT JOIN CaseDepends C ON T.case_name = C.case_name LEFT JOIN DailyCase D ON C.case_name = D.case_name WHERE T.server LIKE ? AND T.case_name NOT IN (SELECT case_name FROM DistributedCaseTbl) AND C.SecData = ? and D.base_data = ? and D.feature_number = ? ;";
			List<Map<String, Object>> toDistributeCaseList = jdbc_cf.findModeResult(queryToDistributeCaseSql, listParams);
			for(int j=0; j<toDistributeCaseList.size(); j++){
				caseList.add(toDistributeCaseList.get(j).get("case_name"));
				readyDistributeCaseNum++;
				if (readyDistributeCaseNum >= Integer.valueOf(ConfigProperites.getInstance().getMaxCaseSizeForOneLab())) {
					return caseList;
				}
			}
			break;
		}
		return caseList;
	}
	
	private void changeLabRunStatus(String serverName, String release, String protocol, JSONArray SPA, JSONArray DB) throws IOException{
		JSONObject installedLab = JSONObject.fromObject(Fiforeader.readLastLine("/home/huanglei/DB/installLab.json"));
		JSONObject installedLabInfo = new JSONObject();
    	if(installedLab.containsKey(serverName)){
    		installedLabInfo = installedLab.getJSONObject(serverName);
    	}
    	installedLabInfo.put(Constant.SERVERRELEASE, release);
		installedLabInfo.put(Constant.SERVERPROTOCOL, protocol);
		installedLabInfo.put(Constant.SERVERNUM, "1");
		installedLabInfo.put(Constant.SERVERMATE, "N");
		installedLabInfo.put(Constant.STATUS, "");
		installedLabInfo.put(Constant.SERVERSPA, SPA);
		installedLabInfo.put(Constant.SERVERRTDB, DB);
		installedLab.put(serverName, installedLabInfo);
    	Fifowriter.writerFile("/home/huanglei/DB", "installLab.json", installedLab.toString());
	}
	
	public JSONObject getDistributeCases() throws Exception {
		JSONObject availableCases = new JSONObject();
		JSONObject cases = new JSONObject();
		JSONArray serversList = CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock, true,null);
		JSONObject serverMem;
		int idleServerNum = 0;
		int caseListNull = 0;
		for (int i = 0; i < serversList.size(); i++) {
			serverMem = serversList.getJSONObject(i).getJSONObject(Constant.LAB);
			String serverName = serverMem.getString(Constant.SERVERNAME);
			if (serversList.getJSONObject(i).getJSONObject(Constant.TASKSTATUS).getString(Constant.STATUS).equals(Constant.CASESTATUSIDLE) && !serverMem.getJSONArray(Constant.SERVERSPA).getString(0).equals("SPALIST_XXX")) {
				idleServerNum++;
				if (null == idleNum.get(serverName)){
					idleNum.put(serverName, 1);
				}else{
					int numTmp = idleNum.get(serverName)+1;
					idleNum.put(serverName, numTmp);
				}
				logger.debug("idle server: " + serverName);
				JSONArray caseList = genCaseListToLab(serverName);
				if(caseList.size()==0){
					caseListNull++;
				}
				JSONObject labInfo = new JSONObject();
				labInfo.put(Constant.CASELISTUUID, UUID.randomUUID().toString());
				labInfo.put(Constant.CASELIST, caseList);
				cases.put(serverName, labInfo);
			}else{
				idleNum.put(serverName, 0);
			}
		}
		if (idleServerNum > serversList.size() && caseListNull == serversList.size()) {
			if (counterUninsRS == 8640) {
				logger.debug("send e-mail of Missing spa and rtdb!");
				counterUninsRS = 0;
				Map<String, Map<String, Map<String, Map<String, String>>>> uninstallRSANSI = getUninstallRS2(serversList,"ANSI");
				Map<String, Map<String, Map<String, Map<String, String>>>> uninstallRSITU = getUninstallRS2(serversList,"ITU");
				// send mail
				JSONArray cc_list = new JSONArray();
				JSONArray to_list = new JSONArray();
				cc_list.add("lei.k.huang@alcatel-lucent.com");
				cc_list.add("Haiqi.Wang@alcatel-lucent.com");
				to_list.add("chen.k.wang@nokia-sbell.com");
				SendMail.genReport(cc_list, to_list, uninstallRSANSI, uninstallRSITU);
			}
			counterUninsRS++;
		}
		//Automatically determine case dependencies and send installed requests.
		for(String serverName: idleNum.keySet()){
			int currentServerNum = idleNum.get(serverName);
			logger.debug(serverName+" of idle status num "+currentServerNum);
            if(currentServerNum > 1){
				for (int i = 0; i < serversList.size(); i++) {
					JSONObject serverBody = serversList.getJSONObject(i);
					JSONObject serverMemTwo = serverBody.getJSONObject(Constant.LAB);
					JSONObject serverStatus = serverBody.getJSONObject(Constant.TASKSTATUS);
					String serverNameTwo = serverMemTwo.getString(Constant.SERVERNAME);
					JSONArray SPA = removeRelease(serverMemTwo.getJSONArray(Constant.SERVERSPA));
					JSONArray DB = removeRelease(serverMemTwo.getJSONArray(Constant.SERVERRTDB));
					String protocol = serverMemTwo.getString(Constant.SERVERPROTOCOL);
					String release = serverMemTwo.getString(Constant.SERVERRELEASE);
					logger.debug("idleThreadSign... " + idleThreadSign.toString());
					if (serverNameTwo.equals(serverName) && serverStatus.getString(Constant.STATUS).equals(Constant.CASESTATUSIDLE) && !idleThreadSign.contains(serverName)) {
						logger.debug("idleThreadSign after... " + idleThreadSign.toString());
						changeLabRunStatus(serverName, release, protocol, SPA, DB);
						Thread installLabThread = new Thread(new Runnable() {
							@Override
							public void run() {
								idleThreadSign.add(serverName);
								while(true){
									try {
										logger.debug("get lab status... " + serverName);
										
										idleNum.put(serverName, 0);
//										serverStatus.put(Constant.STATUS, Constant.LABSTATUSREADYINSTALL);
//										CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock,false,serverBody);
										JSONObject installedLab = JSONObject.fromObject(Fiforeader.readLastLine("/home/huanglei/DB/installLab.json"));
										String installedStatus = installedLab.getJSONObject(serverName).getString(Constant.STATUS);
										if(installedStatus!=null && !"".equals(installedStatus)) {
											serverStatus.put(Constant.STATUS, installedStatus);
											CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock,false,serverBody);
											if(Constant.REINSTALLLABSUCCESS.equals(installedStatus)){
												logger.debug("lab reinstall completed... " + serverName+" response result "+installedStatus);
												serverStatus.put(Constant.STATUS, Constant.CASESTATUSIDLE);
												CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock,false,serverBody);
												idleThreadSign.remove(serverName);
												break;
											}else if(Constant.REINSTALLLABFAIL.equals(installedStatus)){
												logger.debug("lab reinstall completed... " + serverName+" response result "+installedStatus);
												JSONArray cc_list = new JSONArray();
												JSONArray to_list = new JSONArray();
												cc_list.add("Haiqi.Wang@alcatel-lucent.com");
												cc_list.add("lei.k.huang@nokia-sbell.com");
												to_list.add("xiuyun.he@nokia-sbell.com");
												SendMail.genReport(cc_list, to_list, serverName);
												serverStatus.put(Constant.STATUS, installedStatus);
												CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock,false,serverBody);
												idleThreadSign.remove(serverName);
												break;
											}else if(Constant.REINSTALLLABCOMPLETE.equals(installedStatus)){
												logger.debug("cases are completed...");
												changeLabRunStatus(serverName, release, protocol, SPA, DB);
												serverStatus.put(Constant.STATUS, installedStatus);
												CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock,false,serverBody);
											}
										}
									} catch (Exception e) {
										logger.error("lab reinstall exception... " + serverName, e);
									}finally{
										try {
											Thread.sleep(100000);
										} catch (InterruptedException e) {}
									}
								}
							}
						});
						installLabThread.start();
					}
				}
			}
			Thread.sleep(2000);
		}
		availableCases.put("availableCase", cases);
		return availableCases;
	}

	private Map<String, Map<String, Map<String, Map<String, String>>>> getUninstallRS2(JSONArray Servers,
			String protocol) throws Exception {
		Map<String, Map<String, Map<String, Map<String, String>>>> unInstallRS = new HashMap<String, Map<String, Map<String, Map<String, String>>>>();
		Map<String, Map<String, Map<String, String>>> unInstallRtdb = new HashMap<String, Map<String, Map<String, String>>>();
		Map<String, Map<String, Map<String, String>>> unInstallSpa = new HashMap<String, Map<String, Map<String, String>>>();
		String sql = "SELECT DISTINCT SPA, RTDB FROM toDistributeCases WHERE server='[]'";
		if (protocol.equals("ANSI")) {
			sql += " and customer='VZW'";
		} else {
			sql += " and customer!='VZW'";
		}
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
		ArrayList<HashMap<String, Object>> list = jdbc.query(sql);
		for (int j = 0; j < list.size(); j++) {
			Map<String, String> caseRtdbMap = new HashMap<String, String>();
			Map<String, String> caseSpaMap = new HashMap<String, String>();
			List<String> caseSpaAndRtdbList = new ArrayList<String>();
			JSONArray caseSpa = JSONArray.fromObject(list.get(j).get("SPA"));
			for (int m = 0; m < caseSpa.size(); m++) {
				String serSpa = mattch(caseSpa.getString(m), "(^[A-Z,a-z]+)");
				if (!serSpa.equals("")) {
					caseSpaAndRtdbList.add(serSpa);
					caseSpaMap.put(serSpa, "");
				} else {
					continue;
				}
			}

			JSONArray caseRtdb = JSONArray.fromObject(list.get(j).get("RTDB"));
			for (int n = 0; n < caseRtdb.size(); n++) {
				String serRtdb = mattch(caseRtdb.getString(n), "(^[A-Z,a-z]+)");
				if (!serRtdb.equals("")) {
					caseSpaAndRtdbList.add(serRtdb);
					caseRtdbMap.put(serRtdb, "");
				} else {
					continue;
				}
			}

			Map<String, Double> maxMap = new HashMap<String, Double>();
			Map<String, Map<String, Map<String, String>>> serverLaRSMap = new HashMap<String, Map<String, Map<String, String>>>();
			for (int i = 0; i < Servers.size(); i++) {
				Map<String, String> rtdbMap = new HashMap<String, String>();
				Map<String, String> spaMap = new HashMap<String, String>();
				JSONObject serverMem = Servers.getJSONObject(i).getJSONObject(Constant.LAB);
				String serverName = serverMem.getString(Constant.SERVERNAME);
				String serverProtocol = serverMem.getString(Constant.SERVERPROTOCOL);
				if (protocol.equals(serverProtocol)) {
					List<String> spaAndRtdbList = new ArrayList<String>();
					JSONArray serverSpa = serverMem.getJSONArray(Constant.SERVERSPA);
					for (int m = 0; m < serverSpa.size(); m++) {
						String serSpa = mattch(serverSpa.getString(m), "(^[A-Z,a-z]+)");
						if (!serSpa.equals("")) {
							spaMap.put(serSpa, "");
							spaAndRtdbList.add(serSpa);
						} else {
							continue;
						}
					}
					JSONArray serverRtdb = serverMem.getJSONArray(Constant.SERVERRTDB);
					for (int n = 0; n < serverRtdb.size(); n++) {
						String serRtdb = mattch(serverRtdb.getString(n), "(^[A-Z,a-z]+)");
						if (!serRtdb.equals("")) {
							rtdbMap.put(serRtdb, "");
							spaAndRtdbList.add(serRtdb);
						} else {
							continue;
						}
					}
					Map<String, Map<String, String>> SPAandRTDB = new HashMap<String, Map<String, String>>();
					SPAandRTDB.put("SPA", spaMap);
					SPAandRTDB.put("RTDB", rtdbMap);
					serverLaRSMap.put(serverName, SPAandRTDB);
					spaAndRtdbList.retainAll(caseSpaAndRtdbList);
					double inters = getIntersection(spaAndRtdbList, caseSpaAndRtdbList).size();
					double union = getUnion(spaAndRtdbList, caseSpaAndRtdbList).size();
					double num = getTwo(inters / union);
					maxMap.put(serverName, num);
				}
			}
			String serverName = getMaxValue(maxMap);
			Map<String, String> serverSpa = serverLaRSMap.get(serverName).get("SPA");
			Map<String, String> serverRTDB = serverLaRSMap.get(serverName).get("RTDB");
			for (String key : caseSpaMap.keySet()) {
				if (!serverSpa.containsKey(key)) {
					if (null == unInstallSpa.get(serverName)) {
						Map<String, String> unInSpaList = new HashMap<String, String>();
						unInSpaList.put(key, "");
						Map<String, Map<String, String>> unInSpa = new HashMap<String, Map<String, String>>();
						unInSpa.put("SPA", unInSpaList);
						unInstallSpa.put(serverName, unInSpa);
					} else {
						unInstallSpa.get(serverName).get("SPA").put(key, "");
					}
				}
			}

			for (String key : caseRtdbMap.keySet()) {
				if (!serverRTDB.containsKey(key)) {
					if (null == unInstallRtdb.get(serverName)) {
						Map<String, String> unInRtdbList = new HashMap<String, String>();
						unInRtdbList.put(key, "");
						Map<String, Map<String, String>> unInRtdb = new HashMap<String, Map<String, String>>();
						unInRtdb.put("RTDB", unInRtdbList);
						unInstallRtdb.put(serverName, unInRtdb);
					} else {
						unInstallRtdb.get(serverName).get("RTDB").put(key, "");
					}
				}
			}
		}
		unInstallRS.put("SPA", unInstallSpa);
		unInstallRS.put("RTDB", unInstallRtdb);
		return unInstallRS;
	}

	public static String getMaxValue(Map<String, Double> map) {
		double value = 0;
		String maxKey = null;
		List<Double> list = new ArrayList<Double>();
		Iterator<Entry<String, Double>> ite = map.entrySet().iterator();
		while (ite.hasNext()) {
			Map.Entry<String, Double> entry = (Map.Entry<String, Double>) ite.next();
			value = Double.parseDouble(entry.getValue().toString());
			list.add(entry.getValue());
			Collections.sort(list);

			if (value == Double.parseDouble(list.get(list.size() - 1).toString())) {
				maxKey = entry.getKey().toString();
			}
		}
		return maxKey;
	}

	private String mattch(String input, String regex) {
		String mattchString = "";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) {
			mattchString = matcher.group(1);
		}
		return mattchString;
	}

	private List<String> getIntersection(List<String> listOne, List<String> listTwo) {
		listOne.retainAll(listTwo);
		return listOne;
	}

	private List<String> getUnion(List<String> listOne, List<String> listTwo) {
		listTwo.removeAll(listOne);
		listOne.addAll(listTwo);
		return listOne;
	}
	public boolean getProcess(String jName) throws IOException{
		String[] cmd = {
				"/bin/sh",
				"-c",
				"ps -ef | grep "+jName
				};
		boolean flag=false;
		Process p = Runtime.getRuntime().exec(cmd);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream os = p.getInputStream();
		byte b[] = new byte[256];
		while(os.read(b)> 0)
			baos.write(b);
		String s = baos.toString().replaceAll("grep "+jName, "");
		if(s.indexOf(jName) >= 0){
			flag=true;
		}else{
			flag=false;
		}
		return flag;
	}
	
	private double getTwo(double num) {
		BigDecimal bigd = new BigDecimal(num);
		double n = bigd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return n;
	}
	
	public static void main(String[] args) throws Exception {
		DistriButeCaseToLab test = new DistriButeCaseToLab();
		// JSONArray getServerInfoFromDB = test.GetServerInfoFromDB();
		// System.out.println(getServerInfoFromDB);
		JSONObject distributeCases = test.getDistributeCases();
		System.out.println(distributeCases);
		/*
		 * JSONArray releaseList = test.GetReleaseList();
		 * System.out.println(releaseList.toString()); String porting_release =
		 * "SP18.3,SP18.9+"; String serverRelease = "SP17.3"; boolean
		 * isReleaseMath=false; JSONArray portingReleaseList = JSONArray
		 * .fromObject("[\"" + porting_release.replace("+", "").replace(",", "\",\"") +
		 * "\"]"); System.out.println(portingReleaseList.toString()); if
		 * (test.IsInJSONArray(serverRelease, portingReleaseList)) { isReleaseMath =
		 * true; } else { if (porting_release.endsWith("+")) { int serverReleasePostion
		 * = test.postionInJSONArray(serverRelease, releaseList);
		 * //logger.error(serverRelease + " --- " + releaseList); if
		 * (serverReleasePostion != -1) {
		 * System.out.println(porting_release.substring(porting_release.lastIndexOf(",")
		 * + 1, porting_release.length() - 1)); int LastReleasePostion =
		 * test.postionInJSONArray(
		 * porting_release.substring(porting_release.lastIndexOf(",") + 1,
		 * porting_release.length() - 1), releaseList);
		 * System.out.println(serverReleasePostion); if (LastReleasePostion != -1 &&
		 * serverReleasePostion >= LastReleasePostion) { isReleaseMath = true; } } } }
		 */

	}
}