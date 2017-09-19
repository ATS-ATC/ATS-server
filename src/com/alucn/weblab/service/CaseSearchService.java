package com.alucn.weblab.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alucn.casemanager.server.common.CaseConfigurationCache;
import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.util.Fiforeader;
import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.casemanager.server.listener.MainListener;
import com.alucn.weblab.dao.impl.CaseSearchDaoImpl;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author haiqiw
 * 2017年8月7日 上午10:58:41
 * desc:CaseSearchService
 */
@Service("caseSearchService")
public class CaseSearchService {
	
	@Autowired(required=true)
	private CaseSearchDaoImpl caseSearchDaoImpl;
	private Map<String, List<String>> caseSearchItemMap = new HashMap<String, List<String>>();
	public static volatile String sqlAdmin = "";
	public static volatile String dataBase = "DailyCase";
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	public Map<String, List<String>> getCaseSearch() throws NumberFormatException, InterruptedException, IOException{
		String tagConfig = MainListener.configFilesPath+File.separator+"TagConfig.json";
		JSONObject caseSearchItems = JSONObject.fromObject(Fiforeader.readCaseInfoFromChannel(tagConfig));
		JSONArray single = caseSearchItems.getJSONArray("single");
		for(int i=0; i<single.size(); i++){
			JSONObject caseSearchItem = single.getJSONObject(i);
			caseSearchItemMap.put(caseSearchItem.getString("name"), JSONArray.toList(caseSearchItem.getJSONArray("value")));
		}
		caseSearchItems = getJsonFile();
		single = caseSearchItems.getJSONArray("single");
		for(int i=0; i<single.size(); i++){
			JSONObject caseSearchItem = single.getJSONObject(i);
			caseSearchItemMap.put(caseSearchItem.getString("name"), JSONArray.toList(caseSearchItem.getJSONArray("value")));
		}
		
		JSONArray multiple = caseSearchItems.getJSONArray("multiple");
		for(int i=0; i<multiple.size(); i++){
			JSONObject caseSearchItem = multiple.getJSONObject(i);
			caseSearchItemMap.put(caseSearchItem.getString("name"), JSONArray.toList(caseSearchItem.getJSONArray("value")));
		}
		
		JSONArray Servers = CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock, true,null);
		List<String> serversName = new ArrayList<String>();
		for (int i = 0; i < Servers.size(); i++) {
			JSONObject ServerMem = Servers.getJSONObject(i).getJSONObject(Constant.LAB);
			String serverName = ServerMem.getString(Constant.SERVERNAME);
			serversName.add(serverName);
		}
		caseSearchItemMap.put("servers",serversName);
		return caseSearchItemMap;
	}
	
	public String searchCaseInfo(String cond, String auth) throws Exception{
		String [] conds = cond.split(";");
		if("".equals(conds[0])){
			return "Please select a data source !";
		}
		dataBase = conds[0];
		String sql = "select case_name from "+conds[0]+" where 1=1 ";
		if(!"".equals(conds[1])){
			String [] release = conds[1].split(",");
			sql += "and release in (";
			for(int i=0; i<release.length; i++){
				sql += "'"+release[i]+"'";
				if(i==(release.length-1)){
					sql += ") ";
				}else{
					sql += ",";
				}
			}
		}
		if(!"".equals(conds[2])){
			String [] customer = conds[2].split(",");
			sql += "and customer in (";
			for(int i=0; i<customer.length; i++){
				sql += "'"+customer[i]+"'";
				if(i==(customer.length-1)){
					sql += ") ";
				}else{
					sql += ",";
				}
			}
		}
		if(!"".equals(conds[3])){
			String [] base_data = conds[3].split(",");
			sql += "and base_data in (";
			for(int i=0; i<base_data.length; i++){
				sql += "'"+base_data[i]+"'";
				if(i==(base_data.length-1)){
					sql += ") ";
				}else{
					sql += ",";
				}
			}
		}
		if(!"".equals(conds[4])){
			sql += "and mate='"+conds[4]+"' ";
		}
		if(!"".equals(conds[5])){
			sql += "and lab_number='"+conds[5]+"' ";
		}
		if(!"".equals(conds[6])){
			sql += "and special_data='"+conds[6]+"' ";
		}
		if(!"".equals(conds[7])){
			String [] porting_release = conds[7].split(",");
			sql += "and porting_release in (";
			for(int i=0; i<porting_release.length; i++){
				sql += "'"+porting_release[i]+"'";
				if(i==(porting_release.length-1)){
					sql += ") ";
				}else{
					sql += ",";
				}
			}
		}
		if(!"".equals(conds[8])){
			String [] case_status = conds[8].split(",");
			sql += "and case_status in (";
			for(int i=0; i<case_status.length; i++){
				sql += "'"+case_status[i].toUpperCase().charAt(0)+"'";
				if(i==(case_status.length-1)){
					sql += ") ";
				}else{
					sql += ",";
				}
			}
		}
		if(!"".equals(conds[9])){
			sql += "and feature_number='"+conds[9]+"' ";
		}
		if(!"".equals(conds[10])){
			sql += "and author='"+conds[10]+"' ";
		}
		JdbcUtil jdbc = null;
		if(conds[0].equals(Constant.DAILYCASE)){
			jdbc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
		}else if(conds[0].equals(Constant.DFTTAG)){
			jdbc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB"));
		}
		ArrayList<HashMap<String, Object>> query = new ArrayList<HashMap<String, Object>>();;
		if(!"".equals(conds[11]) && !"0".equals(conds[11])){
			query = caseSearchDaoImpl.query(jdbc, sql);
			if(query!=null && query.size()!=0){
				jdbc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
				String query_sql="select max(group_id) gid from toDistributeCases";
				String gid = caseSearchDaoImpl.query(jdbc, query_sql).get(0).get("gid").toString();
				JSONArray Servers = CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock,
						true, null);
				JSONArray spaList=null;
				JSONArray rtdbList=null;
				for (int i = 0; i < Servers.size(); i++) {
					JSONObject serverMem = Servers.getJSONObject(i).getJSONObject(Constant.LAB);
					String serverName = serverMem.getString(Constant.SERVERNAME);
					if(serverName.equals(conds[11])){
						spaList=serverMem.getJSONArray(Constant.SERVERSPA);
						rtdbList=serverMem.getJSONArray(Constant.SERVERRTDB);
					}
				}
				for(int i=0; i<query.size(); i++){
					String disSql="INSERT INTO toDistributeCases (case_name, lab_number, mate, special_data, base_data, second_data, release, porting_release, SPA, RTDB, server, customer, group_id) VALUES('"+query.get(i).get("case_name")+"', '"+query.get(i).get("lab_number")+"', '"+query.get(i).get("mate")+"', '"+query.get(i).get("special_data")+"', '"+query.get(i).get("base_data")+"', '"+query.get(i).get("second_data")+"', '"+query.get(i).get("release")+"', '"+query.get(i).get("porting_release")+"', '"+spaList.toString()+"', '"+rtdbList.toString()+"', '"+query.get(i).get("server")+"', '"+query.get(i).get("customer")+"', "+gid+");";
					caseSearchDaoImpl.insert(jdbc, disSql);
				}
			}
		}else if(auth.equals("all")){
			sqlAdmin=sql;
			query = caseSearchDaoImpl.query(jdbc, sql);
		}
		return "Total record:"+query.size();
	}
	
	public JSONObject getJsonFile() {
		URL url;
		JSONObject tagInfos = new JSONObject();
		try {
			url = new URL("http://135.251.249.250/hg/SurepayDraft/rawfile/tip/.info/TagConfig.json");
			InputStream inputStream = null;
			InputStreamReader inputStreamReader = null;
			BufferedReader reader = null;
			String tempLine, response = "";
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				inputStream = connection.getInputStream();
				inputStreamReader = new InputStreamReader(inputStream);
				reader = new BufferedReader(inputStreamReader);

				while ((tempLine = reader.readLine()) != null) {
					response += tempLine;

				}

				tagInfos = JSONObject.fromObject(response);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tagInfos;
	}
}
