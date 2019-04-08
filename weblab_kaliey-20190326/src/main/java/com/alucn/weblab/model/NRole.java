package com.alucn.weblab.model;

public class NRole {
	private int id;
	private String role_name;
	private String remark;
	private String stateflag;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getRole_name() {
		return role_name;
	}
	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getStateflag() {
		return stateflag;
	}
	public void setStateflag(String stateflag) {
		this.stateflag = stateflag;
	}
	public NRole(int id, String role_name, String remark, String stateflag) {
		super();
		this.id = id;
		this.role_name = role_name;
		this.remark = remark;
		this.stateflag = stateflag;
	}
	public NRole() {
		super();
	}
	@Override
	public String toString() {
		return "NRole [id=" + id + ", role_name=" + role_name + ", remark=" + remark + ", stateflag=" + stateflag + "]";
	}
	
}
