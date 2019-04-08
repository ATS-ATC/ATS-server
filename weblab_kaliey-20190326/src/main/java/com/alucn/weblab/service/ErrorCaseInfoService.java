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
import com.alucn.weblab.utils.KalieyMysqlUtil;
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
	@Autowired
	private KalieyMysqlUtil jdbc;
	 
	public Map<String, String> getErrorCaseInfo(String userName, String auth) throws Exception{
		errroCases = new HashMap<String, String>();
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
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
	/*public ArrayList<HashMap<String, Object>> getErrorCaseInfoTable(String userName, String auth) throws Exception{
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String sql = "select feature,owner,servername,count(casename) fcount  from errorcaseinfo where 1=1";
		if(!auth.equals(Constant.AUTH)){
			sql = sql+" and owner='"+userName+"'";
		}
		sql = sql+" group by feature,owner,servername";
		ArrayList<HashMap<String, Object>> result = errorCaseDaoImpl.query(jdbc, sql);
		return result;
	}*/
	public ArrayList<HashMap<String, Object>> getErrorCaseInfo(String featureName, String author, boolean checkAllCase) throws Exception{
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		String getErrorCase = "SELECT casename, err_reason, err_desc, report_path,owner FROM errorcaseinfo WHERE 1=1 and feature='"+featureName+"'";
		//if(!checkAllCase.equals(Constant.AUTH)){
		if(!checkAllCase){
			getErrorCase = getErrorCase+" and owner='"+author+"'";
		}
		System.out.println(getErrorCase);
		ArrayList<HashMap<String, Object>> result = errorCaseDaoImpl.query(jdbc, getErrorCase);
		return result;
	}
	
	public ArrayList<HashMap<String, Object>> getErrorCaseReason() throws Exception{
//		failedReason = new HashMap<String, String>();
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
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
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		String getErrorType = "select casename, feature, err_reason, owner, insert_date, mark_date, email_date, servername, err_desc,max(mark_date) from errorcaseinfoHistory where mark_date != '' group by casename";
		ArrayList<HashMap<String, Object>> result = errorCaseDaoImpl.query(jdbc, getErrorType);
		return result;
	}
	
	public void setMarkCase(String userName, String featureName, String errorcases, String failedreasons) throws Exception{
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		for(String acase : errorcases.split(",")){
//			String markCaseSql = "UPDATE  errorcaseinfo SET err_reason='"+failedreasons+"', mark_date='"+curDate+"' WHERE casename='"+ acase +"' AND owner='"+ userName + "' AND feature='"+featureName.trim()+"'";
			String markCaseSql = "UPDATE  errorcaseinfo SET err_desc='"+failedreasons.split("@")[1]+"', err_reason='"+failedreasons.split("@")[0]+"', mark_date='"+DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss")+"' WHERE casename='"+ acase +"' AND feature='"+featureName.trim()+"'";
			errorCaseDaoImpl.insert(jdbc, markCaseSql);
		}
	}
	public void setMarkCaseInfo(String userName, String featureName, String caseList, String failType, String failDesc, String tagTime) throws Exception{
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		for(String acase : caseList.split(",")){
			String markCaseSql = 
					"update  errorcaseinfo set "
					+ "err_desc='"+failDesc+"', "
					+ "err_reason='"+failType+"', "
					+ "tagTime='"+tagTime+"', "
					+ "mark_date='"+DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss")+"' "
					+ "where casename='"+ acase +"' "
					+ "and feature='"+featureName.trim()+"';";
			System.out.println("markCaseSql : "+markCaseSql);
			errorCaseDaoImpl.update(jdbc, markCaseSql);
		}
	}
	public ArrayList<HashMap<String, Object>> getErrorCaseInfoTable(String userName,String feature, boolean hasRole, String offset, String limit, String sort, String sortOrder) throws Exception {
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		//String sql = "select feature,owner,servername,count(casename) fcount  from errorcaseinfo where 1=1";
		/*String sql = "select a.feature,a.owner,a.servername,count(a.casename) fcount,ifnull(ucount,0) ucount,ifnull(ccount,0) ccount\n" + 
				"from errorcaseinfo a \n" + 
				"left join (\n" + 
				"select feature,count(1) ucount from errorcaseinfo where err_reason=='' or err_reason is null group by feature\n" + 
				") b on a.feature=b.feature\n" + 
				"left join (\n" + 
				"select feature,count(1) ccount from errorcaseinfo where err_reason!='' group by feature\n" + 
				") c on a.feature=c.feature "
				+ "where 1=1 and a.feature<>'' ";
		if(!hasRole){
			sql = sql+" and a.owner='"+userName+"'";
		}
		if(feature!=null && !"".equals(feature)) {
			sql=sql+" and a.feature like '%"+feature+"%' ";
		}
		sql = sql+" group by a.feature,a.owner,a.servername ";
		if(sort!=null && !"".equals(sort)) {
			sql=sql+" order by "+sort+" "+sortOrder;
		}else {
			sql=sql+" order by ucount desc";
		}
		sql = sql+" limit "+offset+","+limit;*/
		String sql = "select a.feature,a.owner,a.servername,count(a.casename) fcount,ifnull(ucount,0) ucount,ifnull(ccount,0) ccount\n" + 
				"from errorcaseinfo a \n" + 
				"left join (\n" + 
				"select feature,count(1) ucount from errorcaseinfo where err_reason='' or err_reason is null group by feature\n" + 
				") b on a.feature=b.feature\n" + 
				"left join (\n" + 
				"select feature,count(1) ccount from errorcaseinfo where err_reason!='' group by feature\n" + 
				") c on a.feature=c.feature "
				+ "where 1=1 and a.feature<>'' ";
		if(!hasRole){
			sql = sql+" and a.owner='"+userName+"'";
		}
		if(feature!=null && !"".equals(feature)) {
			sql=sql+" and a.feature like '%"+feature+"%' ";
		}
		sql = sql+" group by a.feature,a.owner,a.servername,b.ucount,c.ccount ";
		if(sort!=null && !"".equals(sort)) {
			sql=sql+" order by "+sort+" "+sortOrder;
		}else {
			sql=sql+" order by ucount desc";
		}
		sql = sql+" limit "+offset+","+limit;
		System.out.println(sql);
		ArrayList<HashMap<String, Object>> result = errorCaseDaoImpl.query(jdbc, sql);
		return result;
	}
	public int getErrorCaseInfoTableCount(String userName,String feature, boolean hasRole) throws Exception {
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		//String sql = "select count(1) ccount  from ( select * from errorcaseinfo group by feature,owner,servername ) where 1=1 ";
		String sql = "select count(1) ccount  from ( select * from errorcaseinfo group by feature,owner,servername ) a where 1=1 ";
		if(!hasRole){
			sql = sql+" and a.owner='"+userName+"'";
		}
		if(feature!=null && !"".equals(feature)) {
			sql=sql+" and a.feature like '%"+feature+"%' ";
		}
		System.out.println(sql);
		ArrayList<HashMap<String, Object>> result = errorCaseDaoImpl.query(jdbc, sql);
		if(result.size()>0) {
			return Integer.parseInt((String)result.get(0).get("ccount"));
		}else {
			return 0; 
		}
	}
	public ArrayList<HashMap<String, Object>> getErrorCaseInfo4(String featureName, String userName, boolean checkAllCase) throws Exception {
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		String getErrorCase = 
				"select distinct a.casename,a.err_reason,b.err_reason err_reason_his,a.err_desc,b.err_desc err_desc_his,a.report_path,a.tagTime,a.owner from errorcaseinfo a\n" + 
				"left join (\n" + 
				"select casename,err_reason,err_desc,max(mark_date) from errorcaseinfoHistory where mark_date != '' group by  casename,err_reason,err_desc\n" + 
				") b on a.casename=b.casename "+ 
				"WHERE 1=1 and a.feature='"+featureName+"' ";
		//if(!checkAllCase.equals(Constant.AUTH)){
		if(!checkAllCase){
			getErrorCase = getErrorCase+" and a.owner='"+userName+"'";
		}
		//getErrorCase = getErrorCase+" group by a.casename";
		System.out.println(getErrorCase);
		ArrayList<HashMap<String, Object>> result = errorCaseDaoImpl.query(jdbc, getErrorCase);
		return result;
	}
}
