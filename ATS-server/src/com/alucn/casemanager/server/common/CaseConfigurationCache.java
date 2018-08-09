package com.alucn.casemanager.server.common;

import java.util.ArrayList;
import java.util.Collections;
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
import com.alucn.casemanager.server.common.util.DateUtil;
import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.casemanager.server.common.util.TelnetCla;
import com.alucn.casemanager.server.process.GetTimeCase;

public class CaseConfigurationCache {
	private static final Logger logger = Logger.getLogger(CaseConfigurationCache.class);
	public static ReadWriteLock lock ;
	public static List<String> removeListServer = Collections.synchronizedList(new ArrayList<String>());;
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
	/**
	 * 
	 * <pre>
	 * Example: JSONArray singletonCaseProperties = CaseConfigurationCache.getSingletonCaseProperties(CaseConfigurationCache.lock);
	 * Description: 获取存放server信息的静态共享变量
	 * Arguments: 锁
	 * Return: JSONArray
	 * Variable：null
	 * </pre>
	 */
	public static JSONArray getSingletonCaseProperties(ReadWriteLock lock) {
		try {
			lock.readLock().lock(); 
			return singletonCaseProperties;
		} finally{
			 lock.readLock().unlock();  
		}
	}
	public static JSONArray readOrWriteSingletonCaseProperties(ReadWriteLock lock,boolean isCheck,JSONObject body){
		if (isCheck) {
			try {
				lock.readLock().lock(); 
				return singletonCaseProperties;
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
					//System.out.println("0==[add host "+serverName+" status : "+taskStatus+"]");
					singletonCaseProperties.add(body);
				}else{
					for(int i=0; i<singletonCaseProperties.size();i++){
						JSONObject tmpJsonObject = (JSONObject) singletonCaseProperties.get(i);
						if(tmpJsonObject.getJSONObject(Constant.LAB).getString(Constant.SERVERNAME).equals(serverName)){
//							singletonCaseProperties.remove(i);
							singletonCaseProperties.set(i,body);
							isExist = true;
							logger.info("[refresh "+serverName+" status : "+taskStatus+"]");
							//System.out.println("[refresh "+serverName+" status : "+taskStatus+"]");
						}
						if(i==singletonCaseProperties.size()-1 && !isExist){
							logger.info("[add host "+serverName+" status : "+taskStatus+"]");
							//System.out.println("!0==[add host "+serverName+" status : "+taskStatus+"]");
							singletonCaseProperties.add(body);
							break;
						}
					}
					//remove server
					for(int i=0; i<singletonCaseProperties.size();i++){
						JSONObject tmpJsonObject = (JSONObject) singletonCaseProperties.get(i);
						if(removeListServer.contains(tmpJsonObject.getJSONObject(Constant.LAB).getString(Constant.SERVERNAME))){
							singletonCaseProperties.remove(i);
						}
					}
					removeListServer.clear();
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
//		JdbcUtil jdbc_dc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("DailyCaseDB"));
		JdbcUtil jdbc_cf = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
		for(int i=0; i<caseFailList.size(); i++){
		    String caseName = caseFailList.getString(i).replace("dft_server/", "");
			String dfttag_sql = "update DftTag set case_status='F',status_owner='ATS' where case_name='"+caseName+"'";
			jdbc_df.executeSql(dfttag_sql);
			String dfttagdaily_sql = "update DailyCase set case_status='F',status_owner='ATS' where case_name='"+caseName+"'";
			jdbc_cf.executeSql(dfttagdaily_sql);
			
			dfttagdaily_sql = "select * from DailyCase where case_name='"+caseName+"'";
			List<Map<String, Object>> list_dc = jdbc_cf.findModeResult(dfttagdaily_sql, null);
			String caseerr_sql = "replace into errorcaseinfo (casename, feature, err_reason, err_desc, owner, insert_date, mark_date, email_date, servername) values('"+caseName+"', '"+list_dc.get(0).get("feature_number")+"', '', '', '"+list_dc.get(0).get("author")+"', '"+DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss")+"', '', '', '"+body.getJSONObject(Constant.LAB).getString(Constant.SERVERNAME)+"')";
			jdbc_cf.executeSql(caseerr_sql);
		}
		
		TelnetCla telnetCla = new TelnetCla(Constant.TMSHOSTNAME, Constant.TMSHOSTPORT, Constant.TMSUSER, Constant.TMSPASSWORD, Constant.TMSUSERAD4, Constant.TMSPASSWORDAD4);
		boolean readUntil = telnetCla.readUntil("apxtms: /home/"+Constant.TMSUSER+">");
		if(!caseSuccessList.getJSONObject(0).getString(Constant.NAME).equals("")){
			GetTimeCase getTimeCase = new GetTimeCase();
			for(int i=0; i<caseSuccessList.size(); i++){
			    String caseName = caseSuccessList.getJSONObject(i).getString(Constant.NAME).replace("dft_server/", "");
			    Integer caseTime = caseSuccessList.getJSONObject(i).getInt(Constant.TIME);
				String dfttag_sql = "update DftTag set case_status='S', status_owner='ATS', case_cost="+caseTime+" where case_name='"+caseName+"'";
				jdbc_df.executeSql(dfttag_sql);
				String dfttagdaily_sql = "delete from DailyCase where case_name='"+caseName+"'";
				jdbc_cf.executeSql(dfttagdaily_sql);
				//getTimeCase.analysisCase(0, "/home/surepayftp/DftCase/"+caseName.split("/")[0], caseName.split("/")[1], caseName.split("/")[0], true, 0,"", false) ;
				getTimeCase.analysisCase(0, "D:/dbs/DB/"+caseName.split("/")[0], caseName.split("/")[1], caseName.split("/")[0], true, 0,"", false) ;
				if(readUntil){
					String result = telnetCla.doJob(Constant.TMSUSER,caseName);
					String[] split = result.split(",");
					for(int j=0; j<split.length-1; j++){
						String sql = "update DftTag set case_level='"+split[j]+"' where case_name='"+caseName+"'";
						jdbc_df.executeSql(sql);
						logger.info("[update "+caseName+" case_level to : "+split[j]+"]");
					}
				}else{
					logger.error("[update "+caseName+" case_level is failed !");
				}
			}
		}
		
	}
}
