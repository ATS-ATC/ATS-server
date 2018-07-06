package com.alucn.casemanager.server.common.model;

public enum ServerMate {
	PRIMARY("Primary"), SECONDARY("Secondary"), N("N");
	
	private String name;
	
	private ServerMate(String name) {  
		this.name = name;  
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
