package com.alucn.casemanager.server.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.aspectj.apache.bcel.generic.ReturnaddressType;
import org.springframework.http.StreamingHttpOutputMessage;
import org.sqlite.JDBC;

import com.alucn.casemanager.server.common.ConfigProperites;
import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.weblab.utils.JDBCHelper;
import com.thoughtworks.selenium.condition.Not;

import mx4j.tools.config.DefaultConfigurationBuilder.New;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author haiqiw
 * 2017年7月6日 下午4:50:59
 * desc:SendMail
 */
public class SendMail {
	
	public static Logger logger = Logger.getLogger(SendMail.class);
    private String[] recipients=null;
    private String subject=null;
    private String context=null;
    
	public SendMail(String[] recipients, String subject, String context) {
		this.recipients = recipients;
		this.subject = subject;
		this.context = context;
	}

	public void sendEmail() throws MessagingException, UnsupportedEncodingException {
		if (recipients != null && recipients.length > 0) {
			System.setProperty("java.net.preferIPv4Stack", "true");
			Properties pro = new Properties();
			pro.put("mail.transport.protocol", "smtp");
			pro.put("mail.smtp.host", "172.24.146.133");
			pro.put("mail.smtp.connectiontimeout", "10000");
			pro.put("mail.smtp.timeout", "10000");
			pro.setProperty("mail.smtp.from", "lei.k.huang@alcatel-lucent.com");
			pro.setProperty("mail.smtp.auth", "false");

			// create session
			Session session = Session.getInstance(pro);
			// Session session=Session.getDefaultInstance(pro,null);
			// Session session=Session.getDefaultInstance(pro,authentication);
			session.setDebug(true);
			Transport ts;
			ts = session.getTransport();
			ts.connect();
			Message message = new MimeMessage(session);
			InternetAddress addressFron = new InternetAddress("No-Reply.SurepayCaseServer@alcatel-lucent.com");
			message.setFrom(addressFron);
			InternetAddress[] addressTo = new InternetAddress[recipients.length];
			for (int i = 0; i < recipients.length; i++) {
				addressTo[i] = new InternetAddress(recipients[i]);
			}
			message.setRecipients(Message.RecipientType.TO, addressTo);
			subject = MimeUtility.encodeText(new String(subject.getBytes(), "GB2312"), "GB2312", "B");
			message.setSubject(subject);
			message.setContent(context, "text/html;charset=UTF-8");
			ts.sendMessage(message, message.getAllRecipients());
			ts.close();
		}

	}

	public static void Send(String title, String info, JSONArray to_mail_list, JSONArray cc_mail_list){
		if(to_mail_list.size() > 0){
            System.setProperty("java.net.preferIPv4Stack", "true");
            Properties prop = new Properties();
            prop.setProperty("mail.transport.protocol", "smtp");
            prop.setProperty("mail.smtp.host", "172.24.146.133");
            prop.setProperty("mail.smtp.from", "lei.k.huang@alcatel-lucent.com");
            prop.setProperty("mail.smtp.auth", "false");
            Session session = Session.getInstance(prop);
            session.setDebug(true);
            Transport ts;
            try {
                ts = session.getTransport();
                ts.connect();
                Message message = createSimpleMail(session,title,info, to_mail_list, cc_mail_list);
                ts.sendMessage(message, message.getAllRecipients());
                ts.close();
                logger.info("[send mail to: ]" + to_mail_list.toString());
            } catch (NoSuchProviderException e) {
            	logger.error(e);
            }catch (MessagingException e) {
            	logger.error(e);
            } catch (Exception e) {
            	logger.error(e);
            }
        }else{
        	logger.error("[Mail to list null.]");
        }
        
    }
    
    
 
    public static MimeMessage createSimpleMail(Session session, String Tilte, String subject, JSONArray to_mail_list, JSONArray cc_mail_list)
            throws Exception {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress("No-Reply.SurepayCaseServer@alcatel-lucent.com"));
        for(int i = 0; i < to_mail_list.size(); i++){
            try{
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to_mail_list.getString(i)));
            }catch (MessagingException e){
            	logger.error(e);
            }
        }
        
        for(int i = 0; i < cc_mail_list.size(); i++){
            try{
                message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc_mail_list.getString(i)));
            }catch (MessagingException e){
            	logger.error(e);
            }
        }
        
        message.setSubject(Tilte);
        message.setContent(subject, "text/html;charset=UTF-8");
        return message;
    }
	
    
    public static void genCertifyReport(JSONArray to_list, JSONArray cc_list, ArrayList<HashMap<String, Object>> report, String type) {
        Calendar cal=Calendar.getInstance();
        if("normal".equals(type))
        {
            int DayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            if(DayOfWeek == 6)
            {
                cal.add(Calendar.DAY_OF_YEAR,4);
            }
            else{
                cal.add(Calendar.DAY_OF_YEAR,2);
            }
        }
       
        Date dt1=cal.getTime();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
        String COBdate = sdf.format(dt1);

        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<head>");
        sb.append("<title>Report</title>");
        sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
        sb.append("<style type=\"text/css\">");
        sb.append("body { margin:0 auto; padding:20px; font:14px;}");
        sb.append("span { font:15px;}");
        sb.append("p, table, caption, td, tr, th {margin:0 auto; padding:0; font-weight:normal;}");
        sb.append("p {margin-bottom:15px;}");
        sb.append("table {border-collapse:collapse; margin-bottom:15px; width:70%;}");
        sb.append("caption {text-align:left; font-size:15px; padding-bottom:10px; }");
        sb.append("table td, table th { text-align:center; padding:5px; border:1px solid #fff; border-width:0 1px 1px 0;}");
        sb.append("thead th { background:#91c5d4; }");
        sb.append("thead th[colspan], thead th[rowspan] { background:#66a9bd; }");
        sb.append("tbody th, tfoot th { text-align:left; background:#91c5d4; }");
        sb.append("tbody td,tfoot td {text-align:center; background:#d5eaf0;}");
        sb.append("tfoot th { background:#b0cc7f;}");
        sb.append("tfoot td {background:#d7e1c5; font-weight:bold;}");
        sb.append("tbody tr:nth-child(odd) td {background: #F5FAFA;}");
        sb.append("tbody tr.odd td {background:#bcd9e1;}");
        sb.append("</style></head>");
        sb.append("<body>");
        if ("reminder".equals(type))
        {
            sb.append("<p><span style=\"background:#cccc33;color:red;font:16px\">Reminder!!</span></p>");
            sb.append("<p><span></span></p>");
        }
        else if("expired".equals(type))
        {
            sb.append("<p><span style=\"background:#cccc33;color:red;font:16px\">Expired!!</span></p>");
            sb.append("<p><span></span></p>");
        }
        sb.append("<p><span>Dear All Testers,</span></p>");
        sb.append("<p><span></span></p>");
        sb.append("<p><span>Your submitted auto cases are certified fail (<span style='color:red'>but some features failure reason checking is Pending for a long time</span>).</span></p>");
        sb.append("<p><span></span></p>");
        sb.append("<p><span>please login <a href=\"http://135.242.16.160:8080/weblab\">http://135.242.16.160:8080/weblab</a>  -> Bad Case Management (via FireFox & NSN-INTRA) to get the detailed fail cases list & failure html report.</span></p>");
        sb.append("<p><span></span></p>");
        
        sb.append("<p><span>please re-login above web link <span style=\"background:#cccc33;\"> before <span style='color:red'>COB of " + COBdate + "</span></span> to submit your checked " + 
                "<span style='color:red'>'Failed Reason'</span> & <span style='color:red'>'Target Fix Date'</span> & necessary details in the <span style='color:red'>'Failed Describe'</span></span></p>");
        sb.append("<p><span></span></p>");
        sb.append("<p><span></span></p>");
        sb.append("<div align=\"left\">");
        sb.append("<br/>");
        sb.append("<br/>");

        sb.append("<table align=\"left\">");
        sb.append("<thead>");
        sb.append("<tr>");

        sb.append("<th>FeatureID</th>");
        sb.append("<th>FTC</th>");
        sb.append("<th>TargetSub</th>");
        sb.append("<th>TotalSub</th>");
        sb.append("<th>TotalFailed</th>");
        sb.append("<th>FailNotChecked</th>");
        sb.append("<th>Owner</th>");
        sb.append("</tr>");
        sb.append("</thead>");
        sb.append("<tbody>");

        for (int i = 0; i < report.size(); i++) {
            sb.append("<tr>");
            sb.append("<td style=\"width:15%\">" + report.get(i).get("feature_number").toString() + "</td>");
            sb.append("<td style=\"width:15%\">" + report.get(i).get("ftc_date").toString() + "</td>");
            sb.append("<td style=\"width:10%\"> " + report.get(i).get("case_num").toString() + "</td>");
            sb.append("<td style=\"width:15%\"> " + report.get(i).get("dft") + "</td>");
            sb.append("<td style=\"width:15%\"> " + report.get(i).get("fail") + "</td>");
            sb.append("<td style=\"background:#cccc33;width:15%\"> " + report.get(i).get("uncheck") + "</td>");
            sb.append("<td style=\"width:15%\"> " + report.get(i).get("user_name") + "</td>");
            sb.append("</tr>");

        }
        sb.append("</tbody>");
        sb.append("</table>");
        sb.append("<br/><br/>");
        sb.append("</div></body></html>");

        /*try {
            PrintStream printStream = new PrintStream(new FileOutputStream(WebServer.ServerViewPath + File.separator
                    + "static" + File.separator + "html" + File.separator + "caseCertified.html"));
            printStream.println(sb.toString());
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            logger.error(e);
        }*/

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String dateTime = df.format(new Date());
//      logger.info(sb.toString());
        String title = "Auto certify failed cases failure reason check " + dateTime;
        if ("reminder".equals(type))
        {
            title = "Reminder!! " + title;
        }
        else if("expired".equals(type))
        {
            title = "Expired!! " + title;
        }
        
        SendMail.Send(title, sb.toString(), to_list, cc_list);
    }
	
	public static void genReport(JSONArray cc_list, JSONArray to_list, JSONArray report, JSONObject buildInfo) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<head>");
		sb.append("<title>Report</title>");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
		sb.append("<style type=\"text/css\">");
		sb.append("body { margin:0 auto; padding:20px; font:13px;}");
		sb.append("p, table, caption, td, tr, th {margin:0 auto; padding:0; font-weight:normal;}");
		sb.append("p {margin-bottom:15px;}");
		sb.append("table {border-collapse:collapse; margin-bottom:15px; width:70%;}");
		sb.append("caption {text-align:left; font-size:15px; padding-bottom:10px; }");
		sb.append("table td, table th { text-align:center; padding:5px; border:1px solid #fff; border-width:0 1px 1px 0;}");
		sb.append("thead th { background:#91c5d4; }");
		sb.append("thead th[colspan], thead th[rowspan] { background:#66a9bd; }");
		sb.append("tbody th, tfoot th { text-align:left; background:#91c5d4; }");
		sb.append("tbody td,tfoot td {text-align:center; background:#d5eaf0;}");
		sb.append("tfoot th { background:#b0cc7f;}");
		sb.append("tfoot td {background:#d7e1c5; font-weight:bold;}");
		sb.append("tbody tr:nth-child(odd) td {background: #F5FAFA;}");
		sb.append("tbody tr.odd td {background:#bcd9e1;}");
		sb.append("</style></head>");
		sb.append("<body>");
		sb.append("<p><span style=\"background:#cccc33;\">@All testers:</span></p>");
		sb.append("<p><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Below cases are failed when auto tested by certified servers.</span></p>");
		sb.append("<p><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Please double check the failure log through the <a href=\"ftp://135.242.16.160/CertifyLog/\">logPath</a>, find out the failure reason and fill the reason through the "+buildInfo.get("webSite")+" (Please login with FireFox).</span></p>");
//		sb.append("<p><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Please double check the failure log , find out the failure reason and fill the reason through the <a href=\"" + buildInfo.get("webSite") + "\">website</a></span></p>");
//		sb.append("<p><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Source Path: </span>");
//		sb.append("<span>" + "<a href=\"" + buildInfo.get("SrcPath") + "\">" + buildInfo.get("SrcPath") + "</a></span>");
//		sb.append("<span>&nbsp;&nbsp;Log Path: </span>");
//		sb.append("<span>" + "<a href=\"" + buildInfo.get("LogPath") + "\">" + buildInfo.get("LogPath")
//				+ "</a></span></p>");
		sb.append("<div align=\"left\">");
		sb.append("<br/>");
		sb.append("<br/>");

		sb.append("<table align=\"left\">");
		sb.append("<thead>");
		sb.append("<tr>");

		sb.append("<th>feature</th>");
		sb.append("<th>owner</th>");
		sb.append("<th>case id</th>");
		sb.append("<th>serverName</th>");
//		sb.append("<th>srcPath</th>");
//		sb.append("<th>logPath</th>");
		sb.append("</tr>");
		sb.append("</thead>");
		sb.append("<tbody>");

		for (int i = 0; i < report.size(); i++) {
			sb.append("<tr>");
			sb.append("<td style=\"width:5%\">" + report.getJSONObject(i).getString("feature") + "</td>");
			sb.append("<td style=\"width:5%\"> " + report.getJSONObject(i).getString("author") + "</td>");
			sb.append("<td style=\"text-align:left; width:50%; word-break:break-all; word-wrap:break-word;\">"
					+ report.getJSONObject(i).getString("case")
					+ "</td>");
			sb.append("<td style=\"width:40%\"> " + report.getJSONObject(i).getString("serverName") + "</td>");
//			sb.append("<td style=\"width:20%\"> " + report.getJSONObject(i).getString("srcPath") + "</td>");
//			sb.append("<td style=\"width:20%\"> " + report.getJSONObject(i).getString("logPath") + "</td>");
			sb.append("</tr>");

		}
		sb.append("</tbody>");
		sb.append("</table>");
		sb.append("<br/><br/>");
		sb.append("</div></body></html>");

		/*try {
			PrintStream printStream = new PrintStream(new FileOutputStream(WebServer.ServerViewPath + File.separator
					+ "static" + File.separator + "html" + File.separator + "caseCertified.html"));
			printStream.println(sb.toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}*/

		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String dateTime = df.format(new Date());
//		logger.info(sb.toString());
		SendMail.Send("Daily certified case failure check " + dateTime, sb.toString(), to_list, cc_list);
	}
	
	public static void genReport(JSONArray cc_list, JSONArray to_list, String serverName){
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String dateTime = df.format(new Date());
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<head>");
		sb.append("<title>Report</title>");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
		sb.append("<style type=\"text/css\">");
		sb.append("body { margin:0 auto; padding:20px; font:13px;}");
		sb.append("p, table, caption, td, tr, th {margin:0 auto; padding:0; font-weight:normal;}");
		sb.append("p {margin-bottom:15px;}");
		sb.append("</style></head>");
		sb.append("<body>");
		sb.append("<p><span style=\"background:#cccc33;\">@xiuyun!</span></p>");
		sb.append("<p><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+ serverName +" installation failure, please deal with it in time.</span></p>");
		sb.append("<br/><br/>");
		sb.append("</body></html>");
		SendMail.Send("Lab Installation Failure " + dateTime, sb.toString(), to_list, cc_list);
	}
	
	public static void genReport(JSONArray cc_list, JSONArray to_list, Map<String,Map<String,Map<String,Map<String,String>>>> unInstallRSANSI, Map<String,Map<String,Map<String,Map<String,String>>>> unInstallRSITU){
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String dateTime = df.format(new Date());
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<head>");
		sb.append("<title>Report</title>");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
		sb.append("<style type=\"text/css\">");
		sb.append("body { margin:0 auto; padding:20px; font:13px;}");
		sb.append("p, table, caption, td, tr, th {margin:0 auto; padding:0; font-weight:normal;}");
		sb.append("p {margin-bottom:15px;}");
		sb.append("</style></head>");
		sb.append("<body>");
		sb.append("<p><span style=\"background:#cccc33;\">@Administrator!</span></p>");
		sb.append("<p><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SPA and RTDB below are missing when automatically certified by certification servers.Please install them ASAP.</span></p>");
		sb.append("<p><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<Strong>ANSI-SPA:</Strong></span></p>");
		for(String serverName : unInstallRSANSI.get("SPA").keySet()){
			sb.append("<p><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+serverName+":"+unInstallRSANSI.get("SPA").get(serverName).get("SPA").keySet().toString()+"</span></p>");
		}
		sb.append("<p><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<Strong>ANSI-RTDB:</Strong></span></p>");
		for(String serverName : unInstallRSANSI.get("RTDB").keySet()){
			sb.append("<p><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+serverName+":"+unInstallRSANSI.get("RTDB").get(serverName).get("RTDB").keySet().toString()+"</span></p>");
		}
		sb.append("<p><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<Strong>ITU-SPA:</Strong></span></p>");
		for(String serverName : unInstallRSITU.get("SPA").keySet()){
			sb.append("<p><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+serverName+":"+unInstallRSITU.get("SPA").get(serverName).get("SPA").keySet().toString()+"</span></p>");
		}
		sb.append("<p><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<Strong>ITU-RTDB:</Strong></span></p>");
		for(String serverName : unInstallRSITU.get("RTDB").keySet()){
			sb.append("<p><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+serverName+":"+unInstallRSITU.get("RTDB").get(serverName).get("RTDB").keySet().toString()+"</span></p>");
		}
		sb.append("<br/><br/>");
		sb.append("</body></html>");
		
		SendMail.Send("Uninstalled RTDB and SPA " + dateTime, sb.toString(), to_list, cc_list);
	}
	
	
	
	private static JSONArray processCases(ArrayList<HashMap<String, Object>> error_infos) throws Exception
	{
	    String user_name, mail;
	    JSONArray author_list = new JSONArray();
	    JSONArray to_list = new JSONArray();
	    if (null != error_infos && error_infos.size() != 0) {
	        
	        for (int i = 0; i < error_infos.size(); i++)
	        {
	            user_name = error_infos.get(i).get("user_name").toString();
	            author_list.add(user_name);
	        }
	        
	        
            JSONArray users = new JSONArray();
            String userSql = "select user_name, mail, show_name from cases_info_db.user_info where user_name in ("
                    + author_list.toString().replace("[", "").replace("]", "").replace("\"", "'") +");";
            
            logger.debug(userSql);
            JDBCHelper mysql = JDBCHelper.getInstance("mysql-1");
            ArrayList<HashMap<String, Object>> user_infos = mysql.query(userSql);
            
            for (int i = 0; i < user_infos.size(); i++)
            {
                user_name = user_infos.get(i).get("user_name").toString();
                mail = user_infos.get(i).get("mail").toString();
                users.add(user_name);
                to_list.add(mail);
            }
            
            //not exist user
            JSONArray not_exist_users = new JSONArray();
           
            boolean isExist = false;
            for(int i = 0; i < author_list.size(); i++)
            {
                isExist = false;
                user_name = author_list.getString(i);
                for(int j = 0; j < users.size(); j++)
                {
                    if(user_name.equals(users.getString(j)))
                    {
                        isExist = true;
                        break;
                    }
                }  
                if( !isExist)
                {
                    not_exist_users.add(user_name);
                }
            }
            JSONArray newUser = Ldap.search_users(not_exist_users);   
            for(int j=0; j<newUser.size(); j++){
                to_list.add(newUser.getJSONObject(j).get("mail").toString()); 
            }
                     
        }
	    return to_list;
	}
	
	private static void GenCetifyReport(String type)
	{
	    String cc_mail_str = ParamUtil.getUnableDynamicRefreshedConfigVal("ccMailList");
        String ccs[] = cc_mail_str.split(";");
        JSONArray cc_list = new JSONArray();
        for(int i = 0; i < ccs.length; i++)
        {
            cc_list.add(ccs[i]);
        }
        
        String condition= "";
        if ("normal".equals(type))
        {
            condition = "email_date='' and mark_date='' ";
        }
        else if ("reminder".equals(type)) {
            Calendar cal=Calendar.getInstance();
            int DayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            if(DayOfWeek == 2 || DayOfWeek == 3)
            {
                cal.add(Calendar.DAY_OF_YEAR,-4);
            }
            else{
                cal.add(Calendar.DAY_OF_YEAR,-2);
            }
            Date dt1=cal.getTime();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            String newDate = sdf.format(dt1);
            String emailDate = newDate + " " + ParamUtil.getUnableDynamicRefreshedConfigVal("certifyMailTime") + ":00:00";
            Calendar cal2=Calendar.getInstance();
            int DayOfWeek2 = cal2.get(Calendar.DAY_OF_WEEK);
            if(DayOfWeek2 == 2 || DayOfWeek2 == 3)
            {
                cal2.add(Calendar.DAY_OF_YEAR,-7);
            }
            else{
                cal2.add(Calendar.DAY_OF_YEAR,-5);
            }
            Date dt2=cal2.getTime();
            SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
            String newDate2 = sdf2.format(dt2);
            String emailDate2 = newDate2 + " " + ParamUtil.getUnableDynamicRefreshedConfigVal("certifyMailTime") + ":00:00";
            condition = " mark_date='' and email_date < '" 
                        + emailDate + "' and email_date >= '" + emailDate2 + "' ";
        }
        else if ("expired".equals(type)) {
            
            String cc_boss_str = ParamUtil.getUnableDynamicRefreshedConfigVal("ccBossMailList");

            String boss[] = cc_boss_str.split(";");
            for(int i = 0; i < boss.length; i++)
            {
                cc_list.add(boss[i]);
            }
            
            Calendar cal2=Calendar.getInstance();
            int DayOfWeek2 = cal2.get(Calendar.DAY_OF_WEEK);
            if(DayOfWeek2 == 2 || DayOfWeek2 == 3)
            {
                cal2.add(Calendar.DAY_OF_YEAR,-7);
            }
            else{
                cal2.add(Calendar.DAY_OF_YEAR,-5);
            }
            Date dt2=cal2.getTime();
            SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
            String newDate2 = sdf2.format(dt2);
            String emailDate2 = newDate2 + " " + ParamUtil.getUnableDynamicRefreshedConfigVal("certifyMailTime") + ":00:00";
            condition = " mark_date='' and email_date < '"  + emailDate2 + "' ";
        }
        String errorCaseSql = "select  " +
                "a.feature_number," +
                "a.user_name," +
                "min(a.ftc_date)ftc_date, " +
                "sum(a.case_num) case_num, " +
                "sum(ifnull(b.dft,0)) dft, " +
                "sum(ifnull(c.fail,0)) fail, " +
                "sum(ifnull(d.uncheck,0)) uncheck, " +
                "ifnull((sum(ifnull(b.dft,0))/sum(a.case_num))*100,0) submit_rate, " +
                "ifnull((sum(ifnull(c.fail,0))/sum(ifnull(b.dft,0)))*100,0) fail_rate, " +
                "to_reporter " +
                "from " +
                "( " +
                "select feature_number,owner,count(1) uncheck, email_date  from cases_info_db.error_case_info where " +
                condition +
                " group by feature_number,owner " +
                ") d left join" +
                "( " +
                "select feature_number,user_name,min(ftc_date) ftc_date,sum(case_num) case_num from cases_info_db.feature_permission group by feature_number,user_name " +
                ")a on a.feature_number = d.feature_number and a.user_name= d.owner " +
                "left join ( " +
                "select feature_number,author,count(1) dft from cases_info_db.case_tag where type='dft' group by feature_number,author " +
                ") b on a.feature_number = b.feature_number and a.user_name= b.author " +
                "left join ( " +
                "select feature_number,author,count(1) fail from cases_info_db.case_tag where case_status='F' group by feature_number,author " +
                ") c on a.feature_number = c.feature_number and a.user_name= c.author " +
                " " +
                "left join ( " +
                "select trim(user_name) user_name,trim(to_reporter) to_reporter from cases_info_db.user_info  " +
                ")e on a.user_name= e.user_name " +
                "where 1=1 " +
                "and a.ftc_date != 0 and e.to_reporter is not null " +
                "group by a.feature_number,a.user_name " +
                "order by to_reporter asc, fail_rate desc,submit_rate asc"; 
        try {
            JDBCHelper mysql = JDBCHelper.getInstance("mysql-1");
            logger.info(errorCaseSql);
            ArrayList<HashMap<String, Object>> result =mysql.query(errorCaseSql);
            if(result.size() > 0)
            {
                
                    JSONArray to_list = processCases(result);
                    genCertifyReport(to_list, cc_list, result, type);
                    
                    if ("normal".equals(type))
                    {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        String dateTime = df.format(new Date());
                        String emailDate = dateTime + " " + ParamUtil.getUnableDynamicRefreshedConfigVal("certifyMailTime") + ":00:00";
                        String updateEmailDate = "update cases_info_db.error_case_info set email_date ='" + emailDate + "' where " + condition;
                        
                        mysql.executeSql(updateEmailDate);
                    }
                
            }
        }catch (Exception e) {
            logger.error("Failed to generate Certify " + type + " Report: " + e.toString());
        }
            
        
	}
	public static void GenerateMailReport() throws Exception
	{
	    //generate new error cases
	    GenCetifyReport("normal");
        
        //generate reminder
	    GenCetifyReport("reminder");
        //generate expired 
	    GenCetifyReport("expired");
        
	}
	
	public static class GenerateMailReportThread extends Thread
    {
        public void run()
        {
            boolean IsSend = false;
            while(true)
            {
                logger.info("mail time: " + ParamUtil.getUnableDynamicRefreshedConfigVal("certifyMailTime"));
                String time_now = new SimpleDateFormat("HH").format(new Date());
                logger.info("time_now: " + time_now);
                if(time_now.equals(ParamUtil.getUnableDynamicRefreshedConfigVal("certifyMailTime")) )
                {
                    Calendar cal=Calendar.getInstance();
                    int DayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                    if(DayOfWeek >= 2 && DayOfWeek <= 6)
                    {
                        if( !IsSend)
                        {
                           
                            try {
                                GenerateMailReport();
                                IsSend = true;
                            } catch (Exception e) {
                                logger.error("[GenerateMailReport failed: " + e.toString());
                            }
                            
                        }
                    }
                }
                else
                {
                    IsSend = false;
                }
                try {
                    sleep(300000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
            }
        }
    }

	public static void main(String[] args) throws UnsupportedEncodingException, MessagingException {
	    
	    try {
            GenerateMailReport();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	    /*
		String[] strings = { "lei.k.huang@nokia-sbell..com" };
		SendMail sendmail = new SendMail(strings, "For Test", "hello world");
		sendmail.sendEmail();
		*/
	}
}