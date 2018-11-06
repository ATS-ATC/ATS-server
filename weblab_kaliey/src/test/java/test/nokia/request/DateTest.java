package test.nokia.request;

import java.sql.Timestamp;
import java.util.Date;

public class DateTest {
	public static void main(String[] args) throws InterruptedException {
		//Timestamp timestamp = new Timestamp(new Date().getTime());
		//System.err.println(timestamp.toString());
		//System.err.println(new Date().getTime());
		
		
		
		long lcreatetime=new Date().getTime();
		System.out.println(lcreatetime+"");
		Thread.sleep(1000*10);
		System.out.println(lcreatetime+"");
		
	}
}
