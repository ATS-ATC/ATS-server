package com.alucn.weblab.model;

public class NServer {

	private String serverName;
	private String serverIp;
	private String serverRelease;
	private String serverProtocol;
	private String serverSPA;
	private String serverRTDB;
	private int createUserId;
	private int createDeptId;
	
	
	public int getCreateDeptId() {
		return createDeptId;
	}
	public void setCreateDeptId(int createDeptId) {
		this.createDeptId = createDeptId;
	}
	public int getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}
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
		return "NServer [serverName=" + serverName + ", serverIp=" + serverIp + ", serverRelease=" + serverRelease
				+ ", serverProtocol=" + serverProtocol + ", serverSPA=" + serverSPA + ", serverRTDB=" + serverRTDB
				+ ", createUserId=" + createUserId + ", createDeptId=" + createDeptId + "]";
	}
	
}
