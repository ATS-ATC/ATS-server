package com.alucn.weblab.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.weblab.dao.impl.MainDaoImpl;

/**
 * @author haiqiw
 * 2017年6月6日 下午4:50:45
 * desc:
 */
@Service("mainService")
public class MainService {
	
	@Autowired
	private MainDaoImpl mainDaoImpl;
	private Map<String, Integer> caseCounter = new HashMap<String, Integer>();

	public Map<String, Integer> getStatistics() throws Exception {
    	caseCounter.put("S", 0);
    	caseCounter.put("F", 0);
    	caseCounter.put("J", 0);
    	caseCounter.put("I", 0);
    	caseCounter.put("R", 0);
    	String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		int total=0;
    	for(String state: caseCounter.keySet()){
			String statisticsAll = "SELECT COUNT(case_name) FROM DftTag WHERE case_status='"+state+"'; " ;
			if (state=="J") {
				statisticsAll = "SELECT COUNT(case_name) FROM DftTag WHERE case_status='F' AND jira_id <> ''; " ;
			}
			Integer value = 0;
			ArrayList<HashMap<String, Object>> result = mainDaoImpl.query(jdbc, statisticsAll);
			for(int i=0; i<result.size();i++){
				for(String key: result.get(i).keySet()){
					value = Integer.parseInt(result.get(i).get(key).toString().trim());
				}
			}
			total+=value;
			caseCounter.put(state, value);
    	}
    	caseCounter.put("T", total);
	    return caseCounter;
	}

	public MainDaoImpl getMainDaoImpl() {
		return mainDaoImpl;
	}

	public void setMainDaoImpl(MainDaoImpl mainDaoImpl) {
		this.mainDaoImpl = mainDaoImpl;
	}
}
