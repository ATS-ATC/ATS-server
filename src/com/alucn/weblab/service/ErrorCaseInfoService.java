package com.alucn.weblab.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.weblab.dao.impl.ErrorCaseDaoImpl;
/**
 * @author haiqiw
 * 2017年6月23日 下午2:02:28
 * desc:ErrorCaseInfoService
 */
@Service("errorCaseInfoService")
public class ErrorCaseInfoService {
	
	@Autowired(required=true)
	private ErrorCaseDaoImpl errorCaseDaoImpl;
	private Map<String, String> errroCases;
	private Map<String, String>failedReason;
	 
	public Map<String, String> getErrorCaseInfo(String userName) throws Exception{
		errroCases = new HashMap<String, String>();
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("DailyCaseDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String getFeatureOfUser = "SELECT DISTINCT feature_number FROM DailyCase WHERE author='"+userName+"'";
		ArrayList<HashMap<String, Object>> result = errorCaseDaoImpl.query(jdbc, getFeatureOfUser);
		for(int i=0; i<result.size();i++){
			Map<String, Object> obj = result.get(i);
			for(String key: obj.keySet()){
				String featureNum = (String)obj.get(key);
				String getCaseName = "SELECT case_name FROM DailyCase WHERE author='"+userName+"' AND feature_number='" + featureNum+"' AND case_status='F';";
				ArrayList<HashMap<String, Object>> resultCaseName = errorCaseDaoImpl.query(jdbc, getCaseName);
				if(resultCaseName.size()>0){
					errroCases.put(featureNum, String.valueOf(resultCaseName.size()));
				}
			}
		}
		return errroCases;
	}
	
	public ArrayList<HashMap<String, Object>> getErrorCaseInfo(String featureName, String author) throws Exception{
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String getErrorCase = "SELECT casename, err_reason FROM errorcaseinfo WHERE fature='"+featureName+"' and owner='"+author+"';";
		ArrayList<HashMap<String, Object>> result = errorCaseDaoImpl.query(jdbc, getErrorCase);
		return result;
	}
	
	public ArrayList<HashMap<String, Object>> getErrorCaseReason() throws Exception{
		failedReason = new HashMap<String, String>();
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String getErrorType = "SELECT error_tpye, err_mark FROM caseerrortype;";
		ArrayList<HashMap<String, Object>> result = errorCaseDaoImpl.query(jdbc, getErrorType);
		for(int i=0; i<result.size();i++){
			Map<String, Object> obj = result.get(i);
			for(String key: obj.keySet()){
				failedReason.put((String)obj.get(key), null);
			}
		}
		return result;
	}
	
	public void setMarkCase(String userName, String featureName, String errorcases, String failedreasons) throws Exception{
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		Date now = new Date(); 
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String curDate= dateFormat.format(now);
		for(String acase : errorcases.split(",")){
			String markCaseSql = "UPDATE  errorcaseinfo SET err_reason='"+failedreasons+"', mark_date='"+curDate+"' WHERE casename='"+ acase +"' AND owner='"+ userName + "' AND fature='"+featureName.trim()+"';";
			errorCaseDaoImpl.insert(jdbc, markCaseSql);
		}
	}
}
