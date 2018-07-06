package com.alucn.weblab.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.util.DateUtil;
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
//	private Map<String, String>failedReason;
	 
	public Map<String, String> getErrorCaseInfo(String userName, String auth) throws Exception{
		errroCases = new HashMap<String, String>();
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String getFeatureOfUser = "SELECT DISTINCT feature FROM errorcaseinfo WHERE 1=1";
		if(!auth.equals(Constant.AUTH)){
			getFeatureOfUser = getFeatureOfUser+" and owner='"+userName+"'";
		}
		ArrayList<HashMap<String, Object>> result = errorCaseDaoImpl.query(jdbc, getFeatureOfUser);
		for(int i=0; i<result.size();i++){
			Map<String, Object> obj = result.get(i);
			for(String key: obj.keySet()){
				String featureNum = (String)obj.get(key);
				String getCaseName = "SELECT casename FROM errorcaseinfo WHERE feature='"+ featureNum+"'";
				ArrayList<HashMap<String, Object>> resultCaseName = errorCaseDaoImpl.query(jdbc, getCaseName);
				if(resultCaseName.size()>0){
					errroCases.put(featureNum, String.valueOf(resultCaseName.size()));
				}
			}
		}
		return errroCases;
	}
	/**
	 * 
	 * <pre>
	 * 例子: errorCaseInfoService.getErrorCaseInfoTable("root","all");
	 * 描述: 用于获取feature与error case数量
	 * 参数: 
	 * 	1、用户名
	 * 	2、权限
	 * 返回: ArrayList<HashMap<String, Object>>
	 * 涉及变量：
	 * </pre>
	 */
	public ArrayList<HashMap<String, Object>> getErrorCaseInfoTable(String userName, String auth) throws Exception{
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String sql = "select feature,owner,servername,count(casename) fcount  from errorcaseinfo where 1=1";
		if(!auth.equals(Constant.AUTH)){
			sql = sql+" and owner='"+userName+"'";
		}
		sql = sql+" group by feature,owner,servername";
		ArrayList<HashMap<String, Object>> result = errorCaseDaoImpl.query(jdbc, sql);
		return result;
	}
	public ArrayList<HashMap<String, Object>> getErrorCaseInfo(String featureName, String author, String auth) throws Exception{
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String getErrorCase = "SELECT casename, err_reason, err_desc FROM errorcaseinfo WHERE 1=1 and feature='"+featureName+"'";
		if(!auth.equals(Constant.AUTH)){
			getErrorCase = getErrorCase+" and owner='"+author+"'";
		}
		ArrayList<HashMap<String, Object>> result = errorCaseDaoImpl.query(jdbc, getErrorCase);
		return result;
	}
	
	public ArrayList<HashMap<String, Object>> getErrorCaseReason() throws Exception{
//		failedReason = new HashMap<String, String>();
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String getErrorType = "SELECT error_tpye, err_mark FROM caseerrortype;";
		ArrayList<HashMap<String, Object>> result = errorCaseDaoImpl.query(jdbc, getErrorType);
//		for(int i=0; i<result.size();i++){
//			Map<String, Object> obj = result.get(i);
//			for(String key: obj.keySet()){
//				failedReason.put((String)obj.get(key), null);
//			}
//		}
		return result;
	}
	
	public ArrayList<HashMap<String, Object>> getErrorCaseReasonHis() throws Exception{
//		failedReason = new HashMap<String, String>();
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String getErrorType = "select casename, feature, err_reason, owner, insert_date, mark_date, email_date, servername, err_desc,max(mark_date) from errorcaseinfoHistory where mark_date != '' group by casename";
		ArrayList<HashMap<String, Object>> result = errorCaseDaoImpl.query(jdbc, getErrorType);
		return result;
	}
	
	public void setMarkCase(String userName, String featureName, String errorcases, String failedreasons) throws Exception{
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		for(String acase : errorcases.split(",")){
//			String markCaseSql = "UPDATE  errorcaseinfo SET err_reason='"+failedreasons+"', mark_date='"+curDate+"' WHERE casename='"+ acase +"' AND owner='"+ userName + "' AND feature='"+featureName.trim()+"'";
			String markCaseSql = "UPDATE  errorcaseinfo SET err_desc='"+failedreasons.split("@")[1]+"', err_reason='"+failedreasons.split("@")[0]+"', mark_date='"+DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss")+"' WHERE casename='"+ acase +"' AND feature='"+featureName.trim()+"'";
			errorCaseDaoImpl.insert(jdbc, markCaseSql);
		}
	}
}
