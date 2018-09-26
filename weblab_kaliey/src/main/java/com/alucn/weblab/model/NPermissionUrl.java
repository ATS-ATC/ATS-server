package com.alucn.weblab.model;

public class NPermissionUrl {
	private int id;
	private String permission_name;
	private String url;
	private String remark;
	private String stateflag;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPermission_name() {
		return permission_name;
	}
	public void setPermission_name(String permission_name) {
		this.permission_name = permission_name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
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
	public NPermissionUrl(int id, String permission_name, String url, String remark, String stateflag) {
		super();
		this.id = id;
		this.permission_name = permission_name;
		this.url = url;
		this.remark = remark;
		this.stateflag = stateflag;
	}
	public NPermissionUrl() {
		super();
	}
	@Override
	public String toString() {
		return "NPermissionUrl [id=" + id + ", permission_name=" + permission_name + ", url=" + url + ", remark=" + remark
				+ ", stateflag=" + stateflag + "]";
	}
	
	
}	
