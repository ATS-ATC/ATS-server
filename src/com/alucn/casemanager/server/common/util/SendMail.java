package com.alucn.casemanager.server.common.util;

import java.io.UnsupportedEncodingException;
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
import java.util.Date;
import org.apache.log4j.Logger;
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
		sb.append("<p><span style=\"background:#cccc33;\">@All testers,</span></p>");
		sb.append("<p><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Below cases are failed when auto tested by certified servers.</span></p>");
		sb.append("<p><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Please double check the failure log, find out the failure reason and fill the reason through the <a href=\"" + buildInfo.get("webSite") + "\">website</a></span></p>");
		sb.append("<p><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Source Path: </span>");
		sb.append("<span>" + "<a href=\"" + buildInfo.get("SrcPath") + "\">" + buildInfo.get("SrcPath") + "</a></span>");
		sb.append("<span>&nbsp;&nbsp;Log Path: </span>");
		sb.append("<span>" + "<a href=\"" + buildInfo.get("LogPath") + "\">" + buildInfo.get("LogPath")
				+ "</a></span></p>");
		sb.append("<div align=\"left\">");
		sb.append("<br/>");
		sb.append("<br/>");

		sb.append("<table align=\"left\">");
		sb.append("<thead>");
		sb.append("<tr>");

		sb.append("<th>feature</th>");
		sb.append("<th>owner</th>");
		sb.append("<th>case id</th>");
		sb.append("</tr>");
		sb.append("</thead>");
		sb.append("<tbody>");

		for (int i = 0; i < report.size(); i++) {
			sb.append("<tr>");
			sb.append("<td style=\"width:10%\">" + report.getJSONObject(i).getString("feature") + "</td>");
			sb.append("<td style=\"width:20%\"> " + report.getJSONObject(i).getString("author") + "</td>");
			sb.append("<td style=\"text-align:left; width:70%; word-break:break-all; word-wrap:break-word;\">"
					+ report.getJSONObject(i).getString("case").replace("[", "").replace("]", "").replace("\"", "")
					+ "</td>");
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
		SendMail.Send("Daily certified case failure check " + dateTime, sb.toString(), to_list, cc_list);
	}

	public static void main(String[] args) throws UnsupportedEncodingException, MessagingException {
		String[] strings = { "Haiqi.Wang@alcatel-lucent.com" };
		SendMail sendmail = new SendMail(strings, "For Test", "hello world");
		sendmail.sendEmail();
	}
}
