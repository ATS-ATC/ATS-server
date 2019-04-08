package com.alucn.weblab.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;
import org.springframework.beans.factory.annotation.Autowired;

import com.alucn.casemanager.server.common.util.Fiforeader;
import com.alucn.weblab.service.CaseSearchService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class StringUtil {
	
	public static void main(String[] args) throws NumberFormatException, InterruptedException, IOException {
		String server ="[\"BJRMS21B\",\"BJRMS21D\",\"CHSP05B\"]";
		
		String login ="root";
		String caseLogId ="1";
		/*String string = "CTRTDB,XBRTDB,GNRTDB,SGLDB,SGLRTDB,SIMDB";
		String jsonString = formatJsonString(string);
		System.out.println(jsonString);*/
		/*String splitCondition = splitCondition("DailyCase;;;;;;;;;;;ANSI;;BJRMS21B,BJRMS21D,CHSP05B;","","case_name,'Y','"+server +"','"+login +"',now(),"+caseLogId );
		System.out.println(splitCondition);*/
	}
	/**
	 * 
	 * <pre>
	 * Example: 
	 * Description: 
	 * 				  1,2,3
	 *              ↓ ↓ ↓ ↓ ↓
	 *             '1','2','3'
	 * Arguments: 
	 * Return: String
	 * Variable：
	 * </pre>
	 */
	public static String formatSplitList(List<String> str) {
		String inString = "";
		if(str.size()>0) {
			for (int i =0 ;i<str.size();i++) {
				if(i==str.size()-1) {
					inString=inString+"'"+str.get(i)+"'";
				}else {
					inString=inString+"'"+str.get(i)+"',";
				}
			}
		}
		//System.out.println(inString);
		return inString;
	}
	/**
	 * 
	 * <pre>
	 * Example: StringUtil.formatJsonString(string);
	 * Description: 
	 * 				"CTRTDB,XBRTDB,GNRTDB,SGLDB,SGLRTDB,SIMDB"
	 * 					↓↓↓↓↓↓↓↓↓↓↓↓转换为如下格式↓↓↓↓↓↓↓↓↓
	 * 				["CTRTDB","XBRTDB","GNRTDB","SGLDB","SGLRTDB","SIMDB"]
	 * Arguments: 
	 * Return: String
	 * Variable：
	 * </pre>
	 */
	public static String formatJsonString(Object object) {
		
		if(object instanceof String) {
			if(object!=null&&!"".equals(object)) {
				String string = (String)object;
				String result= "[";
				if(string.contains(",")) {
					String[] split = string.split(",");
					int i=0;
					for (String str : split) {
						if(i!=split.length-1) {
							result=result+"\""+str+"\",";
						}else {
							result=result+"\""+str+"\"]";
						}
						i++;
					}
				}else {
					result=result+"\""+string+"\"]";
				}
				return result;
			}else {
				return "";
			}
		}
		
		return null;
	}
	
	
	
}
