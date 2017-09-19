package com.alucn.casemanager.server.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;
import com.alucn.casemanager.server.common.ConfigProperites;
import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.util.DateUtil;
import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.casemanager.server.common.util.SendMail;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * @author haiqiw
 * 2017年7月13日 下午3:15:56
 * desc:MarkCaseErr
 */
public class MarkCaseErr {
	private static final Logger logger = Logger.getLogger(DftagTimer.class);

	Timer timerRemindTask;
	Timer timerRemindTaskAgain;

	public MarkCaseErr(int delay, int period) {
		timerRemindTask = new Timer();
		timerRemindTaskAgain = new Timer();
		timerRemindTask.schedule(new RemindTask(), delay * 1000, period * 1000);
		timerRemindTaskAgain.schedule(new RemindTaskAgain(), delay * 1000, period * 1000 * 10);
	}

	public synchronized boolean getSendMail(String sql) throws Exception {
		JdbcUtil jdbc_cf = new JdbcUtil(Constant.DATASOURCE,
				ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
		String errorCaseSql = sql;
		List<Map<String, Object>> list_dc = jdbc_cf.findModeResult(errorCaseSql, null);

		if (null != list_dc && list_dc.size() != 0) {
			String feature = "";
			String serverName = "";
			JSONArray report = new JSONArray();
			JSONArray to_list = new JSONArray();
			Map<String, String> to_list_map = new HashMap<String, String>();
			for (int i = 0; i < list_dc.size(); i++) {
				StringBuffer errCaseString = new StringBuffer();
				feature = list_dc.get(i).get("feature").toString();
				serverName = list_dc.get(i).get("servername").toString();
				String errCaseListSql = "select e.casename, e.feature, e.owner, e.servername, u.UserName, u.Email, u.ShowName from errorcaseinfo e left join UserInfo u on u.UserName = e.owner where e.feature='"
						+ feature + "' and e.owner='" + list_dc.get(i).get("owner").toString() + "' and e.servername='"
						+ serverName + "'";
				List<Map<String, Object>> errCaseList = jdbc_cf.findModeResult(errCaseListSql, null);
				JSONObject errCaseInfo = new JSONObject();
				errCaseInfo.put("feature", feature);
				String showName = errCaseList.get(0).get("ShowName").toString();
				String eMail = errCaseList.get(0).get("Email").toString();
				if(null==showName || "".equals(showName)){
					JSONObject userObj = getUserInfo(errCaseList.get(0).get("owner").toString());
					if("{}".equals(userObj.toString())){
						continue;
					}
					showName=userObj.getString("lastname") + " " + userObj.getString("firstname");
					eMail = userObj.getString("mail");
					String userInfoSql = "INSERT INTO UserInfo (UserName, Email, ShowName) VALUES('"+errCaseList.get(0).get("owner").toString()+"', '"+eMail+"', '"+showName+"')";
					jdbc_cf.executeSql(userInfoSql);
				}
				errCaseInfo.put("author",showName);
				to_list_map.put(eMail, "");
				for (int j = 0; j < errCaseList.size(); j++) {
					errCaseString.append(errCaseList.get(j).get("casename"));
					if (j != errCaseList.size() - 1) {
						errCaseString.append(",");
					}
				}
				errCaseInfo.put("case", errCaseString.toString().replace(feature + "/", ""));
				errCaseInfo.put("serverName", serverName);
//				errCaseInfo.put("srcPath", serverName);
//				errCaseInfo.put("logPath", serverName);
				report.add(errCaseInfo);
			}
			if(report.size()!=0){
				JSONArray cc_list = new JSONArray();
				cc_list.add("lei.k.huang@alcatel-lucent.com");
				to_list.add("Haiqi.Wang@alcatel-lucent.com");
				for (String key : to_list_map.keySet()) {
					to_list.add(key);
				}
				JSONObject buildInfo = new JSONObject();
				buildInfo.put("webSite", "http://" + ConfigProperites.getInstance().getCaseServerWebIp() + ":8080/weblab");
				SendMail.genReport(cc_list, to_list, report, buildInfo);
				for (int i = 0; i < list_dc.size(); i++) {
					String updateMail = "update errorcaseinfo set email_date='"
							+ DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss") + "' where feature='"
							+ list_dc.get(i).get("feature").toString() + "' and owner='"
							+ list_dc.get(i).get("owner").toString() + "' and servername='" + serverName + "'";
					jdbc_cf.executeSql(updateMail);
				}
				return true;
			}else{
				return false;
			}
		} else {
			return false;
		}
	}

	public JSONObject getUserInfo(String author) throws IOException {
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
	
	class RemindTask extends TimerTask {
		@Override
		public void run() {
			logger.info("[MarkCaseErr Time''s Up!]");
			try {
				String errorCaseSql = "select feature,owner,servername from errorcaseinfo where email_date='' and mark_date='' group by feature,owner";
				if (getSendMail(errorCaseSql)) {
					logger.info("[MarkCaseErr send email success!]");
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("[MarkCaseErr Timer Is Error:]", e);
			}
		}
	}

	class RemindTaskAgain extends TimerTask {

		@Override
		public void run() {
			try {
				logger.info("[MarkCaseErrAgain Time''s Up!]");
				String errorCaseSql = "select feature,owner,servername from errorcaseinfo where email_date>'"
						+ DateUtil.convert2String(
								DateUtil.addDays(
										DateUtil.convert2Date(DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"),
												"yyyy-MM-dd HH:mm:ss"),
										Integer.parseInt(ConfigProperites.getInstance().getCaseCheckMailPeriod())),
								"yyyy-MM-dd HH:mm:ss")
						+ "' and mark_date='' group by feature,owner";
				if (getSendMail(errorCaseSql)) {
					logger.info("[MarkCaseErrAgain send email success!]");
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("[MarkCaseErrAgain Timer Is Error:]", e);
			}
		}
	}
}
