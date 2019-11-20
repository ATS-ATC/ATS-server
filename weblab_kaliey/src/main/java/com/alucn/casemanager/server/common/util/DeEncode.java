package com.alucn.casemanager.server.common.util;

import java.io.UnsupportedEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.misc.BASE64Decoder;     
import sun.misc.BASE64Encoder;

public class DeEncode {
	private static Log log = LogFactory.getLog(DeEncode.class);	
		public static String getBase64(String str) {
			byte[] b = null;
			String s = null;
			try {
				b = str.getBytes("utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (b != null) {
				s = new BASE64Encoder().encode(b);
			}
			return s;
		}

		// ����
		public static String getFromBase64(String s) {
			byte[] b = null;
			String result = null;
			if (s != null) {
				BASE64Decoder decoder = new BASE64Decoder();
				try {
					b = decoder.decodeBuffer(s);
					result = new String(b, "utf-8");
				} catch (Exception e) {
					log.error(e);
				}
			}
			return result;
		}
	/** 
	 * ���� 
	 *  
	 * @param content ��Ҫ���ܵ����� 
	 * @param password  �������� 
	 * @return 
	 */  
	
	/**��������ת����16���� 
	 * @param buf 
	 * @return 
	 */  
	public static String parseByte2HexStr(String str) {  
			byte[] buf;
			StringBuffer sb = new StringBuffer();
			try {
				buf = str.getBytes("utf-8");
				  
		        for (int i = 0; i < buf.length; i++) {  
		                String hex = Integer.toHexString(buf[i] & 0xFF);  
		                if (hex.length() == 1) {  
		                        hex = '0' + hex;  
		                }  
		                sb.append(hex.toUpperCase());  
		        }  
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				log.error(e);
			}
	        
	        return sb.toString();  
	}  
	
	/**��16����ת��Ϊ������ 
	 * @param hexStr 
	 * @return 
	 */  
	public static byte[] parseHexStr2Byte(String hexStr) {  
	        if (hexStr.length() < 1)  
	                return null;  
	        byte[] result = new byte[hexStr.length()/2];  
	        for (int i = 0;i< hexStr.length()/2; i++) {  
	                int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);  
	                int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);  
	                result[i] = (byte) (high * 16 + low);  
	        }  
	        return result;  
	}  
	
}