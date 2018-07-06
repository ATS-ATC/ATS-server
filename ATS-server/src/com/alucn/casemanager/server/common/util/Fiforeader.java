package com.alucn.casemanager.server.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Fiforeader {
	public static String readCaseInfoFromChannel(String fifoPath) throws NumberFormatException, InterruptedException, IOException{
		FileInputStream inputStream = new FileInputStream(fifoPath);
		String result=null;
		byte[] bs = new byte[4096];
		int n = 0;
		while ((n = inputStream.read(bs)) != -1) {
			result = new String(bs, 0, n);
		}
		inputStream.close();
		return result;
	}
	
	public static String readLastLine(String filePath) throws IOException {
		  File file = new File(filePath);
		  if (!file.exists() || file.isDirectory() || !file.canRead()) {  
		    return null;  
		  }  
		  RandomAccessFile raf = null;  
		  try {  
		    raf = new RandomAccessFile(file, "r");  
		    long len = raf.length();  
		    if (len == 0L) {  
		      return "";  
		    } else {  
		      long pos = len - 1;  
		      while (pos > 0) {  
		        pos--;  
		        raf.seek(pos);  
		        if (raf.readByte() == '\n') {  
		          break;  
		        }  
		      }  
		      if (pos == 0) {  
		        raf.seek(0);  
		      }  
		      byte[] bytes = new byte[(int) (len - pos)];  
		      raf.read(bytes);  
		        return new String(bytes);  
		    }  
		  } catch (FileNotFoundException e) {  
		     e.printStackTrace();
		  } finally {  
		    if (raf != null) {  
		      try {  
		        raf.close();  
		      } catch (Exception ea) { 
		          ea.printStackTrace(); 
		      }  
		    }  
		  }  
		  return null;
	}
	public static void main(String[] args) throws NumberFormatException, InterruptedException, IOException {
		System.out.println(Fiforeader.readCaseInfoFromChannel("D:/test/test.txt"));
	}
}
