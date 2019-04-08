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
import com.alucn.casemanager.server.common.exception.SysException;
import com.alucn.casemanager.server.common.util.DateUtil;
import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.casemanager.server.common.util.TelnetCla;
import com.alucn.casemanager.server.process.GetTimeCase;
import com.alucn.weblab.utils.KalieyMysqlUtil;

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
	public static List<Object[]> paramsList = new ArrayList<Object[]>();
	public static List<Object> listParams = new ArrayList<Object>();
	private static KalieyMysqlUtil jdbc = KalieyMysqlUtil.getInstance();
	
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
	 * Description:
	 * Arguments:
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
				/*JdbcUtil jdbc_cf = null;
				try {
					jdbc_cf = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
				}catch (Exception e) {
					e.printStackTrace();
				}*/
				if(singletonCaseProperties.size()==0){
					//------------------------------20181115--------------------------------------------------
					String laststatus = body.getJSONObject(Constant.TASKSTATUS).getString("status").toString();
					body.getJSONObject(Constant.LAB).put("laststatus", laststatus);
					//body.getJSONObject(Constant.LAB).put("lasttime", TimeUtil.stampToTime(new Date().getTime()));
					body.getJSONObject(Constant.LAB).put("lasttime", new Date().getTime());
					//body.getJSONObject(Constant.LAB).put("hodingtime", "0");
					String deptid = body.getJSONObject(Constant.LAB).getString("deptid");
					String updateServerList = "replace into serverList values('"+serverName+"','','','','"+laststatus+"','"+deptid+"')";
					// jdbc_cf.executeSql(updateServerList);
					//-----------------------------------------------------------------------------------------------
					logger.info("[add host "+serverName+" status : "+taskStatus+"]");
					//System.out.println("0==[add host "+serverName+" status : "+taskStatus+"]");
					singletonCaseProperties.add(body);
				}else{
					for(int i=0; i<singletonCaseProperties.size();i++){
						JSONObject tmpJsonObject = (JSONObject) singletonCaseProperties.get(i);
						if(tmpJsonObject.getJSONObject(Constant.LAB).getString(Constant.SERVERNAME).equals(serverName)){
							//singletonCaseProperties.remove(i);
							String status = body.getJSONObject(Constant.TASKSTATUS).getString("status").toString();
							String ip = body.getJSONObject(Constant.LAB).getString(Constant.IP);
							String serverRelease = body.getJSONObject(Constant.LAB).getString(Constant.SERVERRELEASE);
							String serverProtocol = body.getJSONObject(Constant.LAB).getString(Constant.SERVERPROTOCOL);
							String serverSPA = body.getJSONObject(Constant.LAB).getString(Constant.SERVERSPA);
							String serverRTDB = body.getJSONObject(Constant.LAB).getString(Constant.SERVERRTDB);
							String serverType = body.getJSONObject(Constant.LAB).getString(Constant.SERVERTYPE);
							String mateServer = body.getJSONObject(Constant.LAB).getString(Constant.MATESERVER);
							String serverMate = body.getJSONObject(Constant.LAB).getString(Constant.SERVERMATE);
							String deptid = body.getJSONObject(Constant.LAB).getString("deptid");
							
							status = status.equals("Succeed")?"Idle":status;
							status = status.equals("ReadyInstall")?"Idle":status;
							
							String ulaststatus="";
							long ulasttime = new Date().getTime();
							try {
								ulaststatus = tmpJsonObject.getJSONObject(Constant.LAB).getString("laststatus");
								ulasttime = tmpJsonObject.getJSONObject(Constant.LAB).getLong("lasttime");
							} catch (Exception e) {
								e.printStackTrace();
								tmpJsonObject.getJSONObject(Constant.LAB).put("laststatus", status);
								tmpJsonObject.getJSONObject(Constant.LAB).put("lasttime", new Date().getTime());
								String updateServerList = "replace into serverList values('"+serverName+"','','','','"+status+"','"+deptid+"')";
								/*try {
									jdbc.executeSql(updateServerList);
								} catch (ClassNotFoundException e1) {
									e1.printStackTrace();
								}*/
							}
							long nowtime = new Date().getTime();
							long hodingtime = nowtime-ulasttime;
							logger.info("ulaststatus : "+ulaststatus+" status : "+status);
							logger.info("ulasttime : "+ulasttime+" nowtime : "+ nowtime+" >> "+hodingtime);
							if(!ulaststatus.equals(status)&&!ulaststatus.equals("Succeed")) {
								logger.info(ulaststatus+">>"+status);
								//id,labname,ip,release,protocol,spa,rtdb,servertype,matetype,mateserver,groupid,endstatus,endtime,startstatus,starttime,stateflag
								try {
									String insertLog = "insert into n_lab_status_time(labname,ip,release,protocol,spa,rtdb,servertype,matetype,mateserver,groupid,endstatus,endtime,startstatus,starttime)"
											+" values('"+serverName+"','"+ip+"','"+serverRelease+"','"+serverProtocol+"','"+serverSPA+"','"+serverRTDB+"','"+serverType+"','"+mateServer+"','"+serverMate+"','"+deptid+"','"+status+"','"+nowtime+"','"+ulaststatus+"','"+ulasttime+"')";
									logger.info(insertLog);
									jdbc.executeSql(insertLog);
									
									String updateServerList = "update serverList set status='"+status+"',deptid='"+deptid+"' where serverName='"+serverName+"'";
									// jdbc.executeSql(updateServerList);
									
								} catch (Exception e) {
									e.printStackTrace();
								}
								body.getJSONObject(Constant.LAB).put("laststatus", status);
								body.getJSONObject(Constant.LAB).put("lasttime", nowtime);
							}else {
								body.getJSONObject(Constant.LAB).put("laststatus", ulaststatus);
								body.getJSONObject(Constant.LAB).put("lasttime", ulasttime);
								String updateServerList = "replace into serverList values('"+serverName+"','','','','"+ulaststatus+"','"+deptid+"')";
								/*try {
									jdbc_cf.executeSql(updateServerList);
								} catch (ClassNotFoundException e1) {
									e1.printStackTrace();
								}*/
							}
							singletonCaseProperties.set(i,body);
							isExist = true;
							logger.info("[refresh "+serverName+" status : "+taskStatus+"]");
							//System.out.println("[refresh "+serverName+" status : "+taskStatus+"]");
						}
						if(i==singletonCaseProperties.size()-1 && !isExist){
							//------------------------------20181115--------------------------------------------------
							String laststatus = body.getJSONObject(Constant.TASKSTATUS).getString("status").toString();
							body.getJSONObject(Constant.LAB).put("laststatus", laststatus);
							//body.getJSONObject(Constant.LAB).put("lasttime", TimeUtil.stampToTime(new Date().getTime()));
							body.getJSONObject(Constant.LAB).put("lasttime", new Date().getTime());
							//body.getJSONObject(Constant.LAB).put("hodingtime", "0");
							String deptid = body.getJSONObject(Constant.LAB).getString("deptid");
							String updateServerList = "replace into serverList values('"+serverName+"','','','','"+laststatus+"','"+deptid+"')";
							/*try {
								jdbc_cf.executeSql(updateServerList);
							} catch (ClassNotFoundException e1) {
								e1.printStackTrace();
							}*/
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
							String updateServerList = "delete from serverList where serverName ='"+tmpJsonObject.getJSONObject(Constant.LAB).getString(Constant.SERVERNAME)+"'";
							/*try {
								jdbc_cf.executeSql(updateServerList);
							} catch (Exception e) {
								e.printStackTrace();
							}*/
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
		logger.info("inOrUcaseStatus++++++++++++++++++");
		JdbcUtil jdbc_df = null;
		JdbcUtil jdbc_cf = null;
		try{
			JSONArray caseFailList = body.getJSONObject(Constant.TASKRESULT).getJSONArray(Constant.FAIL);
			JSONArray caseSuccessList = body.getJSONObject(Constant.TASKRESULT).getJSONArray(Constant.SUCCESS);
			logger.info("caseFailList++++++++++++++++++"+caseFailList.toString());
			logger.info("caseSuccessList++++++++++++++++++"+caseSuccessList.toString());
			if(jdbc_df == null){
				jdbc_df = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB"));
			}
			if(jdbc_cf == null){
				jdbc_cf = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
			}
//			JdbcUtil jdbc_dc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("DailyCaseDB"));
			List<Object[]> multiParamsList = new ArrayList<Object[]>();
			paramsList.clear();
			listParams.clear();
			TelnetCla telnetCla = new TelnetCla(Constant.TMSHOSTNAME, Constant.TMSHOSTPORT, Constant.TMSUSER, Constant.TMSPASSWORD, Constant.TMSUSERAD4, Constant.TMSPASSWORDAD4);
			boolean readUntil = telnetCla.readUntil("apxtms: /home/"+Constant.TMSUSER+">");
			if(caseSuccessList.size() > 0 && !caseSuccessList.getJSONObject(0).getString(Constant.NAME).equals("")){
				GetTimeCase getTimeCase = new GetTimeCase();
				for(int i=0; i<caseSuccessList.size(); i++){
				    String caseName = caseSuccessList.getJSONObject(i).getString(Constant.NAME).replace("dft_server/", "");
				    Integer caseTime = caseSuccessList.getJSONObject(i).getInt(Constant.TIME);
				    Object [] paramsArray = new Object[2];
				    paramsArray[0] = caseTime;
				    paramsArray[1] = caseName;
					multiParamsList.add(paramsArray);
					Object [] paramsArray2 = new Object[1];
					paramsArray2[0] = caseName;
					paramsList.add(paramsArray2);
					getTimeCase.analysisCase(0, "/home/surepayftp/DftCase/"+caseName.split("/")[0], caseName.split("/")[1], caseName.split("/")[0], true, 0,"", false) ;
//					getTimeCase.analysisCase(0, "D:/dbs/DB/"+caseName.split("/")[0], caseName.split("/")[1], caseName.split("/")[0], true, 0,"", false) ;
					if(readUntil){
						String result = telnetCla.doJob(Constant.TMSUSER,caseName);
						String[] split = result.split(",");
						for(int j=0; j<split.length-1; j++){
							String sql = "UPDATE DftTag SET case_level='"+split[j]+"' WHERE case_name='"+caseName+"' ;";
							jdbc_df.executeSql(sql);
							logger.info("[update "+caseName+" case_level to : "+split[j]+"]");
						}
					}else{
						logger.error("[update "+caseName+" case_level is failed !");
					}
				}
				logger.info("multiParamsList++++++++++++++++++"+multiParamsList.toString());
				logger.info("paramsList++++++++++++++++++"+paramsList.toString());
				jdbc_df.executeBatch("UPDATE DftTag SET case_status='S', status_owner='ATS', case_cost=? WHERE case_name=? ;", multiParamsList);
				jdbc_cf.executeBatch("DELETE FROM DailyCase WHERE case_name=? ;", paramsList);
			}
			
			List<Object[]> failParamsList = new ArrayList<Object[]>();
			multiParamsList.clear();
			if(caseFailList.size() > 0 && !caseFailList.getJSONObject(0).getString(Constant.NAME).equals("")){
				for(int i=0; i<caseFailList.size(); i++){
					String caseName = caseFailList.getJSONObject(i).getString(Constant.NAME).replace("dft_server/", "");
				    Object [] paramsArray = new Object[1];
					paramsArray[0] = caseName;
					paramsList.add(paramsArray);
					failParamsList.add(paramsArray);
					String dfttagdaily_sql2 = "SELECT * FROM DailyCase WHERE case_name='"+caseName+"' ;";
					List<Map<String, Object>> list_dc = jdbc_cf.findModeResult(dfttagdaily_sql2, null);
					if(list_dc.size()>0) {
						Object [] errorCaseParamsArray = new Object[7];
						errorCaseParamsArray[0] = caseName;
						errorCaseParamsArray[1] = list_dc.get(0).get("feature_number");
						errorCaseParamsArray[2] = list_dc.get(0).get("author");
						errorCaseParamsArray[3] = DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss");
						errorCaseParamsArray[4] = body.getJSONObject(Constant.LAB).getString(Constant.SERVERNAME);
						errorCaseParamsArray[5] = caseFailList.getJSONObject(i).getString(Constant.HTML);
						errorCaseParamsArray[6] = caseFailList.getJSONObject(i).getString(Constant.ERROR);
						multiParamsList.add(errorCaseParamsArray);
					}
				}
				logger.info("failParamsList++++++++++++++++++"+failParamsList.toString());
				jdbc_df.executeBatch("UPDATE DftTag SET case_status='F',status_owner='ATS' WHERE case_name=? ;", failParamsList);
				jdbc_cf.executeBatch("UPDATE DailyCase SET case_status='F',status_owner='ATS' WHERE case_name=? ;", failParamsList);
				jdbc_cf.executeBatchWithTrigger("REPLACE INTO errorcaseinfo (casename, feature, err_reason, err_desc, owner, insert_date, mark_date, email_date, servername, report_path, sunit_report_reason) VALUES(?, ?, '', '', ?, ?, '', '', ?, ?, ?) ;", multiParamsList);
			}
			
			logger.info("paramsList++++++++++++++++++"+paramsList.toString());
			jdbc_cf.executeBatch("DELETE FROM toDistributeCases WHERE case_name=? ;", paramsList);
			jdbc_cf.executeBatch("DELETE FROM DistributedCaseTbl WHERE case_name=? ;", paramsList);
			if((caseFailList.size() > 0 && caseFailList.getJSONObject(0).getString(Constant.NAME).equals("")) && (caseSuccessList.size() > 0 && caseSuccessList.getJSONObject(0).getString(Constant.NAME).equals(""))){
				logger.info("AutoRun is failure...");
				throw new SysException("AutoRun is failure...");
			}
		}catch (Exception e) {
			while(true){
				try {
					if(jdbc_cf == null){
						jdbc_cf = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
					}
					String serverName = body.getJSONObject(Constant.LAB).getString(Constant.SERVERNAME);
					String dt = "DELETE FROM toDistributeCases WHERE server LIKE ? ;";
					String dd = "DELETE FROM DistributedCaseTbl WHERE server_name LIKE ? ;";
					Object [] paramsArray = new Object[1];
					paramsArray[0] = "%"+serverName+"%";
					jdbc_cf.executeUpdate(dt, paramsArray);
					jdbc_cf.executeUpdate(dd, paramsArray);
					logger.info("[delete table DistributedCaseTbl succeed]");
					break;
				} catch (Exception e2) {
					Thread.sleep(3000);
				}
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		try {
			throw new SysException("AutoRun is failure...");
		} catch (Exception e) {
			System.out.println("aaa");
		}
	}
}
