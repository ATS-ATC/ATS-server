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
				//DistriButeCaseToLab disarrayCase = new DistriButeCaseToLab();
				//单例模式DistriButeCaseToLab.getDistriButeCaseToLab()用于创建唯一对象
                JSONObject caseList = DistriButeCaseToLab.getDistriButeCaseToLab().GetDistributeCases().getJSONObject(Constant.AVAILABLECASE);
				if(0 != caseList.size()){
					logger.info("[case  list :]"+caseList.toString());
				}
				//To prevent the lab side from receiving the same caselist, the incoming ack affects the next wave of caselist distribution
				clientACK.clear();      
				logger.info("[call distributeCase...]");
				distributeCase(toHashMap(caseList));
				
				//check the lab status, remove the distributed case list
				if(count > 10){
				    DbOperation.SyncDailyCaseFromDftTag();
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
	/**
	 * <pre>
	 * Example: 
	 * Description: 应该是分发case到不同的server上去，具体细节还需要再研究下
	 * Arguments: caseList 需要分发的case集合(包含server信息吧？打印一个看看)
	 * Return: void
	 * Variable：
	 * 		queueOfClient 是保存长连接的静态变量么？
	 * 		clientACK 好像是避免重复运行case的
	 * </pre>
	 */
    public void distributeCase(ConcurrentHashMap<String, String> caseList) throws IOException, InterruptedException{
    	//System.err.println("distributeCase caseList:========================="+caseList);
    	//distributeCase caseList:=========================
    	/*{
    		BJRMS21B={"uuid":"781da79d-90b6-4552-85a3-0656dc7338b1","case_list":[]}, 
    		BJRMS21A={"uuid":"c219bdbf-8f21-4ac3-89d2-717721d093ba","case_list":["732784/fs5422.json","732784/fs5423.json","732784/fs5424.json","732784/fs5425.json","732784/fs5426.json","732784/fs5427.json"]}, 
    		BJRMS21F={"uuid":"bea82afb-76ee-4f21-8184-24462d464f31","case_list":[]}, 
    		BJRMS21E={"uuid":"9dcbbec8-36d7-4378-bb3e-83ddc7b7885e","case_list":[]}, 
    		BJRMS21D={"uuid":"404cd7ad-9196-4b9f-a4df-279261ad2cbf","case_list":[]}, 
    		BJRMS21C={"uuid":"1f7da1b8-8c51-42b2-aca5-daa266792c20","case_list":["731590/fr0611.json"]}
    	}*/
        while(true){
            if(caseList.size() == 0)
                return;
            boolean IsCaseExist = false;
            @SuppressWarnings("rawtypes")
            Iterator it = caseList.keySet().iterator();  
            while(it.hasNext()){
            	//server名称例如：BJRMS21B
                String key = it.next().toString();
                JSONArray case_array = JSONObject.fromObject(caseList.get(key)).getJSONArray(Constant.CASELIST);
                String value = caseList.get(key);
                //检测这个server是否有长连接
                if(CaseConfigurationCache.queueOfClient.get(key) != null){
                	//获取内存中的server信息
                    JSONArray currKeyStatus = CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock,true,null);
                    for(int i=0; i<currKeyStatus.size();i++){
                        JSONObject tmpJsonObject = (JSONObject) currKeyStatus.get(i);
                        String serverName = tmpJsonObject.getJSONObject(Constant.LAB).getString(Constant.SERVERNAME);
                        String status = tmpJsonObject.getJSONObject(Constant.TASKSTATUS).getString(Constant.STATUS);
                        if(serverName.equals(key)){
                        	//判断server状态，如果是Dead就移除
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
                                    //tmpJsonObject.getJSONObject(Constant.TASKSTATUS).put(Constant.STATUS, Constant.CASESTATUSREADY);
                                    //CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock,false,tmpJsonObject);
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
