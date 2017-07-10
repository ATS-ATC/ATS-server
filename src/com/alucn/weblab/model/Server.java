package com.alucn.weblab.model;

/**
 * @author haiqiw
 * 2017年6月7日 下午4:18:06
 * desc:Server
 */
public class Server {

	private String serverName;
	private String serverIp;
	private String serverRelease;
	private String serverProtocol;
	private String serverSPA;
	private String serverRTDB;
	
	
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getServerIp() {
		return serverIp;
	}
	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
	public String getServerRelease() {
		return serverRelease;
	}
	public void setServerRelease(String serverRelease) {
		this.serverRelease = serverRelease;
	}
	public String getServerProtocol() {
		return serverProtocol;
	}
	public void setServerProtocol(String serverProtocol) {
		this.serverProtocol = serverProtocol;
	}
	public String getServerSPA() {
		return serverSPA;
	}
	public void setServerSPA(String serverSPA) {
		this.serverSPA = serverSPA;
	}
	public String getServerRTDB() {
		return serverRTDB;
	}
	public void setServerRTDB(String serverRTDB) {
		this.serverRTDB = serverRTDB;
	}
	
	@Override
	public String toString() {
		return "Server [serverName=" + serverName + ", serverIp=" + serverIp + ", serverRelease=" + serverRelease
				+ ", serverProtocol=" + serverProtocol + ", serverSPA=" + serverSPA + ", serverRTDB=" + serverRTDB
				+ "]";
	}
}
