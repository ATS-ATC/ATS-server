package com.alucn.weblab.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.weblab.dao.impl.MainDaoImpl;


@Service("mainService")
public class MainService {
	
	@Autowired
	private MainDaoImpl mainDaoImpl;
	private Map<String, Integer> caseCounter = new HashMap<String, Integer>();
	
	public ArrayList<HashMap<String, Object>> getCustomerCount() throws Exception{
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String sql = "select a.customer,cc,ff from (\n" + 
				"select customer,count(1) cc from DftTag where case_status='S' group by customer order by cc asc\n" + 
				")a\n" + 
				"left join (\n" + 
				"select customer,count(1) ff from DftTag where case_status='F' group by customer\n" + 
				") b on a.customer=b.customer";
		ArrayList<HashMap<String, Object>> query = mainDaoImpl.query(jdbc, sql);
		return query;
	}
	public ArrayList<HashMap<String, Object>> getReleaseCount() throws Exception{
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String sql = "select a.release,cc,ff from (\n" + 
				"select release,count(1) cc from DftTag where case_status='S' group by release order by release asc\n" + 
				")a \n" + 
				"left join (\n" + 
				"select release,count(1) ff from DftTag where case_status='F' group by release\n" + 
				") b on a.release=b.release";
		ArrayList<HashMap<String, Object>> query = mainDaoImpl.query(jdbc, sql);
		return query;
	}

	public Map<String, Integer> getStatistics() throws Exception {
    	caseCounter.put("S", 0);
    	caseCounter.put("F", 0);
    	caseCounter.put("P", 0);
    	caseCounter.put("O", 0);
    	caseCounter.put("PP", 0);
    	caseCounter.put("I", 0);
    	caseCounter.put("R", 0);
    	String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		int total=0;
    	for(String state: caseCounter.keySet()){
			String statisticsAll = "SELECT COUNT(case_name) FROM DftTag WHERE case_status='"+state+"'; " ;
			/*
			if (state=="J") {
				statisticsAll = "SELECT COUNT(case_name) FROM DftTag WHERE case_status='F' AND jira_id <> ''; " ;
			}
			*/
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
	public ArrayList<HashMap<String, Object>> getWelcomeInfo(String useName) throws Exception {
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String sql ="select count(a.case_name) allCase,count(b.case_name) fCase,count(c.case_name) sCase\n" + 
				"from DftTag a\n" + 
				"left join DftTag b on a.case_name=b.case_name and b.case_status='F'\n" + 
				"left join DftTag c on a.case_name=c.case_name and c.case_status='S'\n" + 
				"where a.author='"+useName+"'";
		ArrayList<HashMap<String, Object>> result = mainDaoImpl.query(jdbc, sql);
		return result;
	}
}
