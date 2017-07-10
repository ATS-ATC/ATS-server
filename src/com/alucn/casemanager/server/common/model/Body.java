package com.alucn.casemanager.server.common.model;

import java.util.List;
import java.util.Map;


/**
 * 响应报文体
 * @author wanghaiqi
 *
 */
public class Body {

	private String status;             //当前客户端状态 1：idle 2:running 3:die
	private String release;            //版本
	private String protocol;           //协议
	private String spa;                //spa
	private String rtdb;               //rtdb
	private String casenum;            //case数量
	private String completedcasenum;   //已经完成的case数量
	private String currentcasename;    //当前正在跑的case名称
	private List<Map<String,String>> casestatus;       //case状态
	
	
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRelease() {
		return release;
	}
	public void setRelease(String release) {
		this.release = release;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getSpa() {
		return spa;
	}
	public void setSpa(String spa) {
		this.spa = spa;
	}
	public String getRtdb() {
		return rtdb;
	}
	public void setRtdb(String rtdb) {
		this.rtdb = rtdb;
	}
	public String getCasenum() {
		return casenum;
	}
	public void setCasenum(String casenum) {
		this.casenum = casenum;
	}
	public String getCompletedcasenum() {
		return completedcasenum;
	}
	public void setCompletedcasenum(String completedcasenum) {
		this.completedcasenum = completedcasenum;
	}
	public String getCurrentcasename() {
		return currentcasename;
	}
	public void setCurrentcasename(String currentcasename) {
		this.currentcasename = currentcasename;
	}
	public List<Map<String, String>> getCasestatus() {
		return casestatus;
	}
	public void setCasestatus(List<Map<String, String>> casestatus) {
		this.casestatus = casestatus;
	}
	@Override
	public String toString() {
		return "Body [status=" + status + ", release=" + release
				+ ", protocol=" + protocol + ", spa=" + spa + ", rtdb=" + rtdb
				+ ", casenum=" + casenum + ", completedcasenum="
				+ completedcasenum + ", currentcasename=" + currentcasename
				+ ", casestatus=" + casestatus + "]";
	}
}
