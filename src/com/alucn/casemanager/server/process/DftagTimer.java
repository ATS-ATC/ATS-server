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

public class DftagTimer {
	private static final Logger logger = Logger.getLogger(DftagTimer.class);

    Timer timer;
    public DftagTimer(int delay,int period){  
        timer = new Timer();  
        timer.schedule(new RemindTask(),delay*1000, period*1000);  
    }  
    class RemindTask extends TimerTask{  
        public void run(){  
        	logger.info("Time''s up!");  
            try {
				JdbcUtil jdbc_dc = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("DailyCaseDB"));
				JdbcUtil jdbc_cf = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB"));
				JdbcUtil jdbc_dp = new JdbcUtil(Constant.DATASOURCE,ParamUtil.getUnableDynamicRefreshedConfigVal("PortingCaseDB"));
				String dfttag_sql = "select * from DftTag where porting_release != 'NA' group by feature_number";
				List<Map<String, Object>> list_dc = jdbc_dc.findModeResult(dfttag_sql, null);
				for(int i=0; i<list_dc.size(); i++){
					if(!list_dc.get(i).get("porting_release").toString().startsWith(list_dc.get(i).get("release").toString())){
						String dftporting_sql = "select distinct(porting_release) from DftPorting where feature_number='"+list_dc.get(i).get("feature_number").toString()+"'";
						List<Map<String, Object>> list_dftp = jdbc_dp.findModeResult(dftporting_sql, null);
						if(list_dftp.size()==0 || !list_dc.get(i).get("porting_release").toString().contains(list_dftp.get(0).get("porting_release").toString())){
							String featureinfo_sql = "select distinct(ftc_date) from FeatureInfo where feature_id = '"+list_dc.get(i).get("feature_number").toString()+"' and case_num>0";
							List<Map<String, Object>> list_fi = jdbc_cf.findModeResult(featureinfo_sql, null);
							if(list_fi.size()==0){
								//TODO send mail
						    	System.out.println("send mail");
							}else{
								Date dt=new Date();
							    SimpleDateFormat matter1=new SimpleDateFormat("yyyy-MM-dd");
							    if(Integer.parseInt(matter1.format(dt).replaceAll("-", ""))-Integer.parseInt(list_fi.get(0).get("ftc_date").toString())>60){
							    	//TODO send mail
							    	System.out.println("send mail");
							    }else{
							    	//TODO
							    }
							}
						}
					}
				}
            } catch (Exception e) {
				e.printStackTrace();
				logger.error("[timer is error:]", e);
			}
        }  
    }  
    public static void main(String[] args) {
    	new DftagTimer(1,5);
	}
}
