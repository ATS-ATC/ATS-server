package com.alucn.casemanager.server.common.util;


import java.lang.reflect.InvocationTargetException;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

import com.alucn.casemanager.server.common.ConfigProperites;
import com.alucn.casemanager.server.common.constant.Constant;


public class ParamUtil {
	public static Logger logger = Logger.getLogger(ParamUtil.class);
	
	public static String getErrMsgStrOfOriginalException(Throwable e){
		StringBuffer errmsg = new StringBuffer();
		if(null != e){
			errmsg.append(e.getClass() + "	" + e.getMessage()+"\n");
		}
		
		if(null != e && null != e.getStackTrace() && e.getStackTrace().length > 0){
			for (StackTraceElement ste : e.getStackTrace()) {
				errmsg.append(ste.toString()+"\n");
			}
		}
		return errmsg.toString();
	}
	
	public static String getCurrentThreadId(){
		return String.valueOf(Thread.currentThread().getId());
	}
	
	/**
	 * Method for obtaining system parameter without implementing refresh
	 * @param configKey
	 * @return
	 */
	public static String getUnableDynamicRefreshedConfigVal(String configKey){
		return ConfigProperites.getInstance().getSingletonProperitesVal(configKey);
	}
	/**
	 *Parse message header
	 * @param reqXml
	 * @return
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws DocumentException 
	 * @throws TranException 
	 */
	public static JSONObject getReqHead(String reqJson) throws Exception{
		//get head
		JSONObject jHead = JSONObject.fromObject(reqJson).getJSONObject(Constant.HEAD);
		//According to the reflection to the request message header
		/*Head reqHead = new Head();
		Field[] fields = Head.class.getDeclaredFields();
		for (Field f : fields) {
			String methodName = "set"+f.getName().substring(0, 1).toUpperCase()+f.getName().substring(1).toLowerCase();
			Method m = Head.class.getMethod(methodName, f.getType());
			if(null == jHead.getString(f.getName())){
				throw new Exception();
			}
			m.invoke(reqHead, jHead.getString(f.getName()));
		}
		return reqHead;*/
		return jHead;
	}
	
	public static JSONObject getReqBody(String reqJson) throws Exception{
		JSONObject jBody = JSONObject.fromObject(reqJson).getJSONObject(Constant.BODY);
		return jBody;
	}
}
