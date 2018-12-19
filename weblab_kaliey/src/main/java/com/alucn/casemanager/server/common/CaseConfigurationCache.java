package com.alucn.casemanager.server.common;

import java.util.ArrayList;
import java.util.Collections;
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
import com.alucn.casemanager.server.common.util.DateUtil;
import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.casemanager.server.common.util.TelnetCla;
import com.alucn.casemanager.server.process.GetTimeCase;
import com.alucn.weblab.utils.TimeUtil;

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
				//判断内存中是否有server信息，
				//1、如果没有直接将client传过来的添加进去
				if(singletonCaseProperties.size()==0){
					//------------------------------我新加的20181115--------------------------------------------------
					String laststatus = body.getJSONObject(Constant.TASKSTATUS).getString("status").toString();
					body.getJSONObject(Constant.LAB).put("laststatus", laststatus);
					//body.getJSONObject(Constant.LAB).put("lasttime", TimeUtil.stampToTime(new Date().getTime()));
					body.getJSONObject(Constant.LAB).put("lasttime", new Date().getTime());
					//body.getJSONObject(Constant.LAB).put("hodingtime", "0");
					//-----------------------------------------------------------------------------------------------
					logger.info("[add host "+serverName+" status : "+taskStatus+"]");
					//System.out.println("0==[add host "+serverName+" status : "+taskStatus+"]");
					singletonCaseProperties.add(body);
				//2、如果内存中有server记录
				}else{
					//遍历每一个server
					for(int i=0; i<singletonCaseProperties.size();i++){
						JSONObject tmpJsonObject = (JSONObject) singletonCaseProperties.get(i);
						//当内存中的server与client中传过来的server匹配时：用client的更新server信息
						if(tmpJsonObject.getJSONObject(Constant.LAB).getString(Constant.SERVERNAME).equals(serverName)){
							//singletonCaseProperties.remove(i);
							String status = body.getJSONObject(Constant.TASKSTATUS).getString("status").toString();
							status = status.equals("Succeed")?"Idle":status;
							status = status.equals("ReadyInstall")?"Idle":status;
							status = status.equals("getInstalling")?"Installing":status;
							
							String ulaststatus="";
							long ulasttime = new Date().getTime();
							try {
								ulaststatus = tmpJsonObject.getJSONObject(Constant.LAB).getString("laststatus");
								ulasttime = tmpJsonObject.getJSONObject(Constant.LAB).getLong("lasttime");
							} catch (Exception e) {
								e.printStackTrace();
								tmpJsonObject.getJSONObject(Constant.LAB).put("laststatus", status);
								tmpJsonObject.getJSONObject(Constant.LAB).put("lasttime", new Date().getTime());
							}
							long nowtime = new Date().getTime();
							long hodingtime = nowtime-ulasttime;
							logger.info("ulaststatus : "+ulaststatus+" status : "+status);
							logger.info("ulasttime : "+ulasttime+" nowtime : "+ nowtime+" >> "+hodingtime);
							if(!ulaststatus.equals(status)&&!ulaststatus.equals("Succeed")) {
								logger.info(ulaststatus+">>"+status);
								//此处应该加到数据库做成log
								//设计表结构：n_lab_status_time
								//id,labname,ip,release,protocol,spa,rtdb,servertype,matetype,mateserver,groupid,endstatus,endtime,startstatus,starttime,stateflag
								try {
									String ip = body.getJSONObject(Constant.LAB).getString(Constant.IP);
									String serverRelease = body.getJSONObject(Constant.LAB).getString(Constant.SERVERRELEASE);
									String serverProtocol = body.getJSONObject(Constant.LAB).getString(Constant.SERVERPROTOCOL);
									String serverSPA = body.getJSONObject(Constant.LAB).getString(Constant.SERVERSPA);
									String serverRTDB = body.getJSONObject(Constant.LAB).getString(Constant.SERVERRTDB);
									String serverType = body.getJSONObject(Constant.LAB).getString(Constant.SERVERTYPE);
									String mateServer = body.getJSONObject(Constant.LAB).getString(Constant.MATESERVER);
									String serverMate = body.getJSONObject(Constant.LAB).getString(Constant.SERVERMATE);
									String deptid = body.getJSONObject(Constant.LAB).getString("deptid");
									JdbcUtil jdbc_cf = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
									String insertLog = "insert into n_lab_status_time(labname,ip,release,protocol,spa,rtdb,servertype,matetype,mateserver,groupid,endstatus,endtime,startstatus,starttime)"
											+" values('"+serverName+"','"+ip+"','"+serverRelease+"','"+serverProtocol+"','"+serverSPA+"','"+serverRTDB+"','"+serverType+"','"+mateServer+"','"+serverMate+"','"+deptid+"','"+status+"','"+nowtime+"','"+ulaststatus+"','"+ulasttime+"')";
									logger.info(insertLog);
									jdbc_cf.executeSql(insertLog);
									
									String updateServerList = "update serverList set status='"+status+"' where serverName='"+serverName+"'";
									jdbc_cf.executeSql(updateServerList);
									
								} catch (Exception e) {
									e.printStackTrace();
								}
								body.getJSONObject(Constant.LAB).put("laststatus", status);
								body.getJSONObject(Constant.LAB).put("lasttime", nowtime);
							}else {
								body.getJSONObject(Constant.LAB).put("laststatus", ulaststatus);
								body.getJSONObject(Constant.LAB).put("lasttime", ulasttime);
							}
							singletonCaseProperties.set(i,body);
							isExist = true;
							logger.info("[refresh "+serverName+" status : "+taskStatus+"]");
							//System.out.println("[refresh "+serverName+" status : "+taskStatus+"]");
						}
						//当内存中只有一个server且没有与client的server匹配，
						//不修改当前server，并新加进去client的server
						if(i==singletonCaseProperties.size()-1 && !isExist){
							//------------------------------我新加的20181115--------------------------------------------------
							String laststatus = body.getJSONObject(Constant.TASKSTATUS).getString("status").toString();
							body.getJSONObject(Constant.LAB).put("laststatus", laststatus);
							//body.getJSONObject(Constant.LAB).put("lasttime", TimeUtil.stampToTime(new Date().getTime()));
							body.getJSONObject(Constant.LAB).put("lasttime", new Date().getTime());
							//body.getJSONObject(Constant.LAB).put("hodingtime", "0");
							//-----------------------------------------------------------------------------------------------
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
			String toDistri_sql = "delete from toDistributeCases where case_name='"+caseName+"'";
			String toDistriTbl_sql = "delete from DistributedCaseTbl where case_name='"+caseName+"'";
			jdbc_cf.executeSql(dfttagdaily_sql);
			jdbc_cf.executeSql(toDistri_sql);
			jdbc_cf.executeSql(toDistriTbl_sql);
			dfttagdaily_sql = "select * from DailyCase where case_name='"+caseName+"'";
			List<Map<String, Object>> list_dc = jdbc_cf.findModeResult(dfttagdaily_sql, null);
			if(list_dc.size()>0) {
				String caseerr_sql = "replace into errorcaseinfo (casename, feature, err_reason, err_desc, owner, insert_date, mark_date, email_date, servername) values('"+caseName+"', '"+list_dc.get(0).get("feature_number")+"', '', '', '"+list_dc.get(0).get("author")+"', '"+DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss")+"', '', '', '"+body.getJSONObject(Constant.LAB).getString(Constant.SERVERNAME)+"')";
				jdbc_cf.executeSql(caseerr_sql);
			}
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
				String toDistri_sql = "delete from toDistributeCases where case_name='"+caseName+"'";
				String toDistriTbl_sql = "delete from DistributedCaseTbl where case_name='"+caseName+"'";
				jdbc_cf.executeSql(dfttagdaily_sql);
				jdbc_cf.executeSql(toDistri_sql);
				jdbc_cf.executeSql(toDistriTbl_sql);
				getTimeCase.analysisCase(0, "/home/surepayftp/DftCase/"+caseName.split("/")[0], caseName.split("/")[1], caseName.split("/")[0], true, 0,"", false) ;
//				getTimeCase.analysisCase(0, "D:/dbs/DB/"+caseName.split("/")[0], caseName.split("/")[1], caseName.split("/")[0], true, 0,"", false) ;
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
