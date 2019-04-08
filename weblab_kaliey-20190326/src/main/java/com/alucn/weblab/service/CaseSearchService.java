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
import java.nio.channels.ScatteringByteChannel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletContext;
import javax.swing.text.DefaultEditorKit.InsertBreakAction;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ContextLoader;

import com.alucn.casemanager.server.common.CaseConfigurationCache;
import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.util.Fiforeader;
import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.casemanager.server.listener.MainListener;
import com.alucn.weblab.dao.impl.CaseSearchDaoImpl;
import com.alucn.weblab.socket.TcpClient;
import com.alucn.weblab.utils.KalieyMysqlUtil;
import com.alucn.weblab.utils.StringUtil;
import com.microsoft.schemas.office.visio.x2012.main.VisioDocumentDocument1;
import com.mysql.fabric.xmlrpc.base.Array;

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
	//private Lock lock = new ReentrantLock(true);
	@Autowired
	private ServletContext servletContext;
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	public Map<String, List<String>> getCaseSearch() throws NumberFormatException, InterruptedException, IOException{
		
		//ServletContext servletContext = ContextLoader.getCurrentWebApplicationContext().getServletContext();
		String configPath = System.getenv("WEBLAB_CONF");
		String [] args1 = {servletContext.getRealPath("conf")};       //localhost server
		String [] args2 = { configPath };
		System.out.println(args2[0]!=null?args2[0]:args1[0]);
		//String tagConfig = MainListener.configFilesPath+File.separator+"TagConfig.json";
		String tagConfig = (args2[0]!=null?args2[0]:args1[0])+File.separator+"TagConfig.json";
		//D:\eclipse-jee-oxygen-3a-win32-x86_64\workspace\.metadata\.plugins\org.eclipse.wst.server.core\tmp1\wtpwebapps\weblab_kaliey\conf\TagConfig.json
		//System.err.println("getCaseSearch >> tagConfig >> "+tagConfig);
		
		
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
		
		return caseSearchItemMap;
	}
	
	public JSONArray getCaseServer(String deptid,boolean hasRole){
		// Map<String, List<String>> caseSearchItemMap = new HashMap<String, List<String>>();
		//JSONArray Servers = CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock, true,null);
		JSONArray Servers = null;
		try {
			Servers = TcpClient.getLabInfo();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// List<String> serversName = new ArrayList<String>();
		JSONArray result = new JSONArray();
		for (int i = 0; i < Servers.size(); i++) {
			JSONObject ServerMem = Servers.getJSONObject(i).getJSONObject("body").getJSONObject(Constant.LAB);
			// String serverName = ServerMem.getString(Constant.SERVERNAME);
			String sdeptid = ServerMem.getString("deptid");
			// System.out.println("getCaseServer: " + ServerMem);
			// getCaseServer: {"deptid":"1","serverName":"BJRMS21E","serverIp":"135.242.17.206","serverRelease":"SP17.9","serverProtocol":"ITU","serverType":"Group","serverMate":"Primary","mateServer":"BJRMS21F","setName":"set1","serverSPA":["AethosTest","CDRPP311","CDRPPGW311","DIAMCL179","DROUTER179","ECTRL179","ENWTPPS179","EPAY179","EPPSA179","EPPSM179","GATEWAY179","NWTCOM111","NWTGSM066"],"serverRTDB":["SCRRTDBV7","AECIDB179","SGLDB28H","TIDDB28C","GPRSSIM08","AIRTDB179","CTRTDB179","HTIDDB179","PMOUDB179","PROMDB179","SIMDB179","SYDB179","GCIPL312","VTXDB179","SHRTDB28F","CDBRTDB","RCNRDB173","HMRTDB173","SESSDB311","ACMDB104","SIMIDXDB","FSNDB173","UARTDB287","RERTDB279","SFFDB28C","GCURDB","SLTBLRTDB","ID2MDN01","GTMDB28A"],"laststatus":"Dead","lasttime":1552614155131}
			// if(sdeptid.equals(deptid)||hasRole) {
			if(sdeptid.equals(deptid)) {
				// serversName.add(serverName);
				result.add(ServerMem);
			}
		}
		// caseSearchItemMap.put("servers",serversName);
		return result;
	}
	public Object searchCaseInfo(Map<String, Object> param ,String cond, String auth,String retrunType) throws Exception{
		
		String [] conds = cond.split(";");
		if("".equals(conds[0])){
			return "Please select a data source !";
		}
		dataBase = conds[0];
		String scope = "*";
		if(retrunType=="total") {
			scope = "count(1) rcount";
		}
		String sql = "select "+scope+" from "+conds[0]+" where 1=1 ";
		if(conds.length>1) {
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
			if(!"".equals(conds[11])){
				System.err.println("11 --> " + conds[11]);
				if("ANSI".equals(conds[11])) {
					sql += "and base_data like 'VzW%'";
				}else if ("ITU".equals(conds[12])) {
					sql += "and base_data not like 'VzW%'";
				}
			}
			if(!"".equals(conds[12])){
				System.err.println("12 --> " + conds[12]); // 12 --> SP29.16
				Map<String, List<String>> caseSearch = getCaseSearch();
				List<String> list = caseSearch.get("release");
				//System.err.println(caseSearch.get("release"));
				int indexOf = -1;
				if(list.contains(conds[12])) {
					indexOf = list.indexOf(conds[12]);
				}
				List<String> subList = new ArrayList<>();
				if(indexOf != -1) {
					subList = list.subList(0, indexOf);
				}
				if(subList.size()>0) {
					sql += "and (";
					//System.err.println(subList); // [SP29.12, SP29.13, SP29.14, SP29.15]
					for (String str : subList) {
						sql += "porting_release like '%" + str + "+%' or ";
					}
					sql = sql.substring(0, sql.length() -3) + " or `release` = '" + conds[12] + "' "
			                   +" or porting_release like '%" + conds[12] + "%')";
				}
			}
		}
		//System.err.println("caseName:+++++++["+param.get("caseName")+"]");
		if(""!=param.get("caseName")&&param.get("caseName")!=null) {
			sql += "and case_name='"+param.get("caseName")+"' ";
		}
		if(retrunType=="rows") {
			if(""!=param.get("offset")&& ""!=param.get("limit")){
				sql +=" limit "+param.get("offset")+","+param.get("limit");
			}
		}
		/*JdbcUtil jdbc = null;
		if(conds[0].equals(Constant.DAILYCASE)){
			jdbc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
		}else if(conds[0].equals(Constant.DFTTAG)){
			jdbc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB"));
		}*/
		KalieyMysqlUtil jdbc = KalieyMysqlUtil.getInstance();
		
		
		//query_sql:===select * from DailyCase where 1=1 and feature_number='731590' and case_name='null'  limit null,null
		System.err.println("query_sql:==="+sql);
		ArrayList<HashMap<String, Object>> query = caseSearchDaoImpl.query(jdbc, sql);
				//new ArrayList<HashMap<String, Object>>();
		
		/*if(!"".equals(conds[11]) && !"0".equals(conds[11])){
			query = caseSearchDaoImpl.query(jdbc, sql);
			if(retrunType=="insert") {
				if(query!=null && query.size()!=0){
					jdbc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
					String query_sql="select max(group_id) gid from toDistributeCases";
					String gid = caseSearchDaoImpl.query(jdbc, query_sql).get(0).get("gid").toString();
					JSONArray Servers = CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock,true, null);
					JSONArray spaList=new JSONArray();
					JSONArray rtdbList=new JSONArray();
					for (int i = 0; i < Servers.size(); i++) {
						JSONObject serverMem = Servers.getJSONObject(i).getJSONObject(Constant.LAB);
						String serverName = serverMem.getString(Constant.SERVERNAME);
						if(serverName.equals(conds[11])){
							spaList=serverMem.getJSONArray(Constant.SERVERSPA);
							rtdbList=serverMem.getJSONArray(Constant.SERVERRTDB);
						}
					}
					for(int i=0; i<query.size(); i++){
						String disSql="INSERT INTO toDistributeCases (case_name, lab_number, mate, special_data, base_data, second_data, release, porting_release, SPA, RTDB, server, customer, group_id) VALUES('"
								+query.get(i).get("case_name")+"', '"
								+query.get(i).get("lab_number")+"', '"
								+query.get(i).get("mate")+"', '"
								+query.get(i).get("special_data")+"', '"
								+query.get(i).get("base_data")+"', '"
								+query.get(i).get("second_data")+"', '"
								+query.get(i).get("release")+"', '"
								+query.get(i).get("porting_release")+"', '"
								+spaList.toString()+"', '"
								+rtdbList.toString()+"', '"
								+query.get(i).get("server")+"', '"
								+query.get(i).get("customer")+"', "
								+gid+");";
						System.out.println("disSql:="+disSql);
						caseSearchDaoImpl.insert(jdbc, disSql);
					}
				}
			}
		}else if(auth.equals("all")){
			sqlAdmin=sql;
			sqlAdmin+=" order by lab_number asc,special_data asc;";
			query = caseSearchDaoImpl.query(jdbc, sql);
		}*/
		if(retrunType=="rows" || retrunType=="condition") {
			return query;
		}
		if(retrunType=="total") {
			return query.get(0).get("rcount");
		}
		return "";//"Total record:"+query.size();
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
	/**
	 * <pre>
	 * Example: String insertToDistributeCasesTbl = caseSearchService.insertToDistributeCasesTbl(paramMap);
	 * Description: 虽然不知道什么原理，但是只要把case信息房到这张表里面就可以指定server运行
	 * 					1、将操作记录到一张日志表内方便后续跟踪
	 *                  2、将数据放到目标表内
	 * Arguments: 参数map
	 * Return: String
	 * Variable：null
	 * </pre>
	 * @throws Exception 
	 */
	public Map<String,Object> insertToDistributeCasesTbl(Map<String,Object> paramMap) throws Exception {
		
		Map<String,Object> returnMap = new HashMap<String,Object>();
		
		String ids = (String)paramMap.get("ids").toString();
		String condition = (String)paramMap.get("condition");
		String login = (String)paramMap.get("login");
		String formtitle = (String)paramMap.get("formtitle");
		// String title ="["+login+"]"+ids.split(",")[0]+"...";
		
		
		if(!"".equals(ids)&&!"".equals(condition)&&!"".equals(login)) {
			JSONArray Servers = CaseConfigurationCache.getSingletonCaseProperties(CaseConfigurationCache.lock);
			StringBuffer midString =new StringBuffer();
			String substring ="";
			if(!ids.contains(",")) {
				substring = midString.append("'"+ids+"'").toString();
			}else {
				String[] split = ids.split(",");
				for (String string : split) {
					midString.append("'"+string+"',");
				}
				int lastIndexOf = midString.lastIndexOf(",");
				substring = midString.substring(0, lastIndexOf);
			}
			System.out.println("substring============="+substring);
			
			String [] conds = condition.split(";");
			
			if("".equals(conds[0])){
				returnMap.put("msg", "Please select a data source !");
				returnMap.put("result", false);
				return returnMap;
			}
			
			/*
			 * 不做任何校验
			 * String csql = "select * from n_rerunning_case_tbl where stateflag='0' and case_info ='"+ids+"' order by datetime desc";
			System.out.println("csql:============="+csql);
			jdbc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
			ArrayList<HashMap<String,Object>> cquery = caseSearchDaoImpl.query(jdbc, csql);
			if(cquery.size()>0) {
				SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
				String date = (String) cquery.get(0).get("datetime");
				System.out.println("ddate:========="+date);
				Date ndate = new Date();
				long from = simpleFormat.parse(date).getTime();
				System.out.println("from:========"+from);
				long to = ndate.getTime();
				System.out.println("to:========"+to);
				int minutes = (int) ((to - from)/(1000 * 60));
				System.out.println("minutes:============"+minutes);
				Object object = cquery.get(0).get("server_info");
				JSONObject server_info = JSONObject.fromObject(object);
				String serverName = (String) server_info.get("serverName");
				System.out.println("serverName:===="+serverName );
				//如果数据库的server_info字段为空，那么下面的代码会报空指针异常java.lang.NullPointerException
				System.out.println("conds[11]:===="+conds[11]+"  :"+serverName.equals(conds[11]));
				if(minutes<=10 && serverName.equals(conds[11])) {
					returnMap.put("msg", "you checked case will be running in 10 minutes !");
					returnMap.put("result", false);
					return returnMap;
				}
			}*/
			
			
			/*String tsql ="select distinct case_name from toDistributeCases";
			ArrayList<HashMap<String,Object>> tquery = caseSearchDaoImpl.query(jdbc, tsql);
			String str = "";
			for (int i=0;i<tquery.size();i++) {
				if(i!=tquery.size()&&i!=0) {
					str=str+",";
				}
				String case_name = "'"+(String)tquery.get(i).get("case_name")+"'";
				str=str+case_name;
			}
			System.err.println("str:======="+str);*/
			/*JdbcUtil jdbc = null;
			if(conds[0].equals(Constant.DAILYCASE)){
				jdbc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
			}else if(conds[0].equals(Constant.DFTTAG)){
				jdbc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB"));
			}*/
			KalieyMysqlUtil jdbc = KalieyMysqlUtil.getInstance();
			dataBase = conds[0];
			String sql = "select * from "+conds[0]+" where 1=1 and case_name in ("+substring+")";// and case_name not in ("+str+")";
			System.out.println("sql+========="+sql);
			ArrayList<HashMap<String,Object>> query = caseSearchDaoImpl.query(jdbc, sql);
			if(query.size()==0) {
				returnMap.put("result", false);
				returnMap.put("msg", "you checked case will be running !");
				return returnMap;
			}
			
			/*String nsql = "select case_name from "+conds[0]+" where 1=1 and case_name in ("+substring+")";// and case_name in ("+str+")";
			System.out.println("nsql+========="+nsql);
			ArrayList<HashMap<String,Object>> nquery = caseSearchDaoImpl.query(jdbc, nsql);*/
			
			
			//jdbc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
			String query_sql="select ifnull(max(group_id),0)+1 gid from toDistributeCases;";
			String gid = caseSearchDaoImpl.query(jdbc, query_sql).get(0).get("gid").toString();
			
			String server = "";
			List serverList = new ArrayList<>();
			if(conds[13].contains(",")) {
				String[] servers = conds[13].split(",");
				server = server+"[";
				for (int i=0; i<servers.length;i++) {
					serverList.add(servers[i]);
					if(i==servers.length-1) {
						server = server+"\""+servers[i]+"\"";
					}else {
						server = server+"\""+servers[i]+"\",";
					}
				}
				server = server+"]";
			}else {
				serverList.add(conds[13]);
				server = server+"[\""+conds[13]+"\"]";
			}
			//此处代码只允许一个server的spa和rtdb
			//明显没有考虑多个server的情况
			//当前问题是如果取每个server的spa和rtdb的交集是否会有影响
			/*JSONObject serverMemDb = new JSONObject();
			JSONArray spaList=new JSONArray();
			JSONArray rtdbList=new JSONArray();
			for (int i = 0; i < Servers.size(); i++) {
				JSONObject serverMem = Servers.getJSONObject(i).getJSONObject(Constant.LAB);
				String serverName = serverMem.getString(Constant.SERVERNAME);
				if(serverName.equals(conds[11])){
					spaList=serverMem.getJSONArray(Constant.SERVERSPA);
					rtdbList=serverMem.getJSONArray(Constant.SERVERRTDB);
					serverMemDb=serverMem;
					System.err.println("serverMemDb:===="+serverMemDb);
				}
			}*/
			
			//[变更]将符合条件的case指定服务器运行 4需求修改<1>
			//4. check case & lab match status for case list before running.
		    //   for matched partial list, continue to execute;
		    //   for unmatched partial list, mark separate list
			
			Map<String,Object> serverMap = new HashMap<String,Object>();
			
			for (int z = 0; z < Servers.size(); z++) {
				Map<String,Object> serverAttr = new HashMap<String,Object>();
				JSONObject serverMem = Servers.getJSONObject(z).getJSONObject(Constant.LAB);
				String serverName = serverMem.getString(Constant.SERVERNAME);
				if(serverList.contains(serverName)){
					serverAttr.put("spaList", serverMem.getJSONArray(Constant.SERVERSPA));
					serverAttr.put("rtdbList", serverMem.getJSONArray(Constant.SERVERRTDB));
					serverMap.put(serverName, serverAttr);
				}
			}
			
			
			
			Map<String,Object> csMap = new HashMap<String,Object>();
			Map<String,Object> cfMap = new HashMap<String,Object>();
			for(int i=0; i<query.size(); i++){
				
				String dpendSql = "select * from CaseDepends where case_name='"+query.get(i).get("case_name")+"'";
				ArrayList<HashMap<String, Object>> dpendList = caseSearchDaoImpl.query(jdbc, dpendSql);
				String spa = "";
				String db = "";
				String t_spa ="[";
				String t_db="[";
				List l_spa = new ArrayList<>();
				List l_db = new ArrayList<>();
				
				//DIAMCL,ENWTPPS,NWTCOM,EPAY
				//["DIAMCL","ENWTPPS","NWTCOM","EPAY"]
				if(dpendList.size()>0) {
					spa = (String) dpendList.get(0).get("SPA");
					db = (String) dpendList.get(0).get("DB");
				}
				if(spa!=null&&!"".equals(spa)) {
					if(spa.contains(",")) {
						String[] split = spa.split(",");
						int j = 0;
						for (String str : split) {
							l_spa.add(str);
							if(j==split.length-1) {
								t_spa=t_spa + "\""+str+"\"]";
							}else {
								t_spa=t_spa + "\""+str+"\",";
							}
							j++;
						}
					}else {
						l_spa.add(spa);
						t_spa=t_spa + "\""+spa+"\"]";
					}
				}else {
					t_spa="";
				}
				if(db!=null&&!"".equals(db)) {
					if(db.contains(",")) {
						String[] split = db.split(",");
						int j = 0;
						for (String str : split) {
							l_db.add(str);
							if(j==split.length-1) {
								t_db=t_db + "\""+str+"\"]";
							}else {
								t_db=t_db + "\""+str+"\",";
							}
							j++;
						}
					}else {
						l_db.add(db);
						t_db=t_db + "\""+db+"\"]";
					}
				}else {
					t_db="";
				}
				
				//System.err.println("t_spa:===="+t_spa);
				//System.err.println("t_db:===="+t_db);
				
				//[变更]将符合条件的case指定服务器运行 4需求修改<2>
				//4. check case & lab match status for case list before running.
			    //   for matched partial list, continue to execute;
			    //   for unmatched partial list, mark separate list
				
				
				List csList = new ArrayList<>();
				List cfList = new ArrayList<>();
				for(String serverName :serverMap.keySet()) {
					Map<String,Object> serverAttr = (Map<String, Object>) serverMap.get(serverName);
					JSONArray spaArray = (JSONArray) serverAttr.get("spaList");
					JSONArray rtdbArray = (JSONArray) serverAttr.get("rtdbList");
					List spaList = JSONArray.toList(spaArray);
					List spaList2 = new ArrayList<>(); 
					for (Object object : spaList) {
						spaList2.add((""+object).trim().replaceAll("\\d+\\w?$", "").replace(".*", "").replace("RTDB ", "").toUpperCase());
					}
					List rtdbList = JSONArray.toList(rtdbArray);
					List rtdbList2 = new ArrayList<>(); 
					for (Object object : rtdbList) {
						rtdbList2.add((""+object).trim().replaceAll("\\d+\\w?$", "").replace(".*", "").replace("RTDB ", "").toUpperCase());
					}
					//满足条件的case
					System.err.println(spaList2.containsAll(l_spa));
					System.err.println(rtdbList2.containsAll(l_db));
					System.err.println(spaList2+"\n"+l_spa);
					System.err.println(rtdbList2+"\n"+l_db);
					/*if(spaList2.containsAll(l_spa) && rtdbList2.containsAll(l_db)) {
						csList.add("\""+serverName+"\"");
					}else {
						cfList.add(serverName+":"+spaList2.containsAll(l_spa)+":"+rtdbList2.containsAll(l_db));
					}*/
					//暂不check运行条件
					csList.add("\""+serverName+"\"");
				}
				if(csList.size()>0) {
					System.out.println("csList:========="+csList);
					csMap.put(query.get(i).get("case_name").toString(), csList);
					String disSql="replace into toDistributeCases (case_name, lab_number, mate, special_data, base_data, second_data, `release`, porting_release, SPA, RTDB, server, customer, group_id) VALUES('"
							+query.get(i).get("case_name")+"', '"
							+query.get(i).get("lab_number")+"', '"
							+query.get(i).get("mate")+"', '"
							+query.get(i).get("special_data")+"', '"
							+query.get(i).get("base_data")+"', '"
							+query.get(i).get("second_data")+"', '"
							+query.get(i).get("release")+"', '"
							+query.get(i).get("porting_release")+"', '"
							/*+spaList.toString()+"', '"
							+rtdbList.toString()+"', '"*/
							+t_spa+"', '"
							+t_db+"', '"
							+csList.toString().trim()+"', '"
							+query.get(i).get("customer")+"', "
							+gid+");";
					System.out.println("disSql:="+disSql);
					caseSearchDaoImpl.insert(jdbc, disSql);
				}else {
					cfMap.put(query.get(i).get("case_name").toString(), cfList);
				}
				//csMap:=-=-=-=--={731590/fr0589.json=[BJRMS21B, BJRMS21A, BJRMS21F, BJRMS21E, BJRMS21D, BJRMS21C]}
				System.err.println("csMap:=-=-=-=--="+csMap);
			}
			
			
			
			
			returnMap.put("s_case", csMap.size());
			returnMap.put("f_case", cfMap.size());
			int i=0;
			
			
			PreparedStatement ps = null;
			PreparedStatement ups = null;
			PreparedStatement sps = null;
			Connection conn = jdbc.getConnection();
			conn.setAutoCommit(false);
			
			while(i<10) {
				//lock.lock();
				String idsql="select ifnull(max(int_id),0)+1 max_id from n_rerunning_case_tbl";
				ArrayList<HashMap<String, Object>> idList = caseSearchDaoImpl.query(jdbc, idsql);
				String s_max_id = (String) idList.get(0).get("max_id");
				int max_id = 1;
				if(s_max_id!=null) {
					max_id = Integer.parseInt(s_max_id);
				}
				try {
					String isql ="insert into n_rerunning_case_tbl(int_id,title,server_info,`condition`,author,`datetime`) values("
							//+max_id+",'"+title +"','"+server+"','"+condition+"','"+login+"',datetime('now', 'localtime'))";
					+max_id+",'"+formtitle +"','"+server+"','"+condition+"','"+login+"',now())";
					System.out.println("isql+================="+isql);
					ps = conn.prepareStatement(isql);
					ps.execute();
					ps.close();
					
					
					
					String baksql="insert into n_case_bak_tbl(case_name,case_status,case_server,spa,db,rerunning_id) values(?,?,?,?,?,?)";
					ups = conn.prepareStatement(baksql);
					for (HashMap<String, Object> bMap : query) {
						String dpendSql = "select * from CaseDepends where case_name='"+(String)bMap.get("case_name")+"'";
						ArrayList<HashMap<String, Object>> dpendList = caseSearchDaoImpl.query(jdbc, dpendSql);
						String spa = "";
						String db = "";
						if(dpendList.size()>0) {
							spa = (String) dpendList.get(0).get("SPA");
							db = (String) dpendList.get(0).get("DB");
						}
						//判断是否是通过check的case
						String case_server="";
						for(String case_name :csMap.keySet()) {
							if(case_name==(String)bMap.get("case_name")&& case_name.equals((String)bMap.get("case_name"))) {
								case_server=csMap.get(case_name).toString();
							}
						}
						for(String case_name :cfMap.keySet()) {
							if(case_name==(String)bMap.get("case_name")&& case_name.equals((String)bMap.get("case_name"))) {
								case_server=cfMap.get(case_name).toString();
							}
						}
						
						
						ups.setString(1, (String)bMap.get("case_name"));
						ups.setString(2, (String)bMap.get("case_status"));
						ups.setString(3, case_server);
						ups.setString(4, spa);
						ups.setString(5, db);
						ups.setString(6, max_id+"");
						ups.addBatch();
					}
					ups.executeBatch();
					ups.close();
					
					
					
					String serverSql ="insert into n_case_server_tbl(rerunning_id,serverName,serverIp,serverRelease,serverProtocol,serverType,serverMate,mateServer,setName,serverSPA,serverRTDB) "
							+ "values(?,?,?,?,?,?,?,?,?,?,?) ";
					sps = conn.prepareStatement(serverSql);
					
					
					for (int j = 0; j < Servers.size(); j++) {
						JSONObject serverMem = Servers.getJSONObject(j).getJSONObject(Constant.LAB);
						String serverName = serverMem.getString(Constant.SERVERNAME);
						boolean flag =false;
						if(conds[11].contains(",")) {
							String[] servers = conds[11].split(",");
							List<String> list=Arrays.asList(servers);
							if(list.contains(serverName)) {
								flag=true;
							}
						}else {
							if(serverName.equals(conds[11])) {
								flag=true;
							}
						}
						
						if(flag){
							sps.setString(1, max_id+"");
							sps.setString(2, (String)serverMem.get("serverName"));
							sps.setString(3, (String)serverMem.get("serverIp"));
							sps.setString(4, (String)serverMem.get("serverRelease"));
							sps.setString(5, (String)serverMem.get("serverProtocol"));
							sps.setString(6, (String)serverMem.get("serverType"));
							sps.setString(7, (String)serverMem.get("serverMate"));
							sps.setString(8, (String)serverMem.get("mateServer"));
							sps.setString(9, (String)serverMem.get("setName"));
							sps.setString(10, serverMem.get("serverSPA").toString());
							sps.setString(11, serverMem.get("serverRTDB").toString());
							sps.addBatch();
						}
					}
					sps.executeBatch();
					sps.close();
					
					
					
					
					
					
					conn.commit();
					returnMap.put("result", true);
					break;
				} catch (Exception e) {
					e.printStackTrace();
					conn.rollback();
					i++;
					Thread.sleep(1000);
				}finally {
					conn.close();
					//lock.unlock();
				}
			}
			
		}
		return returnMap;
	}
	public Object searchCaseRunLogInfo(Map<String, Object> param, String auth, String retrunType, List<String> deptids,Boolean adminFlag) throws Exception {
		
		/*String sql ="select int_id,title,server_info,condition,author,datetime from n_rerunning_case_tbl where stateflag='0' order by datetime desc";*/
		/*String dept_ids = "";
		if(adminFlag) {
			String dsql = "select id deptid from n_dept where stateflag=0";
			ArrayList<HashMap<String, Object>> queryDept = caseSearchDaoImpl.query(jdbc, dsql);
			List<String> allDeptids= new ArrayList<String>();
			if(queryDept.size()>0) {
				for (HashMap<String, Object> hashMap : queryDept) {
					String deptid = ""+hashMap.get("deptid");
					if(deptid!=null && !"".equals(deptid)) {
						allDeptids.add(deptid);
					}
				}
			}
			dept_ids = StringUtil.formatSplitList(allDeptids);
		}else {
			dept_ids = StringUtil.formatSplitList(deptids);
		}*/
		String sql ="select distinct a.int_id,a.title,a.server_info,a.condition,a.author,a.datetime from n_rerunning_case_tbl a " + 
				"left join n_user b on a.author=b.username and b.stateflag='0' " + 
				"left join n_user_dept c on b.id=c.user_id  and c.stateflag='0' " + 
				"where a.stateflag='0' "; 
		if(!adminFlag) {
			String dept_ids = StringUtil.formatSplitList(deptids);
			sql =sql +"and c.dept_id in("+dept_ids+") ";
		}
			sql =sql +"order by a.datetime desc ";
		/*if(auth=="all") {
			
		}*/
		System.out.println("searchCaseRunLogInfo  >>  "+sql);
		if(retrunType=="rows") {
			if(""!=param.get("offset")&& ""!=param.get("limit")){
				sql +=" limit "+param.get("offset")+","+param.get("limit");
			}
		}
		//JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
		KalieyMysqlUtil jdbc = KalieyMysqlUtil.getInstance();
		ArrayList<HashMap<String, Object>> query = caseSearchDaoImpl.query(jdbc, sql);
		if(retrunType=="rows") {
			return query;
		}
		if(retrunType=="total") {
			return query.size()+"";
		}
		return "";//"Total record:"+query.size();
	}
	public ArrayList<HashMap<String, Object>> searchCaseRunLogInfoById(Map<String, Object> param) throws Exception {
		String sql ="select *  from n_rerunning_case_tbl where stateflag='0' and int_id= "+param.get("int_id");
		//JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
		KalieyMysqlUtil jdbc = KalieyMysqlUtil.getInstance();
		ArrayList<HashMap<String, Object>> query = caseSearchDaoImpl.query(jdbc, sql);
		return query;
	}
	public ArrayList<HashMap<String, Object>> searchCaseRunLogCaseInfoById(Map<String, Object> param) throws Exception {
		
		Boolean case_status = (Boolean)param.get("case_status");
		String sql = "";
		if(case_status) {
			sql ="select * from n_case_bak_tbl where stateflag='0' and rerunning_id= "+param.get("int_id") +" and case_server not like '%:%'";
		}else {
			sql ="select * from n_case_bak_tbl where stateflag='0' and rerunning_id= "+param.get("int_id") +" and case_server like '%:%'";
		}
		System.err.println(sql);
		//JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
		KalieyMysqlUtil jdbc = KalieyMysqlUtil.getInstance();
		ArrayList<HashMap<String, Object>> query = caseSearchDaoImpl.query(jdbc, sql);
		ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
		String case_str = "";
		if(query.size()>0) {
			for (HashMap<String, Object> hashMap : query) {
				String case_name = (String) hashMap.get("case_name");
				case_str=case_str+"'"+case_name+"',";
			}
			int lastIndexOf = case_str.lastIndexOf(",");
			String substring = case_str.substring(0, lastIndexOf);
			System.err.println(substring);
			//jdbc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB"));
			sql ="select * from DftTag a left join CaseDepends b on a.case_name= b.case_name where a.case_name in ("+substring+")";
			arrayList = caseSearchDaoImpl.query(jdbc, sql);
			
		}
		for (HashMap<String, Object> hashMap : arrayList) {
			for (HashMap<String, Object> qMap : query) {
				if(qMap.get("case_name").equals(hashMap.get("case_name"))) {
					hashMap.put("per_case_status", qMap.get("case_status"));
					hashMap.put("spa", qMap.get("spa"));
					hashMap.put("db", qMap.get("db"));
					String case_server = (String)qMap.get("case_server");
					hashMap.put("case_server",case_server);
				}
			}
		}
		return arrayList;
	}
	public ArrayList<HashMap<String, Object>> searchCaseRunLogCaseServerById(Map<String, Object> param) throws Exception {
		String sql ="select * from kaliey.n_case_server_tbl where stateflag='0' and rerunning_id= "+param.get("int_id");
		//JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
		KalieyMysqlUtil jdbc = KalieyMysqlUtil.getInstance();
		ArrayList<HashMap<String, Object>> query = caseSearchDaoImpl.query(jdbc, sql);
		return query;
	}

	public void onlyrun(String set, String server,String title,String login,String only, String flag, String condition) throws Exception {
		// 第零步，判断是否是search传进来的case,如果是有一种特殊情况，全选flag选中后
		if(flag!="") {
			
		} else {
			
		}
		// 第一步，校验数据是否在数据库中，确认不是胡乱传的
		if (!"".equals(set)) {
			String[] split = set.split(",");
			String casesql = "";
			for (String string : split) {
				casesql = casesql +"'"+string+"',";
			}
			int len = split.length;
			String cases = casesql.substring(0, casesql.length()-1);
			//System.out.println(cases); '72624/fn3916.json','72624/fn3917.json','72624/fn3918.json','72624/fn3919.json'
			boolean checkCaseInfo = checkCaseInfo(cases,len);
			if(checkCaseInfo) {
				// 第二步，将数据存入日志记录表
				int caseLogId = insertCaseLog(title,server,login);
				if (caseLogId!=-1) {
					List<List> params = new ArrayList<>();
					for (String casename : split) {
						List list = new ArrayList<>();
						list.add(casename);
						list.add(only);
						list.add(server);
						list.add(login);
						list.add("now()");
						list.add(caseLogId);
						params.add(list);
					}
					// 第三步，将数据传入正式运行表中
					InsertTempRunCase(params);
				}
			}
		}
	}
	public boolean checkCaseInfo(String set,int len) throws Exception {
		boolean result = false;
		String sql ="select * from cases_info_db.case_tag where case_name in ("+set+")";
		KalieyMysqlUtil jdbc = KalieyMysqlUtil.getInstance();
		ArrayList<HashMap<String, Object>> query = caseSearchDaoImpl.query(jdbc, sql);
		if(query.size()==len) {
			result = true;
		}
		return result;
	}
	public int insertCaseLog(String title,String server,String login) throws Exception {
		KalieyMysqlUtil jdbc = KalieyMysqlUtil.getInstance();
		String isql ="insert into n_rerunning_case_tbl(title,server_info,`condition`,author,`datetime`) values('"+title +"','"+server+"','','"+login+"',now())";
		int id = jdbc.executeSqlReturnId(isql);
		return id;
	}
	public void insertCaseServer(List<List> params) throws SQLException {
		KalieyMysqlUtil jdbc = KalieyMysqlUtil.getInstance();
		String serverSql ="insert into n_case_server_tbl(rerunning_id,serverName,serverIp,serverRelease,serverProtocol,serverType,serverMate,mateServer,setName,serverSPA,serverRTDB) "
				+ "values(?,?,?,?,?,?,?,?,?,?,?) ";
		jdbc.executeBatch(serverSql,params);
	}
	public void insertCaseBak(List<List> params) throws SQLException {
		KalieyMysqlUtil jdbc = KalieyMysqlUtil.getInstance();
		String baksql="insert into n_case_bak_tbl(case_name,case_status,case_server,spa,db,rerunning_id) values(?,?,?,?,?,?)";
		jdbc.executeBatch(baksql,params);
	}
	public void InsertTempRunCase(List<List> params) throws Exception {
		KalieyMysqlUtil jdbc = KalieyMysqlUtil.getInstance();
		String isql ="insert into temp_run_case(case_name,only_run_flag,target_labs,submit_owner,submit_date,batch_id) "
				+ "values(?,?,?,?,?,?)";
		jdbc.executeBatch(isql,params);
	}
	@Test
	public void test() throws Exception {
		CaseSearchService cs = new CaseSearchService();
		cs.onlyrun("72624/fn3916.json,72624/fn3917.json,72624/fn3918.json,72624/fn3919.json","testserver","testtitle","root","Y","","");
	}
}
