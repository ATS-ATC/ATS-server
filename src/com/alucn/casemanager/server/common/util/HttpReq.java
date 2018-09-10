package com.alucn.casemanager.server.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import net.sf.json.JSONObject;

public class HttpReq {
	
	public static JSONObject reqUrl(String httpUrl) {
		JSONObject infos = new JSONObject();
		URL url;
		try {
			url = new URL(httpUrl);
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
			}
			infos = JSONObject.fromObject(response);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return infos;
	}
	
	
	public static String reqUrl(String httpUrl, String data) {
		URL url;
		String response = "";
		try {
			url = new URL(httpUrl);
			InputStream inputStream = null;
			InputStreamReader inputStreamReader = null;
			BufferedReader reader = null;
			String tempLine;
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("PUT");
			connection.setRequestProperty("Content-Type", " application/json");
			connection.setDoOutput(true);
			OutputStream out = connection.getOutputStream();
			out.write(data.getBytes());
			out.flush();
			out.close();

			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				inputStream = connection.getInputStream();
				inputStreamReader = new InputStreamReader(inputStream);
				reader = new BufferedReader(inputStreamReader);
				while ((tempLine = reader.readLine()) != null) {
					response += tempLine;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}
}
