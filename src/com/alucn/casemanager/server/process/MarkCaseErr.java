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
				String errorCaseSql = "select casename, fature, err_reason, owner, insert_date, mark_date, email_date from errorcaseinfo where email_date='' and mark_date='';";
				List<Map<String, Object>> list_dc = jdbc_cf.findModeResult(errorCaseSql, null);
				
//				genReport(JSONArray cc_list, JSONArray to_list, JSONArray report, JSONObject buildInfo);
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
