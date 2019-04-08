package com.alucn.weblab.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.weblab.dao.impl.QueryCaseDaoImpl;
import com.alucn.weblab.utils.KalieyMysqlUtil;

@Service("queryCaseInfoService")
public class QueryCaseInfoService {
	
	@Autowired(required=true)
	private QueryCaseDaoImpl queryCaseDaoImpl;
	
	private KalieyMysqlUtil jdbc = KalieyMysqlUtil.getInstance();
	
	/*public ArrayList<HashMap<String, Object>> getQueryCaseInfoTable(String userName,String caseStatus,String feature,String offset,String limit,String etype) throws Exception{
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		Class.forName("org.sqlite.JDBC");
		Connection connection = null;
		Statement state = null;
		String CaseInfoDB = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		connection = DriverManager.getConnection("jdbc:sqlite:" + CaseInfoDB);
		state = connection.createStatement();
		state.execute("ATTACH DATABASE '"+CaseInfoDB+"' AS attCaseInfo");
		String sql="";
		if("all".equals(etype)) {
			sql =sql+ "select * from DftTag a left join attCaseInfo.DailyCase b on a.case_name=b.case_name where 1=1";
		}
		else if ("current".equals(etype)) {
			//sql =sql+ "select feature_number,case_name,author,release,code_changed_spa,functionality,base_data,case_status,call_type,customer,porting_release from DftTag where 1=1";
			sql =sql+ "select a.case_name,a.release,a.customer,a.base_data,a.mate,a.lab_number,a.special_data, " + 
					"a.porting_release,a.service,a.call_type,a.code_changed_spa,a.functionality,a.feature_number, " + 
					"a.author,a.case_status,a.case_level,a.case_cost,a.time_sensitive,b.submit_date " + 
					"from DftTag a " + 
					"left join attCaseInfo.DailyCase b on a.case_name=b.case_name " + 
					"where 1=1";
		}
		if(!auth.equals(Constant.AUTH)){
			sql = sql+" and author='"+userName+"'";
		}
		if(!"".equals(feature)) {
			sql = sql+" and a.feature_number like '%"+feature+"%'";
		}
		sql = sql+" and a.case_status='"+caseStatus+"'";
		if(!"".equals(limit)) {
			sql = sql+" limit "+offset+","+limit;
		}
		System.out.println("sql==========="+sql);
		ArrayList<HashMap<String, Object>> result = queryCaseDaoImpl.query(jdbc, sql);
		return result;
	}*/
	public ArrayList<HashMap<String,Object>> getQueryCaseInfoTableNew(String userName,String caseStatus,String feature,String offset,String limit,String etype, String sort, String sortOrder, String mate, String lab) throws Exception{
		ArrayList<HashMap<String,Object>> list = new ArrayList();
		String sort_new = sort;
		if("hodingduration".equals(sort)) {
			sort_new="submit_date";
		}
		//Class.forName("org.sqlite.JDBC");
		//Connection connection = null;
		//Statement state = null;
		//Connection connection_DftCaseDB = null;
		//Statement state_DftCaseDB = null;
		//String CaseInfoDB = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		//String DftCaseDB = ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB");
		//connection = DriverManager.getConnection("jdbc:sqlite:" + CaseInfoDB);
		//connection_DftCaseDB = DriverManager.getConnection("jdbc:sqlite:" + DftCaseDB);
		//state = connection.createStatement();
		//state_DftCaseDB = connection_DftCaseDB.createStatement();
		//System.err.println("ATTACH DATABASE '"+CaseInfoDB+"' AS attCaseInfo");
		//state_DftCaseDB.execute("ATTACH DATABASE '"+CaseInfoDB+"' AS attCaseInfo");
		String sql="";
		if("all".equals(etype)) {
			sql =sql+ "select a.*,b.submit_date from DftTag a left join DailyCase b on a.case_name=b.case_name where 1=1";
		}
		else if ("current".equals(etype)) {
			//sql =sql+ "select feature_number,case_name,author,release,code_changed_spa,functionality,base_data,case_status,call_type,customer,porting_release from DftTag where 1=1";
			sql =sql+ "select a.case_name,a.release,a.customer,a.base_data,a.mate,a.lab_number,a.special_data, " + 
					"a.porting_release,a.service,a.call_type,a.code_changed_spa,a.functionality,a.feature_number, " + 
					"a.author,a.case_status,a.case_level,a.case_cost,a.time_sensitive,b.submit_date " + 
					"from DftTag a " + 
					"left join DailyCase b on a.case_name=b.case_name " + 
					"where 1=1";
		}
		/*if(!auth.equals(Constant.AUTH)){
			sql = sql+" and author='"+userName+"'";
		}*/
		if(!"".equals(feature)) {
			sql = sql+" and a.feature_number like '%"+feature+"%'";
		}
		if(!"".equals(mate)) {
			sql = sql+" and a.mate = '"+mate+"'";
		}
		if(!"".equals(lab)) {
			sql = sql+" and a.lab_number = '"+lab+"'";
		}
		sql = sql+" and a.case_status='"+caseStatus+"'";
		if(sort_new!=null && !"".equals(sort_new)) {
			sql=sql+" order by "+sort_new+" "+sortOrder;
		}
		if(!"".equals(limit)) {
			sql = sql+" limit "+offset+","+limit;
		}
		System.out.println("sql==========="+sql);
		ArrayList<HashMap<String, Object>> result = queryCaseDaoImpl.query(jdbc, sql);
		//ResultSet resultSet = state_DftCaseDB.executeQuery(sql);
		
		/*ResultSetMetaData md = resultSet.getMetaData(); //获得结果集结构信息,元数据
		int columnCount = md.getColumnCount();   //获得列数 
		while (resultSet.next()) {
			HashMap<String,Object> rowData = new HashMap<String,Object>();
			for (int i = 1; i <= columnCount; i++) {
				rowData.put(md.getColumnName(i), resultSet.getObject(i));
			}
			list.add(rowData);
		}
		System.err.println("list >> "+list);*/
		return result;
	}
	
	
	
	public int getQueryCaseInfoTableCount(String userName,String caseStatus,String feature, String mate, String lab) throws Exception{
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		String sql = "select count(1) from DftTag a where 1=1";
		/*if(!auth.equals(Constant.AUTH)){
			sql = sql+" and author='"+userName+"'";
		}*/
		if(!"".equals(feature)) {
			sql = sql+" and a.feature_number like '%"+feature+"%'";
		}
		if(!"".equals(mate)) {
			sql = sql+" and a.mate = '"+mate+"'";
		}
		if(!"".equals(lab)) {
			sql = sql+" and a.lab_number = '"+lab+"'";
		}
		sql = sql+" and a.case_status='"+caseStatus+"'";
		ArrayList<HashMap<String, Object>> result = queryCaseDaoImpl.query(jdbc, sql);
		int totle =0;
		for(int i=0; i<result.size();i++){
			for(String key: result.get(i).keySet()){
				totle = Integer.parseInt(result.get(i).get(key).toString().trim());
			}
		}
		return totle;
	}
	
	
}
