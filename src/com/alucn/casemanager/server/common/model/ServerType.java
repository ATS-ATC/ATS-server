package com.alucn.casemanager.server.common.model;

public enum ServerType {
	LINE("Line"), GROUP("Group");
	
	private String name;
	
	private ServerType(String name) {  
		this.name = name;  
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
