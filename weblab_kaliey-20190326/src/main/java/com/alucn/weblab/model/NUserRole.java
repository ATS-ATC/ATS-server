package com.alucn.weblab.model;

public class NUserRole {
	private int id;
	private int user_id;
	private int role_id;
	private String stateflag;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public int getRole_id() {
		return role_id;
	}
	public void setRole_id(int role_id) {
		this.role_id = role_id;
	}
	public String getStateflag() {
		return stateflag;
	}
	public void setStateflag(String stateflag) {
		this.stateflag = stateflag;
	}
	public NUserRole(int id, int user_id, int role_id, String stateflag) {
		super();
		this.id = id;
		this.user_id = user_id;
		this.role_id = role_id;
		this.stateflag = stateflag;
	}
	public NUserRole() {
		super();
	}
	@Override
	public String toString() {
		return "NUserRole [id=" + id + ", user_id=" + user_id + ", role_id=" + role_id + ", stateflag=" + stateflag
				+ "]";
	}
	
}
