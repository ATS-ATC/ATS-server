package test.nokia.request;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Test {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long createtime = Long.parseLong("1556243489973");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		Date date = new Date(createtime);
		String format = simpleDateFormat.format(date);
		System.out.println(format);
	}

}
