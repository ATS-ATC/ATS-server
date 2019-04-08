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
		getLabStatus();
	}
	
	public static String getLabStatus() {
		JSONObject jsonFile = getJsonFile("http://135.242.16.160:8000/auto-test/api/get_lab_info");
		if(!jsonFile.isEmpty()) {
			Object result = jsonFile.getJSONObject("data").get("result");
			if("SUCCESS".equals(result)) {
				JSONArray jsonArray = jsonFile.getJSONObject("data").getJSONArray("msg");
				System.out.println(jsonArray);
			}
		}
		
		return null;
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
