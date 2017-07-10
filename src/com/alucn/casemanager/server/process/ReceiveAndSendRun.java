package com.alucn.casemanager.server.process;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.apache.log4j.Logger;
import com.alucn.casemanager.server.common.CaseConfigurationCache;
import com.alucn.casemanager.server.common.ConfigProperites;
import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.exception.SysException;
import com.alucn.casemanager.server.common.util.ParamUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * It is mainly responsible for receiving the request message and sending the response message
 * @author wanghaiqi
 *
 */

public class ReceiveAndSendRun implements Runnable {
	private BufferedReader input;
	private BufferedWriter out;
	private BufferedInputStream bis;
	private Socket socket;
	private static Logger logger = Logger.getLogger(ReceiveAndSendRun.class);
	private final BlockingQueue<String> sendMessageBlockingQueue = new ArrayBlockingQueue<String>(20, false);
	public static String serverName;
	
	public void run() {
		Thread sendThread = new Thread(new SendMessage());
		sendThread.start();
		int countNum =0;
		while(true){
			try {
				if(socket==null || socket.isClosed() || !socket.isConnected()){
					new SysException("server socket is closed");
				}
				logger.info("[loop read or write client socket inorout...]");
				//1.Get request message (string)
				String reqJson = this.getMessage();
				//2.Call the main processing logic method to get the response message
				String rspResult = "";
				//a.If it is a pre health test, direct response back to the preset response message
				if(Constant.EMBEDDED_MESSAGE_REQ.equals(reqJson)){
					logger.info("[Health monitoring in progress...]");
					countNum =0;
					rspResult = Constant.EMBEDDED_MESSAGE_RSP;
				}
				//b.Get response message
				else{
				    countNum =0;
					MainProcess mainProcess = new MainProcess();
					long start=0L,end=0L;
					start = System.currentTimeMillis();
					rspResult = mainProcess.process(reqJson, socket, sendMessageBlockingQueue);
					end = System.currentTimeMillis();
					logger.info("Request processing time"+(end-start));
				}
				//3.Send response message (string)
				this.sendMessage(rspResult);
				logger.info("[Send response message (string):"+rspResult+"]");
			}catch (SysException e) {
				logger.error("[Failed to receive or send message]"+e.getMessage());
				logger.error(ParamUtil.getErrMsgStrOfOriginalException(e.getCause()));
			} catch (Exception e) {
				logger.error("[Failed to receive or send message]");
				logger.error(ParamUtil.getErrMsgStrOfOriginalException(e));
			}finally{
				if(countNum >= Integer.parseInt(ConfigProperites.getInstance().getCaseClientRetryTime())){
                    JSONArray currKeyStatus = CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock,true,null);
                    for(int i=0; i<currKeyStatus.size();i++){
                        JSONObject tmpJsonObject = (JSONObject) currKeyStatus.get(i);
                        if(tmpJsonObject.getJSONObject(Constant.LAB).getString(Constant.SERVERNAME).equals(serverName)){
                        	tmpJsonObject.getJSONObject(Constant.TASKSTATUS).put(Constant.STATUS, Constant.CASESTATUSDEAD);
                        	tmpJsonObject.getJSONObject(Constant.TASKSTATUS).put(Constant.RUNNINGCASE, "");
                        	CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock,false,tmpJsonObject);
                        }
                    }
				    logger.info("[Reconnection to the maximum number of times, the connection Invalid, kill thread]");
					try {
                        this.socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
				    break;
				}
				try {
					Thread.sleep(Integer.parseInt(ConfigProperites.getInstance().getCaseClientSocketTime()));
				} catch (NumberFormatException | InterruptedException e) {
					e.printStackTrace();
				}
				countNum++;
			}
		}
	}

	public ReceiveAndSendRun(Socket socket) throws IOException {
		this.socket = socket;
		this.bis = new BufferedInputStream(socket.getInputStream()); 
		this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	}
	
	class SendMessage implements Runnable {
		public SendMessage(){}
		@Override
		public void run() {
			while(true){
				try {
					logger.info("send message thread is started !");
					String message = sendMessageBlockingQueue.take();
					sendMessage(message);
					Thread.sleep(Integer.parseInt(ConfigProperites.getInstance().getCaseClientSocketTime()));
				} catch (Exception e) {
					logger.error("[Failed to send message]"+e.getMessage());
				}
			}
		}
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
	 * Get request message (string)
	 * @return
	 * @throws IOException
	 * @throws SysException 
	 */
	
	public String getMessage() throws IOException, SysException {
		
		byte[] communicateHead = readByByteNumber(this.bis, 4);
		//JSON partial message byte length
		int jsonDataLength = byteArr2Int(communicateHead);
		if(Constant.EMBEDDED_MESSAGE_REQ.getBytes().length != jsonDataLength){
			logger.info("socket.getInetAddress()  = " +socket.getInetAddress());
			logger.info(getCurrentTime4Log()+" Receive request message length: "+jsonDataLength);
		}
		//Read JSON message byte array
		byte[] jsonDataArr = readByByteNumber(this.bis, jsonDataLength);
		//json message
		String jsonData = new String(jsonDataArr,"utf-8");
		if(!Constant.EMBEDDED_MESSAGE_REQ.equals(jsonData)){
			logger.info(getCurrentTime4Log()+"Receive request message JSON data:"+jsonData);
		}
		
		return jsonData;
	}
	
	/**
	 * Reads an array of bytes according to the number of bytes until the total number of bytes returned
	 * @param bis
	 * @param byteNumber
	 * @return
	 * @throws IOException
	 * @throws SysException 
	 */
	public byte[] readByByteNumber(BufferedInputStream bis , int byteNumber) throws IOException, SysException{
		
		byte[] resultByteArr = new byte[byteNumber];
		// complete length of bytes read
		int completeReadByteCount = 0;
		//Read byte length
		int onceReadByteCount = 0;
//		ConfigProperites cp = ConfigProperites.getInstance();
		int mills = Integer.parseInt(ConfigProperites.getInstance().getReadPeriodTimeout());
		long timeDiff = mills;
		long start = System.currentTimeMillis();
		while(completeReadByteCount < byteNumber){
			if(System.currentTimeMillis() - start >= timeDiff){
				throw new SysException("Packet receive timeout");
			}
			//Single read byte array
			byte[] onceReadByteArr = new byte[byteNumber-completeReadByteCount];
			//Read byte length
			onceReadByteCount = bis.read(onceReadByteArr);
//			logger.debug("onceReadByteCount = " + onceReadByteCount);
			//Splicing to JSON message byte array
			System.arraycopy(onceReadByteArr, 0, resultByteArr, completeReadByteCount, onceReadByteCount);
			completeReadByteCount+=onceReadByteCount;
//			logger.debug("completeReadByteCount = " + completeReadByteCount);
		}
		
		return resultByteArr;
	}
	/**
	 * Send response message (string)
	 * @param resjson
	 * @throws IOException
	 */
	public void sendMessage(String resjson) throws IOException {
		//head
		int jsonDataLength = resjson.getBytes(Constant.CHARACTER_SET_ENCODING_UTF8).length;
		DataOutputStream dos = new DataOutputStream(this.socket.getOutputStream());
		dos.write(int2ByteArr(jsonDataLength));
		if(!Constant.EMBEDDED_MESSAGE_RSP.equals(resjson)){
			logger.info(getCurrentTime4Log()+"Send response message header"+jsonDataLength);
		}
		dos.write(resjson.getBytes(Constant.CHARACTER_SET_ENCODING_UTF8));
		dos.flush();
		if(!Constant.EMBEDDED_MESSAGE_RSP.equals(resjson)){
			logger.info(getCurrentTime4Log()+"Send response message json data"+resjson);
		}
		if(null != null){
			dos.close();
			dos = null;
		}
	}
	
	/**
	 *close
	 */
	public void close() {
		
		try {
			input.close();
			out.close();
//			socket.close();
		} catch (IOException e) {
			logger.error(ParamUtil.getErrMsgStrOfOriginalException(e));
		} finally {
			input = null;
			out = null;
//			socket = null;
		}
	}

	 
}
