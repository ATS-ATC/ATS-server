package com.alucn.weblab.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class LabStatusUtil {
	public static void main(String[] args) {
		LabStatusUtil.getLabStatus();
	}
	
	public static JSONArray getLabStatus() {
		JSONObject jsonFile = getJsonFile("http://135.242.16.160:8000/auto-test/api/get_lab_info");
		JSONArray jsonArray = new JSONArray();
		if(!jsonFile.isEmpty()) {
			Object result = jsonFile.getJSONObject("data").get("result");
			if("SUCCESS".equals(result)) {
				jsonArray = jsonFile.getJSONObject("data").getJSONArray("msg");
			}
		}
		JSONArray infos = new JSONArray();
		if(!"[]".equals(jsonArray.toString()) && !"".equals(jsonArray.toString())) {
			for(int i=0; i<jsonArray.size(); i++){
				
				JSONObject body = new JSONObject();
				
				JSONObject lab = jsonArray.getJSONObject(i);
				body.put("lab", lab);
				
				JSONObject taskStatus = new JSONObject();
				taskStatus.put("status",lab.getString("status"));
				try {
					taskStatus.put("runningCase",lab.getString("runningCase"));
				} catch (Exception e) {
					e.printStackTrace();
					taskStatus.put("runningCase","");
				}
				body.put("taskStatus", taskStatus);
				
				body.put("taskResult","{\"success\":[],\"fail\":[]}");
				
				JSONObject temp = new JSONObject();
				temp.put("head", "{\"reqType\": \"getlabstatus\",\"response\": \"\"}");
				temp.put("body",body);
				infos.add(temp);
			}
		}
		return infos;
	}
	public static JSONObject getJsonFile(String inUrl) {
		URL url;
		JSONObject tagInfos = new JSONObject();
		try {
			url = new URL(inUrl);
			InputStream inputStream = null;
			InputStreamReader inputStreamReader = null;
			BufferedReader reader = null;
			String tempLine, response = "";
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				inputStream = connection.getInputStream();
				inputStreamReader = new InputStreamReader(inputStream);
				reader = new BufferedReader(inputStreamReader);

				while ((tempLine = reader.readLine()) != null) {
					response += tempLine;

				}

				tagInfos = JSONObject.fromObject(response);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tagInfos;
	}
}
