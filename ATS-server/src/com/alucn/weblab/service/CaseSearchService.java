package com.alucn.weblab.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.alucn.weblab.model.Case;

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
	public Object searchCaseInfo(Map<String, Object> param ,String cond, String auth,String retrunType) throws Exception{
		
		String [] conds = cond.split(";");
		if("".equals(conds[0])){
			return "Please select a data source !";
		}
		dataBase = conds[0];
		String sql = "select * from "+conds[0]+" where 1=1 ";
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
		if(""!=param.get("caseName")) {
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
		ArrayList<HashMap<String, Object>> query = new ArrayList<HashMap<String, Object>>();
		
		if(!"".equals(conds[11]) && !"0".equals(conds[11])){
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
		}
		if(retrunType=="rows") {
			return query;
		}
		if(retrunType=="total") {
			return query.size()+"";
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
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
		
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
			
			String csql = "select * from n_rerunning_case_tbl where stateflag='0' and case_info ='"+ids+"' order by datetime desc";
			System.out.println("csql:============="+csql);
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
				if(minutes<=10) {
					returnMap.put("msg", "you checked case will be running in 10 minutes !");
					returnMap.put("result", false);
					return returnMap;
				}
			}
			
			dataBase = conds[0];
			String sql = "select * from "+conds[0]+" where 1=1 and case_name in ("+substring+") and case_name not in ("
					+ "select distinct case_name from toDistributeCases)";
			System.out.println("sql+========="+sql);
			ArrayList<HashMap<String,Object>> query = caseSearchDaoImpl.query(jdbc, sql);
			if(query.size()==0) {
				returnMap.put("result", false);
				returnMap.put("msg", "you checked case will be running !");
				return returnMap;
			}
			
			String nsql = "select case_name from "+conds[0]+" where 1=1 and case_name in ("+substring+") and case_name in ("
					+ "select distinct case_name from toDistributeCases)";
			System.out.println("nsql+========="+nsql);
			ArrayList<HashMap<String,Object>> nquery = caseSearchDaoImpl.query(jdbc, nsql);
			
			
			jdbc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
			String query_sql="select max(group_id) gid from toDistributeCases";
			String gid = caseSearchDaoImpl.query(jdbc, query_sql).get(0).get("gid").toString();
			
			
			JSONArray spaList=new JSONArray();
			JSONArray rtdbList=new JSONArray();
			String server = "[\""+conds[11]+"\"]";
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
						+server+"', '"
						+query.get(i).get("customer")+"', "
						+gid+");";
				System.out.println("disSql:="+disSql);
				caseSearchDaoImpl.insert(jdbc, disSql);
				
			}
			returnMap.put("s_case", query.size());
			returnMap.put("f_case", nquery.size());
			
			String isql ="insert into n_rerunning_case_tbl(title,case_info,condition,server_info,s_case,f_case,author,datetime) values('"
					+title +"','"+ids+"','"+condition+"','"+Servers.toString()+"','"+query.toString()+"','"+nquery.toString()+"','"+login+"',datetime('now', 'localtime'))";
			System.out.println("isql+================="+isql);
			caseSearchDaoImpl.insert(jdbc, isql);
			
			returnMap.put("result", true);
		}
		
		return returnMap;
	}
	public Object searchCaseRunLogInfo(Map<String, Object> param, String auth, String retrunType) throws Exception {
		String sql ="select int_id,title,condition,author,datetime, case when s_case=='[]' then '0' else '1' end s_case, case when f_case =='[]' then '0' else '1' end f_case  from n_rerunning_case_tbl where stateflag='0' order by datetime desc";
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
