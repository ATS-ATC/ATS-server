package com.alucn.casemanager.server.process;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import com.alucn.casemanager.server.common.CaseConfigurationCache;
import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.exception.SysException;
import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.weblab.disarray.DistriButeCaseToLab;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;



/**
 * distribute case
 * @author wanghaiqi
 */
public class DistributeCase implements Runnable{
	public static Logger logger = Logger.getLogger(DistributeCase.class);
	public static ConcurrentHashMap<String, Boolean> clientACK = new ConcurrentHashMap<String, Boolean>(); 
	
	@Override
	public void run() {
	    //int count = 0;
		while (true) {
			try {
				Thread.sleep(100000);
				logger.info("[DistributeCase...]");
				//distribute case
                JSONObject caseList = DistriButeCaseToLab.getDistriButeCaseToLab().getDistributeCases().getJSONObject(Constant.AVAILABLECASE);
				if(0 != caseList.size()){
					logger.info("[case  list :]"+caseList.toString());
				}
				//To prevent the lab side from receiving the same caselist, the incoming ack affects the next wave of caselist distribution
				clientACK.clear();      
				logger.info("[call distributeCase...]");
				distributeCase(toHashMap(caseList));
				
				//check the lab status, remove the distributed case list
				/*if(count > 10){
				    DbOperation.SyncDailyCaseFromDftTag();
				    JSONArray unNeedServers = new JSONArray();
				    JSONArray currServersStatus = CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock,true,null);
	                for(int i=0; i<currServersStatus.size();i++){
	                    JSONObject tmpJsonObject = currServersStatus.getJSONObject(i);
	                    String serverName = tmpJsonObject.getJSONObject(Constant.LAB).getString(Constant.SERVERNAME);
	                    String status = tmpJsonObject.getJSONObject(Constant.TASKSTATUS).getString(Constant.STATUS);
	                    if(!status.equals(Constant.CASESTATUSRUNNING))
	                    	unNeedServers.add(serverName);
	                }
	                if(unNeedServers.size() > 0)
	                    DbOperation.DeleteDistributedCase(unNeedServers);
	                count = 0;
				}
				count ++;*/		
				//TODO
				//distribute command
				distributeCommand(null);
			} catch (SysException e) {
				logger.error("[Distribute case or command exception :]"+e.getMessage());
				logger.error(ParamUtil.getErrMsgStrOfOriginalException(e.getCause()));
			}catch (Exception e) {
				logger.error("[Distribute case or command exception :]", e);
			}
		}
	}
	
    public void distributeCase(ConcurrentHashMap<String, String> caseList) throws IOException, InterruptedException{
        while(true){
            if(caseList.size() == 0)
                return;
            @SuppressWarnings("rawtypes")
            Iterator it = caseList.keySet().iterator();  
            while(it.hasNext()){
                String caseToServerName = it.next().toString();
                JSONArray caseArray = JSONObject.fromObject(caseList.get(caseToServerName)).getJSONArray(Constant.CASELIST);
                String uuidAndCaseArray = caseList.get(caseToServerName);
                if(CaseConfigurationCache.queueOfClient.get(caseToServerName) != null){
                    JSONArray currServerNamesStatus = CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock,true,null);
                    for(int i=0; i<currServerNamesStatus.size();i++){
                        JSONObject tmpJsonObject = (JSONObject) currServerNamesStatus.get(i);
                        String serverName = tmpJsonObject.getJSONObject(Constant.LAB).getString(Constant.SERVERNAME);
                        String status = tmpJsonObject.getJSONObject(Constant.TASKSTATUS).getString(Constant.STATUS);
                        if(serverName.equals(caseToServerName)){
                            if(status.equals(Constant.CASESTATUSDEAD)){
                                caseList.remove(caseToServerName);
                            }else{
                                if(null != clientACK.get(serverName) && clientACK.get(serverName)){
                                    caseList.remove(caseToServerName);
                                    clientACK.remove(serverName);
                                    logger.info("case list send to "+caseToServerName+" success");
                                    break;
                                }
                                if (caseArray.size() > 0){
                                    CaseConfigurationCache.queueOfClient.get(caseToServerName).put(Constant.AVAILABLECASE+":"+uuidAndCaseArray);
                                    logger.info("put case list to queue of "+caseToServerName);
                                    //tmpJsonObject.getJSONObject(Constant.TASKSTATUS).put(Constant.STATUS, Constant.CASESTATUSREADY);
                                    //CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock,false,tmpJsonObject);
                                    break;
                                }else{
                                	caseList.remove(caseToServerName);
                                	logger.info("no case is suitable for "+caseToServerName);
                                	break;
                                }
                            }
                        }
                    }
                }else{
                    logger.info("[send host is not exist: ]"+ caseToServerName);
                }
            } 
            Thread.sleep(10000);
        }
    }
    
	//TODO
	public void distributeCommand(JSONObject caseList) throws IOException{
		return;
	}
	
    public static ConcurrentHashMap<String, String> toHashMap(JSONObject object){  
        ConcurrentHashMap<String, String> caseListMap = new ConcurrentHashMap<String, String>(); 
          Iterator<?> it = object.keys();  
          while (it.hasNext()){  
              String key = it.next().toString();  
              String value = object.get(key).toString();  
              caseListMap.put(key, value);  
          }  
          return caseListMap;  
      }  
	/**
	 * log format
	 * @return
	 */
	public String  getCurrentTime4Log() {
		return "  "+new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(System.currentTimeMillis()))+"  ";
	}
	
	/**
	 * @param byteArr
	 * @return
	 */
	public int byteArr2Int(byte[] byteArr){
		int result ;
		result = (int)(((byteArr[0]&0xFF)<<24)|((byteArr[1]&0xFF)<<16)|((byteArr[2]&0xFF)<<8)|(byteArr[3]&0xFF));
		return result;
	}
	/**
	 * @param i
	 * @return
	 */
	public  byte[] int2ByteArr(int i){
		byte[] result = new byte[4];
		result[0] = (byte)((i>>24)&0xFF);
		result[1] = (byte)((i>>16)&0xFF);
		result[2] = (byte)((i>>8)&0xFF);
		result[3] = (byte)(i&0xFF);
		return result;
	}
	
	/**
	 * Send response message (string)
	 * @param resjson
	 * @throws IOException
	 */
	public void sendMessage(String resjson,Socket socket) throws IOException {
		//head
		int jsonDataLength = resjson.getBytes(Constant.CHARACTER_SET_ENCODING_UTF8).length;
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		dos.write(int2ByteArr(jsonDataLength));
		if(!Constant.EMBEDDED_MESSAGE_RSP.equals(resjson)){
			logger.info(getCurrentTime4Log()+"Send message header"+jsonDataLength);
		}
		dos.write(resjson.getBytes(Constant.CHARACTER_SET_ENCODING_UTF8));
		dos.flush();
		if(!Constant.EMBEDDED_MESSAGE_RSP.equals(resjson)){
			logger.info(getCurrentTime4Log()+"Send message json data"+resjson);
		}
	}
}
