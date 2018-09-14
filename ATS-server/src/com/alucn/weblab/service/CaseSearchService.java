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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
	//private Lock lock = new ReentrantLock(true);
	
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
		JdbcUtil jdbc = null;
		if(conds[0].equals(Constant.DAILYCASE)){
			jdbc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
		}else if(conds[0].equals(Constant.DFTTAG)){
			jdbc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB"));
		}
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
		String title ="["+login+"]"+ids.split(",")[0]+"...";
		
		
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
			JdbcUtil jdbc = null;
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
			if(conds[0].equals(Constant.DAILYCASE)){
				jdbc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
			}else if(conds[0].equals(Constant.DFTTAG)){
				jdbc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB"));
			}
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
			
			
			jdbc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
			String query_sql="select max(group_id) gid from toDistributeCases";
			String gid = caseSearchDaoImpl.query(jdbc, query_sql).get(0).get("gid").toString();
			
			
			
			String server = "";
			List serverList = new ArrayList<>();
			if(conds[11].contains(",")) {
				String[] servers = conds[11].split(",");
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
				serverList.add(conds[11]);
				server = server+"[\""+conds[11]+"\"]";
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
					List rtdbList = JSONArray.toList(rtdbArray);
					//满足条件的case
					System.err.println(spaList.containsAll(l_spa));
					System.err.println(rtdbList.containsAll(l_db));
					System.err.println(spaList+"\n"+l_spa);
					System.err.println(rtdbList+"\n"+l_db);
					if(spaList.containsAll(l_spa) && rtdbList.containsAll(l_db)) {
						csList.add("\""+serverName+"\"");
					}else {
						cfList.add(serverName+"(check_spa:"+spaList.containsAll(l_spa)+"|check_db:"+rtdbList.containsAll(l_db)+")");
					}
				}
				if(csList.size()>0) {
					System.out.println("csList:========="+csList);
					csMap.put(query.get(i).get("case_name").toString(), csList);
					String disSql="replace into toDistributeCases (case_name, lab_number, mate, special_data, base_data, second_data, release, porting_release, SPA, RTDB, server, customer, group_id) VALUES('"
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
					//caseSearchDaoImpl.insert(jdbc, disSql);
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
				String idsql="select max(int_id)+1 max_id from n_rerunning_case_tbl";
				ArrayList<HashMap<String, Object>> idList = caseSearchDaoImpl.query(jdbc, idsql);
				String s_max_id = (String) idList.get(0).get("max_id");
				int max_id = 1;
				if(s_max_id!=null) {
					max_id = Integer.parseInt(s_max_id);
				}
				try {
					String isql ="insert into n_rerunning_case_tbl(int_id,title,server_info,condition,author,datetime) values("
							+max_id+",'"+title +"','"+server+"','"+condition+"','"+login+"',datetime('now', 'localtime'))";
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
	public Object searchCaseRunLogInfo(Map<String, Object> param, String auth, String retrunType) throws Exception {
		String sql ="select int_id,title,server_info,condition,author,datetime from n_rerunning_case_tbl where stateflag='0' order by datetime desc";
		/*if(auth=="all") {
			
		}*/
		if(retrunType=="rows") {
			if(""!=param.get("offset")&& ""!=param.get("limit")){
				sql +=" limit "+param.get("offset")+","+param.get("limit");
			}
		}
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
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
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
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
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
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
			jdbc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB"));
			sql ="select * from DftTag where case_name in ("+substring+")";
			arrayList = caseSearchDaoImpl.query(jdbc, sql);
		}
		for (HashMap<String, Object> hashMap : arrayList) {
			for (HashMap<String, Object> qMap : query) {
				if(qMap.get("case_name").equals(hashMap.get("case_name"))) {
					hashMap.put("per_case_status", qMap.get("case_status"));
					String case_server = (String)qMap.get("case_server");
					hashMap.put("case_server",case_server);
				}
			}
		}
		return arrayList;
	}
	public ArrayList<HashMap<String, Object>> searchCaseRunLogCaseServerById(Map<String, Object> param) throws Exception {
		String sql ="select * from n_case_server_tbl where stateflag='0' and rerunning_id= "+param.get("int_id");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
		ArrayList<HashMap<String, Object>> query = caseSearchDaoImpl.query(jdbc, sql);
		return query;
	}
	
	
	/*public ArrayList searchCaseRunLogInfoById2(Map<String, Object> param) throws Exception {
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc =  new JdbcUtil(Constant.DATASOURCE, dbFile);
		Connection conn =  jdbc.getConnection();
		String sql ="select *  from n_rerunning_case_tbl where stateflag='0' and int_id= "+param.get("int_id");
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet resultSet = ps.executeQuery();
		ps.close();
		ArrayList populate = Populate(resultSet,Case.class);
		return populate;
	}
	*//**
     * 将结果集转换成实体对象集合
     * @param res 结果集
     * @param c 实体对象映射类
     * @return
    * @throws SQLException
    * @throws IllegalAccessException
    * @throws InstantiationException
     *//*
    public static ArrayList Populate(ResultSet rs,Class cc) throws SQLException, InstantiationException, IllegalAccessException{
        
        //结果集 中列的名称和类型的信息
        ResultSetMetaData rsm = rs.getMetaData();
        int colNumber = rsm.getColumnCount();
        ArrayList list = new ArrayList();
        Field[] fields = cc.getDeclaredFields();
        
        //遍历每条记录
        while(rs.next()){
            //实例化对象
            Object obj = cc.newInstance();
            //取出每一个字段进行赋值
            for(int i=1;i<=colNumber;i++){
                Object value = rs.getObject(i);
                //匹配实体类中对应的属性
                for(int j = 0;j<fields.length;j++){
                    Field f = fields[j];
                    if(f.getName().equals(rsm.getColumnName(i))){
                        boolean flag = f.isAccessible();
                        f.setAccessible(true);
                        f.set(obj, value);
                        f.setAccessible(flag);
                        break;
                    }
                }
                 
            }
            list.add(obj);
        }
        return list;
    }*/
}
