package com.alucn.weblab.socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import net.sf.json.JSONArray;

public class TcpClient {

	public static void main(String[] args) throws Exception{
		JSONArray labInfo = getLabInfo();
		//System.out.println(labInfo);
		/*JSONArray fromObject = JSONArray.fromObject(labInfo.toString());
		System.out.println(fromObject);*/
	}
	public static JSONArray getLabInfo() throws IOException {
		
		Socket socket = new Socket("127.0.0.1", 10098);
		
		/*OutputStream out = socket.getOutputStream();
		byte[] buf = "hello socket".getBytes();
		out.write(buf);*/
		
		InputStream in = socket.getInputStream();
		byte[] bytes = new byte[1024 * 10];
		int len = in.read(bytes); 
		/*int len=-1;
		String result ="";
		while((len=in.read(bytes))!=-1){
			result = result + new String(bytes,0,len);
		}*/
		//System.out.println(new String(ibuf,0,len));
		
		socket.close();
		return JSONArray.fromObject(new String(bytes,0,len));
		//return result;
	}

}
