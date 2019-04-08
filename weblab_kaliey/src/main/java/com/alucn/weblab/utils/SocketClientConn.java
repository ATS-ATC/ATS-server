package com.alucn.weblab.utils;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;

import com.alucn.weblab.model.SocketInfo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import  com.alucn.casemanager.server.common.constant.Constant;

public class SocketClientConn {

	
	public static Logger logger = Logger.getLogger(SocketClientConn.class);
    public static Socket socket = null;
	public static DataOutputStream dos;
	public static BufferedInputStream bis;
	public static byte[] resultByteArr;
	private static StringBuffer receiveJsonData = new StringBuffer();
	
	public static int byteArr2Int(byte[] byteArr){
		int result ;
		result = (int)(((byteArr[0]&0xFF)<<24)|((byteArr[1]&0xFF)<<16)|((byteArr[2]&0xFF)<<8)|(byteArr[3]&0xFF));
		return result;
	}
	
	public static  byte[] int2ByteArr(int i){
		byte[] result = new byte[4];
		result[0] = (byte)((i>>24)&0xFF);
		result[1] = (byte)((i>>16)&0xFF);
		result[2] = (byte)((i>>8)&0xFF);
		result[3] = (byte)(i&0xFF);
		return result;
	}
	
	
	public static boolean isEmpty(String str){
		if(str==null||"".equals(str)){
			return true;
		}
		return false;
	}
	
	public static String date2String(Date date, String pattern){
		SimpleDateFormat formater = new SimpleDateFormat(pattern);
		String result = formater.format(date);
		return result;
	}
	
	public static void setSocket() throws IOException {
		bis = new BufferedInputStream(socket.getInputStream()); 
		dos = new DataOutputStream(socket.getOutputStream());
	}
	
	public synchronized static void connector(SocketInfo info, String reqPacket) throws Exception{
		socket = getInstance();
		try {
			socket.connect(new InetSocketAddress(info.getIp(), Integer.parseInt(info.getPort())), info.getTimeout());
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		setSocket(); 
		//Set read timeout
		socket.setSoTimeout(info.getSoTimeout());
		//send req message
		int jsonDataLength = reqPacket.getBytes(Constant.CHARACTER_SET_ENCODING_UTF8).length;
		dos.write(SocketClientConn.int2ByteArr(jsonDataLength));
		dos.write(reqPacket.getBytes(Constant.CHARACTER_SET_ENCODING_UTF8));
		dos.flush();
		readInfo();
		// System.out.println(readInfo);
		close();
		//socket.close();
	}
	
	public static String readInfo() throws IOException, Exception{
		receiveJsonData.delete(0,receiveJsonData.length()); 
		//JSON partial message byte length
		int jsonDataLength = byteArr2Int(readByByteNumber(SocketClientConn.bis, 4));
		//Read JSON message byte array
		byte[] jsonDataArr = readByByteNumber(SocketClientConn.bis, jsonDataLength);
		//json message
		String receiveJsonDataTmp = new String(jsonDataArr, Constant.CHARACTER_SET_ENCODING_UTF8);
		receiveJsonData.append(receiveJsonDataTmp);
		receiveJsonDataTmp = null;
		jsonDataArr = null;
		return receiveJsonData.toString();
	}
	
	/**
	 * Reads an array of bytes according to the number of bytes until the total number of bytes returned
	 * @param bis
	 * @param byteNumber
	 * @return
	 * @throws IOException
	 * @throws SysException 
	 */
	public static byte[] readByByteNumber(BufferedInputStream bis , int byteNumber) throws IOException, Exception{
		
		byte [] resultByteArrTmp = new byte[byteNumber];
		// complete length of bytes read
		int completeReadByteCount = 0;
		//Read byte length
		int onceReadByteCount = 0;
		int mills = 5000;
		long timeDiff = mills;
		long start = System.currentTimeMillis();
		while(completeReadByteCount < byteNumber){
			if(System.currentTimeMillis() - start >= timeDiff){
				throw new Exception("Packet receive timeout");
			}
			//Single read byte array
			byte[] onceReadByteArr = new byte[byteNumber-completeReadByteCount];
			//Read byte length
			onceReadByteCount = bis.read(onceReadByteArr);
			//logger.debug("onceReadByteCount = " + onceReadByteCount);
			//Splicing to JSON message byte array
			System.arraycopy(onceReadByteArr, 0, resultByteArrTmp, completeReadByteCount, onceReadByteCount);
			completeReadByteCount+=onceReadByteCount;
			//logger.debug("completeReadByteCount = " + completeReadByteCount);
		}
		resultByteArr=resultByteArrTmp;
		resultByteArrTmp=null;
		return resultByteArr;
	}
	public static Socket getInstance(){
		return socket == null ? new Socket():socket;
	}
	
	public static void close(){
		try {
			if(socket!=null){
				socket.close();
			}
			if(dos!=null){
				dos.close();
			}
			if(bis!=null){
				bis.close();
			}
		} catch (IOException e) {
			logger.error("socket close is exception",e);
			e.printStackTrace();
		}finally{
			socket=null;
			dos=null;
			bis=null;
		}
	}
	public static JSONArray getLabStatus() {
		SocketInfo socketInfo = new SocketInfo();
		socketInfo.setIp("135.242.16.160");
		socketInfo.setPort("10097");
		socketInfo.setTimeout(5000);
		socketInfo.setSoTimeout(5000);
		try {
			connector(socketInfo, "{\"head\": {\"reqType\": \"getlabstatus\",\"response\": \"\"},\"body\": {\"lab\": {\"serverIp\": \"\",\"serverName\": \"\",\"serverRelease\": \"\",\"serverProtocol\": \"\",\"serverTpye\": \"\",\"serverMate\": \"\",\"mateServer\": \"\",\"setName\": \"\",\"serverSPA\": [],\"serverRTDB\": []},\"taskStatus\": {\"status\": \"\",\"runningCase\": \"\"},\"taskResult\": {\"success\": [{\"name\": \"\",\"time\": \"\"}],\"fail\": [{\"name\": \"\", \"error\": \"\", \"html\": \"\"}]}}}");
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*JSONObject info = JSONObject.fromObject(receiveJsonData.toString());
		JSONArray jsonArray = info.getJSONObject("head").getJSONArray("response");*/
		JSONArray infos = new JSONArray();
		//System.out.println(receiveJsonData.toString());
		if(!"[]".equals(receiveJsonData.toString()) && !"".equals(receiveJsonData.toString())) {
			JSONObject info = JSONObject.fromObject(receiveJsonData.toString());
			JSONArray jsonArray = info.getJSONObject("head").getJSONArray("response");
			for(int i=0; i<jsonArray.size(); i++){
				JSONObject temp = new JSONObject();
				JSONObject body = jsonArray.getJSONObject(i);
				temp.put("head", "{\"reqType\": \"getlabstatus\",\"response\": \"\"}");
				temp.put("body",body);
				infos.add(temp);
			}
		}
		return infos;
	}
	public static void main(String[] args) throws Exception {
		SocketInfo socketInfo = new SocketInfo();
		socketInfo.setIp("135.242.16.160");
		socketInfo.setPort("10097");
		socketInfo.setTimeout(5000);
		socketInfo.setSoTimeout(5000);
		//connector(socketInfo, "{\"head\": {\"reqType\": \"getlabstatus\",\"response\": \"\"},\"body\": {\"lab\": {\"serverIp\": \"\",\"serverName\": \"\",\"serverRelease\": \"\",\"serverProtocol\": \"\",\"serverTpye\": \"\",\"serverMate\": \"\",\"mateServer\": \"\",\"setName\": \"\",\"serverSPA\": [],\"serverRTDB\": []},\"taskStatus\": {\"status\": \"\",\"runningCase\": \"\"},\"taskResult\": {\"success\": [{\"name\": \"\",\"time\": \"\"}],\"fail\": [{\"name\": \"\", \"error\": \"\", \"html\": \"\"}]}}}");
		connector(socketInfo, "{\"head\": {\"reqType\": \"getlabstatus\",\"response\": \"\"},\"body\": {\"lab\": {\"serverIp\": \"\",\"serverName\": \"\",\"serverRelease\": \"\",\"serverProtocol\": \"\",\"serverTpye\": \"\",\"serverMate\": \"\",\"mateServer\": \"\",\"setName\": \"\",\"serverSPA\": [],\"serverRTDB\": []},\"taskStatus\": {\"status\": \"\",\"runningCase\": \"\"},\"taskResult\": {\"success\": [{\"name\": \"\",\"time\": \"\"}],\"fail\": [{\"name\": \"\", \"error\": \"\", \"html\": \"\"}]}}}");
		/*JSONArray infos = JSONArray.fromObject(receiveJsonData.toString());
		for(int i=0; i<infos.size(); i++){
			JSONObject info = infos.getJSONObject(i);
			JSONObject lib = info.getJSONObject(Constant.LAB);
			System.out.println(lib);
		}*/
		JSONObject info = JSONObject.fromObject(receiveJsonData.toString());
		JSONArray jsonArray = info.getJSONObject("head").getJSONArray("response");
		JSONArray infos = new JSONArray();
		for(int i=0; i<jsonArray.size(); i++){
			JSONObject temp = new JSONObject();
			JSONObject body = jsonArray.getJSONObject(i);
			temp.put("head", "{\"reqType\": \"getlabstatus\",\"response\": \"\"}");
			temp.put("body",body);
			infos.add(temp);
		}
		System.out.println(infos.toString());
	}
}
