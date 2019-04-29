package com.alucn.weblab.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
public class TimeUtil {

    public TimeUtil() {
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args){
        long startTime = 1516636799;
        long endTime = 1516636800;
        System.out.println(equation(startTime * 1000, System.currentTimeMillis()));
        System.out.println(System.currentTimeMillis());
    }
    //计算两个时间戳间隔多少天
    public static int equation(long startTime, long endTime) {
        startTime = dateToStamp(stampToDate(startTime));
        endTime = dateToStamp(stampToDate(endTime));
        int newL = (int) ((endTime - startTime) / (1000 * 3600 * 24));
        return newL;

    }
    public static String getTimeDifference(String strTime1,String strTime2) {
		//格式日期格式，在此我用的是"2018-01-24 19:49:50"这种格式
		//可以更改为自己使用的格式，例如：yyyy/MM/dd HH:mm:ss 。。。
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		   try{
			   Date now = df.parse(strTime1);
			   Date date=df.parse(strTime2);
			   long l=now.getTime()-date.getTime();       //获取时间差
			   long day=l/(24*60*60*1000);
			   long hour=(l/(60*60*1000)-day*24);
			   long min=((l/(60*1000))-day*24*60-hour*60);
			   long s=(l/1000-day*24*60*60-hour*60*60-min*60);
			   //System.out.println(""+day+"day"+hour+"hour"+min+"min"+s+"s");
			   return ""+day+"/"+hour+":"+min+":"+s+"";
		   }catch(Exception e){
			   e.printStackTrace();
		   }
		   return "";
	}
    public static String getTimeDifference(long now,long start) {
    	try{
    		long l=now-start;       //获取时间差
    		long day=l/(24*60*60*1000);
    		long hour=(l/(60*60*1000)-day*24);
    		long min=((l/(60*1000))-day*24*60-hour*60);
    		long s=(l/1000-day*24*60*60-hour*60*60-min*60);
    		//System.out.println(""+day+"day"+hour+"hour"+min+"min"+s+"s");
    		return ""+day+"/"+hour+":"+min+":"+s+"";
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return "";
    }
    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(long l) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long lt = l;
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }
    public static String stampToTime(long l) {
    	String res;
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
    	long lt = l;
    	Date date = new Date(lt);
    	res = simpleDateFormat.format(date);
    	return res;
    }

    /*
     * 将时间转换为时间戳
     */
    public static long dateToStamp(String s) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
            return date.getTime();
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block

            e.printStackTrace();
            return -1;
        }

    }
}
