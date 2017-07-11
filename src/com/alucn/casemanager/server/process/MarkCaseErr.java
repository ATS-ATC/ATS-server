package com.alucn.casemanager.server.process;

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

public class MarkCaseErr {
	private static final Logger logger = Logger.getLogger(DftagTimer.class);

    Timer timerRemindTask;
    Timer timerRemindTaskAgain;
    public MarkCaseErr(int delay,int period){  
    	timerRemindTask = new Timer();  
    	timerRemindTaskAgain = new Timer();
    	timerRemindTask.schedule(new RemindTask(),delay*1000, period*1000);  
    	timerRemindTaskAgain.schedule(new RemindTaskAgain(),delay*1000, period*1000*10);
    }
    
    public synchronized void  getSendMail(String sql) throws Exception{
    	JdbcUtil jdbc_cf = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
		String errorCaseSql = sql;
		List<Map<String, Object>> list_dc = jdbc_cf.findModeResult(errorCaseSql, null);
		
		if(null != list_dc && list_dc.size()!=0){
			String feature = "";
			String serverName = "";
			JSONArray report = new JSONArray();
			for(int i=0; i<list_dc.size(); i++){
				StringBuffer errCaseString = new StringBuffer();
				feature = list_dc.get(i).get("fature").toString();
				serverName = list_dc.get(i).get("servername").toString();
				String errCaseListSql = "select e.casename, e.fature, e.owner, e.servername, u.UserName, u.ShowName from errorcaseinfo e left join UserInfo u on u.UserName = e.owner where e.fature='"+feature+"' and e.owner='"+list_dc.get(i).get("owner").toString()+"' and e.servername='"+serverName+"'";
				List<Map<String, Object>> errCaseList = jdbc_cf.findModeResult(errCaseListSql, null);
				JSONObject errCaseInfo = new JSONObject();
				errCaseInfo.put("feature", feature);
				errCaseInfo.put("author", errCaseList.get(0).get("ShowName").toString());
				for(int j=0; j<errCaseList.size(); j++){
					errCaseString.append(errCaseList.get(j).get("casename"));
					if(j != errCaseList.size()-1){
						errCaseString.append(",");
					}
				}
				errCaseInfo.put("case", errCaseString.toString().replace(feature+"/", ""));
				errCaseInfo.put("srcPath", serverName);
				errCaseInfo.put("logPath", serverName);
				report.add(errCaseInfo);
			}
			JSONArray cc_list = new JSONArray();
			cc_list.add("lei.k.huang@alcatel-lucent.com");
			JSONArray to_list = new JSONArray();
			String toListSql = "select u.UserName, u.Email, u.ShowName from UserInfo u left join (select distinct owner from errorcaseinfo where email_date='' and mark_date='') b on u.UserName = b.owner";
			List<Map<String, Object>> toMailList= jdbc_cf.findModeResult(toListSql, null);
			for(int i=0; i<toMailList.size(); i++){
				to_list.add(toMailList.get(i).get("Email").toString());
			}
			JSONObject buildInfo = new JSONObject();
			buildInfo.put("webSite", "http://"+ConfigProperites.getInstance().getCaseServerWebIp()+":8080/weblab");
			SendMail.genReport(cc_list, to_list, report, buildInfo);
			for(int i=0; i<list_dc.size(); i++){
				String updateMail = "update errorcaseinfo set email_date='"+DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss")+"' where fature='"+list_dc.get(i).get("fature").toString()+"' and owner='"+list_dc.get(i).get("owner").toString()+"' and servername='"+serverName+"'";
				jdbc_cf.executeSql(updateMail);
			}
		}
    }
    
    class RemindTask extends TimerTask{
    	@Override
        public void run(){  
        	logger.info("[MarkCaseErr Time''s Up!]");  
            try {
				String errorCaseSql = "select fature,owner,servername from errorcaseinfo where email_date='' and mark_date='' group by fature,owner";
				getSendMail(errorCaseSql);
            } catch (Exception e) {
				e.printStackTrace();
				logger.error("[MarkCaseErr Timer Is Error:]", e);
			}
        }  
    }  
    
    class RemindTaskAgain extends TimerTask{

		@Override
		public void run() {
			try {
				String errorCaseSql = "select fature,owner,servername from errorcaseinfo where email_date>'"+DateUtil.convert2String(DateUtil.addDays(DateUtil.convert2Date(DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd HH:mm:ss"), Integer.parseInt(ConfigProperites.getInstance().getCaseCheckMailPeriod())), "yyyy-MM-dd HH:mm:ss")+"' and mark_date='' group by fature,owner";
				getSendMail(errorCaseSql);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("[MarkCaseErrAgain Timer Is Error:]", e);
			}
		}
    	
    }
}
