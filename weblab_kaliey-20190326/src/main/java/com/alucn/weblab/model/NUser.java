package com.alucn.weblab.model;

import java.util.List;

public class NUser {
	
	private int id;
	private String username;
	private String password;
	private List<Integer> roleIdList; 
	private int deptid;
	private String stateflag;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public List<Integer> getRoleIdList() {
		return roleIdList;
	}
	public void setRoleIdList(List<Integer> roleIdList) {
		this.roleIdList = roleIdList;
	}
	public int getDeptid() {
		return deptid;
	}
	public void setDeptid(int deptid) {
		this.deptid = deptid;
	}
	public String getStateflag() {
		return stateflag;
	}
	public void setStateflag(String stateflag) {
		this.stateflag = stateflag;
	}
	public NUser(int id, String username, String password, List<Integer> roleIdList, int deptid, String stateflag) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.roleIdList = roleIdList;
		this.deptid = deptid;
		this.stateflag = stateflag;
	}
	public NUser() {
		super();
	}
	@Override
	public String toString() {
		return "NUser [id=" + id + ", username=" + username + ", password=" + password + ", roleIdList=" + roleIdList
				+ ", deptid=" + deptid + ", stateflag=" + stateflag + "]";
	}
	
	
	
	
	
}
