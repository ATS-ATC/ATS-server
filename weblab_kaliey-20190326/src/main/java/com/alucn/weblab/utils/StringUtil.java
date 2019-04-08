package com.alucn.weblab.utils;

import java.util.List;

public class StringUtil {
	
	public static void main(String[] args) {
		String string = "CTRTDB,XBRTDB,GNRTDB,SGLDB,SGLRTDB,SIMDB";
		String jsonString = formatJsonString(string);
		System.out.println(jsonString);
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
