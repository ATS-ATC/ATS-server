package com.alucn.casemanager.server.process;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.casemanager.server.common.util.ParamUtil;

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
    class RemindTask extends TimerTask{
    	@Override
        public void run(){  
        	logger.info("[MarkCaseErr Time''s Up!]");  
            try {
				JdbcUtil jdbc_cf = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
				String errorCaseSql = "select fature,owner,servername from errorcaseinfo where email_date='' and mark_date='' group by fature,owner";
				List<Map<String, Object>> list_dc = jdbc_cf.findModeResult(errorCaseSql, null);
				String feature = "";
				String owner = "";
				String serverName = "";
				if(null != list_dc && list_dc.size()!=0){
					for(int i=0; i<list_dc.size(); i++){
						StringBuffer errCaseString = new StringBuffer();
						feature = list_dc.get(i).get("fature").toString();
						serverName = list_dc.get(i).get("servername").toString();
						String errCaseListSql = "select e.casename, e.fature, e.owner, e.servername, u.UserName, u.ShowName from errorcaseinfo e left join UserInfo u on u.UserName = e.owner where e.fature='"+feature+"' and e.owner='"+owner+"' and e.servername='"+serverName+"'";
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
					}
				}
				
				
				JSONArray cc_list = new JSONArray();
				cc_list.add("lei.k.huang@alcatel-lucent.com");
				
				
//				SendMail.genReport(cc_list, JSONArray to_list, JSONArray report, buildInfo);
            } catch (Exception e) {
				e.printStackTrace();
				logger.error("[MarkCaseErr Timer Is Error:]", e);
			}
        }  
    }  
    
    class RemindTaskAgain extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
    	
    }
}
