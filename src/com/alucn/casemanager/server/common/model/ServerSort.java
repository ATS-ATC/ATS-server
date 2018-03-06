package com.alucn.casemanager.server.common.model;

import java.io.Serializable;
import java.util.Map;

import net.sf.json.JSONObject;

public class ServerSort implements Comparable<ServerSort>,Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<String,JSONObject> map;

	public Map<String, JSONObject> getMap() {
		return map;
	}

	public void setMap(Map<String, JSONObject> map) {
		this.map = map;
	}


	@Override
	public int compareTo(ServerSort o) {
		String one = "";
		String two = "";
		for(String key1 : o.getMap().keySet()){
			one = key1;
			break;
		}
		for(String key2 : getMap().keySet()){
			two = key2;
			break;
		}
		return -one.compareTo(two);
	} 
}
