package com.alucn.casemanager.server.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.net.telnet.TelnetClient;


public class TelnetCla {
	Object lock = new Object();
	TelnetClient telnet = null;
	String hostname;
	int hostport = 23;
	String user;
	String password;
	String userAD4;
	String passwordAD4;
	private InputStream in;
	private PrintStream out;
	private static final String ORIG_CODEC = "ISO8859-1";
	private static final String TRANSLATE_CODEC = "GBK";

	public TelnetCla(String hostname, int hostport, String user, String password, String userAD4, String passwordAD4) throws Exception {
		super();
		this.hostname = hostname;
		this.hostport = hostport;
		this.user = user;
		this.password = password;
		this.userAD4 = userAD4;
		this.passwordAD4 = passwordAD4;

		telnet = new TelnetClient("VT100");                           // VT100 VT52 VT220 VTNT ANSI
		telnet.setDefaultTimeout(10000);
		telnet.connect(hostname, hostport);
		in = telnet.getInputStream();
		out = new PrintStream(telnet.getOutputStream());
		
		if(!ping(hostname)){
			if(readUntil("Username: ")){
				write(userAD4);
				write("\n");
			}
			if(readUntil("Password: ")){
				write(passwordAD4);
				write("\n");
			}
		}

		if(readUntil("login: ")){
			write(user);
			write("\n");
		}
		
		if(readUntil("Password: ")){
			write(password);
			write("\n");
		}
	}


	public boolean readUntil(String str) {
		try {
			StringBuffer sb = new StringBuffer();
			char c;
			int code = -1;
			boolean ansiControl = false;
			boolean returnValue = false;
			while ((code = (in.read())) != -1) {
				c = (char) code;
				if (c == '\033') {
					ansiControl = true;
					int code2 = in.read();
					char cc = (char) code2;
					if (cc == '[' || cc == '(') {
					}
				}
				if (!ansiControl) {
					if (c == '\r') {
						if (sb.lastIndexOf(str)!=-1) {
							returnValue = true;
							break;
						}
						if (sb.lastIndexOf(">")!=-1 ) {
							returnValue = false;
							break;
						}
						sb.delete(0, sb.length());
					}else if (c == '\n'){
					}else{
						sb.append(c);
					}
					if (sb.lastIndexOf(str)!=-1) {
						returnValue = true;
						break;
					}
					if (sb.lastIndexOf(">")!=-1) {
						returnValue = false;
						break;
					}
				}

				if (ansiControl) {
					if (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || c == '"') {
						ansiControl = false;
					}
				}
			}
			return returnValue;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	
	public String readResult(String str) {
		try {
			StringBuffer sb = new StringBuffer();
			char c;
			int code = -1;
			boolean ansiControl = false;
			
			while ((code = (in.read())) != -1) {
				c = (char) code;
				if (c == '\033') {
					ansiControl = true;
					int code2 = in.read();
					char cc = (char) code2;
					if (cc == '[' || cc == '(') {
					}
				}
				if (!ansiControl) {
					if (c == '\r') {
						sb.append(",");
						if (sb.lastIndexOf(str) != -1) {
							break;
						}
					} else if (c == '\n')
						;
					else
						sb.append(c);
					if (sb.lastIndexOf(str) != -1) {
						break;
					}
				}

				if (ansiControl) {
					if (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || c == '"') {
						ansiControl = false;
					}
				}
			}
			return new String(sb.toString().getBytes(ORIG_CODEC), TRANSLATE_CODEC);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public void write(String s) {
		try {
			out.write(s.getBytes());
			out.flush();
		} catch (Exception e) {
		}
	}

	public void write(String s, int sleep) {
		write(s);
		try {
			Thread.sleep(sleep);
		} catch (Exception e) {
		}
	}

	
	public void close() {
		if (out != null)
			out.close();
		if (in != null)
			try {
				in.close();
			} catch (IOException e1) {
			}
		if (telnet != null)
			try {
				telnet.disconnect();;
			} catch (Exception e) {
			}
	}


	
	public String doJob(String user, String caseName) throws Exception {
		try {
				String tmsSql = "tmsquery -y select level where tidnum=\""+caseName.split("/")[1].replaceAll(".json","")+"\"\n";
				write(tmsSql);
				String result = readResult("apxtms: /home/"+user+">");
				return result;
		} finally {
			Thread.sleep(200);
			close();
		}
	}
	
	public boolean ping(String ip) throws UnknownHostException, IOException{
		int  timeOut =  5000;   
		boolean status = InetAddress.getByName(ip).isReachable(timeOut);
		return status;
	}
	
	public static void main(String[] args) throws Exception {
		String hostname = "135.3.27.70";
		int hostport = 23;
		
		String userAD4 = "svc_surepay";
		String passwordAD4 = "asb#2345"; 
		
		String user = "yahou";//yanzhizh yahou
		String password = "tyz89bgv";//yanzhizh tyz89bgv 
		TelnetCla helper = null;
		helper = new TelnetCla(hostname, hostport, user, password, userAD4, passwordAD4);
		System.out.println(helper.readUntil("apxtms: /home/yahou>"));
		helper.close();
	}
}
