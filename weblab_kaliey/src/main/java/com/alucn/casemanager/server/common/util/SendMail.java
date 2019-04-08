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

import com.alucn.casemanager.server.common.ConfigProperites;
import com.alucn.casemanager.server.common.constant.Constant;
import com.thoughtworks.selenium.condition.Not;

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
	
    
    public static void genCertifyReport(JSONArray to_list, JSONArray cc_list, JSONArray report, boolean isRemind, boolean isExpired) {
        Calendar cal=Calendar.getInstance();
        if(!isRemind && !isExpired)
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
        if (isRemind)
        {
            sb.append("<p><span style=\"background:#cccc33;color:red;font:16px\">Reminder!!</span></p>");
            sb.append("<p><span></span></p>");
        }
        else if(isExpired)
        {
            sb.append("<p><span style=\"background:#cccc33;color:red;font:16px\">Expired!!</span></p>");
            sb.append("<p><span></span></p>");
        }
        sb.append("<p><span>Dear All Testers,</span></p>");
        sb.append("<p><span></span></p>");
        sb.append("<p><span>Your submitted auto cases are certified fail (<span style='color:red'>but some features failure reason checking is Pending for a long time</span>).</span></p>");
        sb.append("<p><span></span></p>");
        sb.append("<p><span>please login <a href=\"http://135.242.16.160:8080/weblab\">http://135.242.16.160:8080/weblab</a>  -> Bad Case Management (via Chrome & AD4) to get the detailed fail cases list & failure html report.</span></p>");
        sb.append("<p><span></span></p>");
        
        sb.append("<p><span>please re-login above web link <span style=\"background:#cccc33;\"> before <span style='color:red'>COB of " + COBdate + "</span></span> to submit your checked " + 
                "<span style='color:red'>‘Failed Reason’</span> & <span style='color:red'>‘Target Fix Date’</span> & necessary details in the <span style='color:red'>‘Failed Describe’</span></span></p>");
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
        sb.append("<th>TotalSub</th>");
        sb.append("<th>TotalFailed</th>");
        sb.append("<th>FailNotChecked</th>");
        sb.append("<th>FailedCaseOwner</th>");
        sb.append("</tr>");
        sb.append("</thead>");
        sb.append("<tbody>");

        for (int i = 0; i < report.size(); i++) {
            sb.append("<tr>");
            sb.append("<td style=\"width:15%\">" + report.getJSONObject(i).getString("FeatureID") + "</td>");
            sb.append("<td style=\"width:15%\">" + report.getJSONObject(i).getString("FTC") + "</td>");
            sb.append("<td style=\"width:15%\"> " + report.getJSONObject(i).getString("TotalSub") + "</td>");
            sb.append("<td style=\"width:15%\"> " + report.getJSONObject(i).getString("TotalFailed") + "</td>");
            sb.append("<td style=\"background:#cccc33;width:15%\"> " + report.getJSONObject(i).getString("FailNotChecked") + "</td>");
            sb.append("<td style=\"width:25%\"> " + report.getJSONObject(i).getString("FailedCaseOwner") + "</td>");
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
        if (isRemind)
        {
            title = "Reminder!! " + title;
        }
        else if(isExpired)
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
		sb.append("<p><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Please double check the failure log through the <a href=\"ftp://135.242.16.160/CertifyLog/\">logPath</a>, find out the failure reason and fill the reason through the "+buildInfo.get("webSite")+" (Please login with Google).</span></p>");
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
	
	
	private static JSONObject getUserInfo(String author) throws IOException {
        JSONObject userObj = new JSONObject();
        URL url = new URL("http://dquery-qa.app.alcatel-lucent.com/ws/rest/json/search/"+author);
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
            if (!response.equals("\"noresults\"")) {
                userObj = JSONObject.fromObject(response);
            } else {
                logger.error("[User: " + author + " is not found in DQuery!]");
            }
        }
        return userObj;
    }
	private static JSONObject processCases(List<Map<String, Object>> list_dc, JdbcUtil jdbc_cf) throws Exception
	{
	    JSONObject result = new JSONObject();
	    if (null != list_dc && list_dc.size() != 0) {
	        
	        String uncheck_key = "";
	        String old_feature = "-1";

            String feature = "-1";
            String author = "-1";
            String uncheckedCaseNum = "-1";
            String totalFailedNum = "-1";
            String totalSubmitNum = "-1";
            String ftcDate = "-1";
            String email = "-1";
            
            JSONArray report = new JSONArray();
            JSONArray to_list = new JSONArray();
            JSONArray featureList = new JSONArray();
            JSONArray author_list = new  JSONArray();
            JSONObject unchecked = new JSONObject();
            JSONObject ftcInfo = new JSONObject();
     
            for (int i = 0; i < list_dc.size(); i++) {
                //feature = list_dc.get(i).get("feature").toString();
				feature = list_dc.get(i).get("feature_number").toString();
                author = list_dc.get(i).get("owner").toString();
                uncheckedCaseNum = list_dc.get(i).get("case_num").toString();
                
                if(!feature.equals(old_feature))
                {
                    featureList.add(feature);
                    old_feature = feature;
                }
                if(author_list.indexOf(author) == -1)
                {
                    author_list.add(author);
                }
                JSONObject uncheckInfo = new JSONObject();
                uncheckInfo.put("FailNotChecked", uncheckedCaseNum);
                uncheckInfo.put("FeatureID", feature);
                uncheckInfo.put("FailedCaseOwner", author);
                unchecked.put(feature + ":" + author, uncheckInfo);
            }
            //String errCaseListSql = "select feature,owner,count(casename) as case_num from errorcaseinfo where feature in ("
            //+ featureList.toString().replace("[", "").replace("]", "").replace("\"", "'") +") group by feature,owner;";
			String errCaseListSql = "select feature_number,owner,count(case_name) as case_num from error_case_info where feature_number in ("
			+ featureList.toString().replace("[", "").replace("]", "").replace("\"", "'") +") group by feature_number,owner;";
            
            List<Map<String, Object>> list_dc_errs = jdbc_cf.findModeResult(errCaseListSql, null);
            for (int i = 0; i < list_dc_errs.size(); i++) {
                
                //feature = list_dc_errs.get(i).get("feature").toString();
				feature = list_dc_errs.get(i).get("feature_number").toString();
                author = list_dc_errs.get(i).get("owner").toString();
                totalFailedNum = list_dc_errs.get(i).get("case_num").toString();
                
                uncheck_key = feature + ":" + author;
                if(unchecked.containsKey(uncheck_key))
                {
                    JSONObject uncheckInfo = unchecked.getJSONObject(uncheck_key);
                    uncheckInfo.put("TotalFailed", totalFailedNum);
                    unchecked.put(uncheck_key, uncheckInfo);
                }
            }
            //String dftSql = "select feature_number,author,count(case_name) as case_num from DftTag where feature_number in ("
			String dftSql = "select feature_number,author,count(case_name) as case_num from case_tag where feature_number in ("
                    + featureList.toString().replace("[", "").replace("]", "").replace("\"", "'") +") group by feature_number,author;";
            JdbcUtil jdbc_dft = new JdbcUtil(Constant.DATASOURCE,
                    ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB"));
            List<Map<String, Object>> list_dft = jdbc_dft.findModeResult(dftSql, null);
            for (int i = 0; i < list_dft.size(); i++) {
                
                feature = list_dft.get(i).get("feature_number").toString();
                author = list_dft.get(i).get("author").toString();
                totalSubmitNum = list_dft.get(i).get("case_num").toString();
                
                uncheck_key = feature + ":" + author;
                if(unchecked.containsKey(uncheck_key))
                {
                    JSONObject uncheckInfo = unchecked.getJSONObject(uncheck_key);
                    uncheckInfo.put("TotalSub", totalSubmitNum);
                    unchecked.put(uncheck_key, uncheckInfo);
                }
            }
            
            String ftcSql = "select feature_id, user_name, ftc_date from FeatureInfo where feature_id in ("
                    + featureList.toString().replace("[", "").replace("]", "").replace("\"", "'") +");";
            JdbcUtil jdbc_ftc = new JdbcUtil(Constant.DATASOURCE,
                    ParamUtil.getUnableDynamicRefreshedConfigVal("PermissionDB"));
            List<Map<String, Object>> list_ftc = jdbc_ftc.findModeResult(ftcSql, null);
            for (int i = 0; i < list_ftc.size(); i++) {
                
                feature = list_ftc.get(i).get("feature_id").toString();
                author = list_ftc.get(i).get("user_name").toString();
                ftcDate = list_ftc.get(i).get("ftc_date").toString();

                ftcInfo.put(feature, ftcDate);
                uncheck_key = feature + ":" + author;
                if(unchecked.containsKey(uncheck_key))
                {
                    JSONObject uncheckInfo = unchecked.getJSONObject(uncheck_key);
                    uncheckInfo.put("FTC", ftcDate);
                    unchecked.put(uncheck_key, uncheckInfo);
                }
            }
            
            JSONArray users = new JSONArray();
            String userSql = "select UserName, Email, ShowName from UserInfo where UserName in ("
                    + author_list.toString().replace("[", "").replace("]", "").replace("\"", "'") +");";
            List<Map<String, Object>> list_user = jdbc_cf.findModeResult(userSql, null);
            for (int i = 0; i < list_user.size(); i++) {
                
                author = list_user.get(i).get("UserName").toString();
                email = list_user.get(i).get("Email").toString();
                users.add(author);
                to_list.add(email);              
            }
            
            
            //not exist user
            JSONArray newUser = new JSONArray();
            boolean isExist = false;
            String userName, showName;
            for(int i = 0; i < author_list.size(); i++)
            {
                isExist = false;
                userName = author_list.getString(i);
                for(int j = 0; j < users.size(); j++)
                {
                    if(userName.equals(users.getString(j)))
                    {
                        isExist = true;
                        break;
                    }
                }  
                if( !isExist)
                {
                    try {
                        JSONObject userObj = getUserInfo(userName);
                        if(!"{}".equals(userObj.toString())){
                            showName=userObj.getString("lastname") + " " + userObj.getString("firstname");
                            email = userObj.getString("mail");
                            JSONObject userInfo = new JSONObject();
                            userInfo.put("UserName", userName);
                            userInfo.put("Email", email);
                            userInfo.put("ShowName", showName);
                            newUser.add(userInfo);
                            to_list.add(email);
                        }
                    } catch (Exception e) {
                        logger.error("Failed to get User Info: " + e.toString());
                    }
                    
                    
                }
                
            }
            
            
            //update UserInfo
            if(newUser.size() > 0)
            {
                String replaceUserInfoTb = "replace into UserInfo (UserName, Email, ShowName) values (?, ?, ?);";
                List<Object[]> paramsList = new ArrayList<Object[]>();
                for(int j=0; j<newUser.size(); j++){
                    Object [] paramsArray = new Object[3];
                    paramsArray[0] = newUser.getJSONObject(j).get("UserName");
                    paramsArray[1] = newUser.getJSONObject(j).get("Email");
                    paramsArray[2] = newUser.getJSONObject(j).get("ShowName");
                    paramsList.add(paramsArray);
                }
                jdbc_cf.executeBatch(replaceUserInfoTb, paramsList);
            }
            
            
            
            Iterator<String> it = unchecked.keys(); 
            while(it.hasNext()){
        
                String key = it.next(); 
                JSONObject failInfo = unchecked.getJSONObject(key);
                report.add(failInfo);
            }
            result.put("to_list", to_list);
            result.put("report", report);
            
       
               
            
        }
	    return result;
	}
	private static void GenerateMailReport() throws Exception
	{
	    //generate new error cases
	    //String errorCaseSql = "select feature,owner,count(casename) as case_num from errorcaseinfo where email_date='' and mark_date='' group by feature,owner;";
	    String errorCaseSql = "select feature_number,owner,count(case_name) as case_num from error_case_info where email_date='' and mark_date='' group by feature_number,owner;";
		JdbcUtil jdbc_cf = new JdbcUtil(Constant.DATASOURCE,
                ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
        List<Map<String, Object>> list_dc = jdbc_cf.findModeResult(errorCaseSql, null);
        if(list_dc.size() > 0)
        {
            try {
                JSONObject re1 = processCases(list_dc, jdbc_cf);
                String cc_mail_str = ParamUtil.getUnableDynamicRefreshedConfigVal("ccMailList");
                String ccs[] = cc_mail_str.split(";");
                JSONArray cc_list = new JSONArray();
                for(int i = 0; i < ccs.length; i++)
                {
                    cc_list.add(ccs[i]);
                }
                genCertifyReport(re1.getJSONArray("to_list"), cc_list, re1.getJSONArray("report"), false, false);
                //String caselistSql = "select casename from errorcaseinfo where email_date='' and mark_date=''";
				String caselistSql = "select case_name from error_case_info where email_date='' and mark_date=''";
                List<Map<String, Object>> list_cases = jdbc_cf.findModeResult(caselistSql, null);
                String casename;
                JSONArray newCaseList = new JSONArray();
                for(int j=0; j<list_cases.size(); j++){
                    //newCaseList.add(list_cases.get(j).get("casename").toString());
					newCaseList.add(list_cases.get(j).get("case_name").toString());
                }
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String dateTime = df.format(new Date());
                String emailDate = dateTime + " " + ParamUtil.getUnableDynamicRefreshedConfigVal("certifyMailTime") + ":00:00";
                //String updateEmailDate = "update errorcaseinfo set email_date ='" + emailDate + "' where casename in ("
				String updateEmailDate = "update error_case_info set email_date ='" + emailDate + "' where case_name in ("
                    + newCaseList.toString().replace("[", "").replace("]", "").replace("\"", "'") +");";
                jdbc_cf.executeSql(updateEmailDate);
                
            } catch (Exception e) {
                logger.error("Failed to generate Certify Report: " + e.toString());
            }
        }
        
        //generate reminder
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
        String RemindConditions = " where mark_date='' and email_date < '" 
                    + emailDate + "' and email_date >= '" + emailDate2 + "' group by feature,owner;";
        //String ReminderCaseSql = "select feature,owner,count(casename) as case_num from errorcaseinfo" +  RemindConditions;
		String ReminderCaseSql = "select feature_number,owner,count(case_name) as case_num from error_case_info" +  RemindConditions;
      
        List<Map<String, Object>> list_remind1 = jdbc_cf.findModeResult(ReminderCaseSql, null);
        
        if(list_remind1.size() > 0)
        {
            try {
                JSONObject re1 = processCases(list_remind1, jdbc_cf);
                String cc_mail_str = ParamUtil.getUnableDynamicRefreshedConfigVal("ccMailList");
                String ccs[] = cc_mail_str.split(";");
                JSONArray cc_list = new JSONArray();
                for(int i = 0; i < ccs.length; i++)
                {
                    cc_list.add(ccs[i]);
                }
                genCertifyReport(re1.getJSONArray("to_list"), cc_list, re1.getJSONArray("report"), true, false);
               
            } catch (Exception e) {
                logger.error("Failed to generate Certify Remind Report: " + e.toString());
            }
        }
        
        //generate expired 
        String ExpiredConditions = " where mark_date='' and email_date < '" 
                 //+ emailDate2 + "' group by feature,owner;";
				 + emailDate2 + "' group by feature_number,owner;";
        //String ExpiredCaseSql = "select feature,owner,count(casename) as case_num from errorcaseinfo" +  ExpiredConditions;
		String ExpiredCaseSql = "select feature_number,owner,count(case_name) as case_num from error_case_info" +  ExpiredConditions;
      
        List<Map<String, Object>> list_expired = jdbc_cf.findModeResult(ExpiredCaseSql, null);
        if(list_expired.size() > 0)
        {
            try {
                JSONObject re1 = processCases(list_expired, jdbc_cf);
                String cc_mail_str = ParamUtil.getUnableDynamicRefreshedConfigVal("ccMailList");
                String cc_boss_str = ParamUtil.getUnableDynamicRefreshedConfigVal("ccBossMailList");
                String ccs[] = cc_mail_str.split(";");
                String boss[] = cc_boss_str.split(";");
                JSONArray cc_list = new JSONArray();
                for(int i = 0; i < ccs.length; i++)
                {
                    cc_list.add(ccs[i]);
                }
                for(int i = 0; i < boss.length; i++)
                {
                    cc_list.add(boss[i]);
                }
                
                genCertifyReport(re1.getJSONArray("to_list"), cc_list, re1.getJSONArray("report"), false, true);
               
            } catch (Exception e) {
                logger.error("Failed to generate Certify Expired Report: " + e.toString());
            }
        }
	}
	
	public static class GenerateMailReportThread extends Thread
    {
        public void run()
        {
            boolean IsSend = false;
            while(true)
            {
                
                if(new SimpleDateFormat("HH").format(new Date()).equals(ParamUtil.getUnableDynamicRefreshedConfigVal("certifyMailTime")) )
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
		String[] strings = { "Haiqi.Wang@alcatel-lucent.com" };
		SendMail sendmail = new SendMail(strings, "For Test", "hello world");
		sendmail.sendEmail();
	}
}