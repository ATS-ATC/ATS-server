package com.alucn.weblab.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.weblab.dao.impl.QueryCaseDaoImpl;

@Service("queryCaseInfoService")
public class QueryCaseInfoService {
	
	@Autowired(required=true)
	private QueryCaseDaoImpl queryCaseDaoImpl;
	
	public ArrayList<HashMap<String, Object>> getQueryCaseInfoTable(String userName, String auth,String caseStatus,String feature,String offset,String limit,String etype) throws Exception{
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String sql="";
		if("all".equals(etype)) {
			sql =sql+ "select * from DftTag where 1=1";
		}
		else if ("current".equals(etype)) {
			sql =sql+ "select feature_number,case_name,author,release,code_changed_spa,functionality,base_data,case_status,call_type,customer,porting_release from DftTag where 1=1";
		}
		if(!auth.equals(Constant.AUTH)){
			sql = sql+" and author='"+userName+"'";
		}
		if(!"".equals(feature)) {
			sql = sql+" and feature_number like '%"+feature+"%'";
		}
		sql = sql+" and case_status='"+caseStatus+"'";
		if(!"".equals(limit)) {
			sql = sql+" limit "+offset+","+limit;
		}
		System.out.println("sql==========="+sql);
		ArrayList<HashMap<String, Object>> result = queryCaseDaoImpl.query(jdbc, sql);
		return result;
	}
	public int getQueryCaseInfoTableCount(String userName, String auth,String caseStatus,String feature) throws Exception{
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String sql = "select count(1) from DftTag where 1=1";
		if(!auth.equals(Constant.AUTH)){
			sql = sql+" and author='"+userName+"'";
		}
		if(!"".equals(feature)) {
			sql = sql+" and feature_number like '%"+feature+"%'";
		}
		sql = sql+" and case_status='"+caseStatus+"'";
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
