package com.alucn.weblab.model;

public class NRolePermission {
	private int id;
	private int role_id;
	private int permission_id;
	private String stateflag;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getRole_id() {
		return role_id;
	}
	public void setRole_id(int role_id) {
		this.role_id = role_id;
	}
	public int getPermission_id() {
		return permission_id;
	}
	public void setPermission_id(int permission_id) {
		this.permission_id = permission_id;
	}
	public String getStateflag() {
		return stateflag;
	}
	public void setStateflag(String stateflag) {
		this.stateflag = stateflag;
	}
	public NRolePermission(int id, int role_id, int permission_id, String stateflag) {
		super();
		this.id = id;
		this.role_id = role_id;
		this.permission_id = permission_id;
		this.stateflag = stateflag;
	}
	public NRolePermission() {
		super();
	}
	@Override
	public String toString() {
		return "NRolePermission [id=" + id + ", role_id=" + role_id + ", permission_id=" + permission_id
				+ ", stateflag=" + stateflag + "]";
	}
	
}
