package com.alucn.casemanager.server.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.casemanager.server.common.util.ParamUtil;

public class CaseConfigurationCache {
	private static final Logger logger = Logger.getLogger(CaseConfigurationCache.class);
	public static ReadWriteLock lock ;
	//Case attribute configuration with dynamic refresh
	private static JSONArray singletonCaseProperties ;
	//Cache socket long connection information
	public static ConcurrentMap<String, BlockingQueue<String>> queueOfClient = null;
	//command configuration with dynamic refresh
	public static JSONObject singletonCaseCommand ;
	
	static {
		try {
			singletonCaseProperties=new JSONArray();
			singletonCaseCommand=new JSONObject();
			queueOfClient = new ConcurrentHashMap<String, BlockingQueue<String>>();
			lock = new ReentrantReadWriteLock(false);
		} catch (Exception e) {
			logger.error("[CaseConfigurationCache exception construct]", e);
			e.printStackTrace();
		}
	}
	
	public static JSONArray readOrWriteSingletonCaseProperties(ReadWriteLock lock,boolean isCheck,JSONObject body){
		if (isCheck) {
			try {
				lock.readLock().lock();  
				JSONArray infos = singletonCaseProperties;
				return infos;
			} finally{
				 lock.readLock().unlock();  
			}
		} else {  
			try {
				lock.writeLock().lock();
				boolean isExist = false;
				String serverName = body.getJSONObject(Constant.LAB).getString(Constant.SERVERNAME);
				String taskStatus = body.getJSONObject(Constant.TASKSTATUS).toString();
				if(singletonCaseProperties.size()==0){
					logger.info("[add host "+serverName+" status : "+taskStatus+"]");
					singletonCaseProperties.add(body);
				}else{
					for(int i=0; i<singletonCaseProperties.size();i++){
						JSONObject tmpJsonObject = (JSONObject) singletonCaseProperties.get(i);
						if(tmpJsonObject.getJSONObject(Constant.LAB).getString(Constant.SERVERNAME).equals(serverName)){
//							singletonCaseProperties.remove(i);
							singletonCaseProperties.set(i,body);
							isExist = true;
							logger.info("[refresh "+serverName+" status : "+taskStatus+"]");
						}
						if(i==singletonCaseProperties.size()-1 && !isExist){
							logger.info("[add host "+serverName+" status : "+taskStatus+"]");
							singletonCaseProperties.add(body);
							break;
						}
					}
				}
				return null;
			} finally{
				lock.writeLock().unlock();  
			}
		}  
	}

	public JSONObject readOrWriteSingletonCaseCommand(ReadWriteLock lock,boolean isCheck, JSONObject message){
		if (isCheck) {
			try {
				lock.readLock().lock();  
				JSONObject tmp = singletonCaseCommand;
	            return tmp;
			} finally{
				lock.readLock().unlock();
			}
			
		} else { 
			try {
				lock.writeLock().lock();  
				singletonCaseCommand = message;
			} finally{
				lock.writeLock().unlock();  
			}
		}
		return null;  
	}

	public static synchronized void inOrUcaseStatus(JSONObject body) throws Exception{
		JSONArray caseFailList = body.getJSONObject(Constant.TASKRESULT).getJSONArray(Constant.FAIL);
		JSONArray caseSuccessList = body.getJSONObject(Constant.TASKRESULT).getJSONArray(Constant.SUCCESS);
		JdbcUtil jdbc_df = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB"));
		JdbcUtil jdbc_dc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("DailyCaseDB"));
		JdbcUtil jdbc_cf = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
		for(int i=0; i<caseFailList.size(); i++){
		    String caseName = caseFailList.getString(i).replace("dft_server/", "");
			String dfttag_sql = "update DftTag set case_status='F',status_owner='ATS' where case_name='"+caseName+"'";
			jdbc_df.executeSql(dfttag_sql);
			String dfttagdaily_sql = "update DailyCase set case_status='F',status_owner='ATS' where case_name='"+caseName+"'";
			jdbc_dc.executeSql(dfttagdaily_sql);
			
			Date dt=new Date();
		    SimpleDateFormat matter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dfttag_sql = "select * from DailyCase where case_name='"+caseName+"'";
			List<Map<String, Object>> list_dc = jdbc_dc.findModeResult(dfttag_sql, null);
			String caseerr_sql = "replace into errorcaseinfo (casename, fature, err_reason, owner, insert_date, mark_date, email_date) values('"+caseName+"', '"+list_dc.get(0).get("feature_number")+"', '', '"+list_dc.get(0).get("author")+"', '"+matter.format(dt)+"', '', '')";
			jdbc_cf.executeSql(caseerr_sql);
		}
		
		for(int i=0; i<caseSuccessList.size(); i++){
		    String caseName = caseSuccessList.getJSONObject(i).getString(Constant.NAME).replace("dft_server/", "");
			String dfttag_sql = "update DftTag set case_status='S',status_owner='ATS' where case_name='"+caseName+"'";
			jdbc_df.executeSql(dfttag_sql);
			String dfttagdaily_sql = "delete from DailyCase where case_name='"+caseName+"'";
			jdbc_dc.executeSql(dfttagdaily_sql);
		}
	}
}
