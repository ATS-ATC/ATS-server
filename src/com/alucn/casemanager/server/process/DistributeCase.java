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
import com.alucn.weblab.disarray.DistriButeCaseToLab;
import com.alucn.weblab.disarray.DbOperation;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;



/**
 * distribute case
 * @author wanghaiqi
 *
 */
public class DistributeCase implements Runnable{
	public static Logger logger = Logger.getLogger(DistributeCase.class);
	public static ConcurrentHashMap<String, Boolean> clientACK = new ConcurrentHashMap<String, Boolean>(); 
	
	@Override
	public void run() {
	    int count = 0;
		while (true) {
			try {
				Thread.sleep(10000);
				logger.info("[DistributeCase...]");
				//distribute case
				DistriButeCaseToLab disarrayCase = new DistriButeCaseToLab();
                JSONObject caseList = disarrayCase.GetDistributeCases().getJSONObject(Constant.AVAILABLECASE);
				if(0 != caseList.size()){
					logger.info("[case  list :]"+caseList.toString());
				}
				clientACK.clear();      //To prevent the lab side from receiving the same caselist, the incoming ack affects the next wave of caselist distribution
				distributeCase(toHashMap(caseList));
				
				//check the lab status, remove the distributed case list
				if(count > 10){
				    logger.info("check machine status.");
				    JSONArray unneedServers = new JSONArray();
				    JSONArray currKeyStatus = CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock,true,null);
	                for(int i=0; i<currKeyStatus.size();i++){
	                    JSONObject tmpJsonObject = currKeyStatus.getJSONObject(i);
	                    String server_name = tmpJsonObject.getJSONObject(Constant.LAB).getString(Constant.SERVERNAME);
	                    String status = tmpJsonObject.getJSONObject(Constant.TASKSTATUS).getString(Constant.STATUS);
	                    if(!status.equals(Constant.CASESTATUSRUNNING))
	                        unneedServers.add(server_name);
	                }
	                if(unneedServers.size() > 0)
	                    DbOperation.DeleteDistributedCase(unneedServers);
	                count = 0;
				}
				count ++;
				
				
				//distribute command
				distributeCommand(null);
			} catch (Exception e) {
				logger.error("[Distribute case or command exception :]", e);
				e.printStackTrace();
			}
		}
	}
	
    public void distributeCase(ConcurrentHashMap<String, String> caseList) throws IOException, InterruptedException{

        while(true)
        {
            if(caseList.size() == 0)
                return;
            boolean IsCaseExist = false;
            @SuppressWarnings("rawtypes")
            Iterator it = caseList.keySet().iterator();  
            while(it.hasNext()){
                String key = it.next().toString();
                JSONArray case_array = JSONObject.fromObject(caseList.get(key)).getJSONArray(Constant.CASELIST);
                String value = caseList.get(key);
                if(CaseConfigurationCache.queueOfClient.get(key) != null){
                    JSONArray currKeyStatus = CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock,true,null);
                    for(int i=0; i<currKeyStatus.size();i++){
                        JSONObject tmpJsonObject = (JSONObject) currKeyStatus.get(i);
                        String serverName = tmpJsonObject.getJSONObject(Constant.LAB).getString(Constant.SERVERNAME);
                        String status = tmpJsonObject.getJSONObject(Constant.TASKSTATUS).getString(Constant.STATUS);
                        if(serverName.equals(key)){
                            if(status.equals(Constant.CASESTATUSDEAD)){
                                caseList.remove(key);
                            }else{
                                if(null != clientACK.get(serverName) && clientACK.get(serverName)){
                                    caseList.remove(key);
                                    clientACK.remove(serverName);
                                    break;
                                }
                                if (case_array.size() > 0){
                                    IsCaseExist = true;
                                    CaseConfigurationCache.queueOfClient.get(key).put(Constant.AVAILABLECASE+":"+value);
                                    tmpJsonObject.getJSONObject(Constant.TASKSTATUS).put(Constant.STATUS, Constant.CASESTATUSREADY);
                                    CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock,false,tmpJsonObject);
                                }
                            }
                        }
                    }
                }else{
                    logger.info("[send host is not exist: ]"+ key);
                }
            } 
            
            if(!IsCaseExist)
                break;
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
        }
        
    }
	
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
