package com.alucn.weblab.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.weblab.dao.impl.JiraSeleniumDaoImpl;

@Service("jiraSeleniumService")
public class JiraSeleniumService {
	
	@Autowired(required=true)
	private JiraSeleniumDaoImpl jiraSeleniumDaoImpl;
	
	public void setTempJiraTbl(Map map) throws SQLException {
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB");
		JdbcUtil jdbc = null;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
			conn = jdbc.getConnection();
			conn.setAutoCommit(false);
			//ps = conn.prepareStatement();
			
			String dsql="delete from temp_jira_status_tbl";
			//jiraSeleniumDaoImpl.delete(jdbc, dsql);
			ps = conn.prepareStatement(dsql);
			ps.execute();
			
			
			//int i =10/0;
			
			
			Set<String> keySet = map.keySet();
			for (String surepay : keySet) {
				Map<String, Object> issuesInfo = (Map<String, Object>) map.get(surepay);
				if(issuesInfo!=null) {
					//String target = (String) issuesInfo.get("target");
					String caseStatus = (String) issuesInfo.get("caseStatus");
					String Feature = (String) issuesInfo.get("Feature");
					List<String> caselist = (List) issuesInfo.get("caselist");
					String assignee = (String) issuesInfo.get("Assignee");
					String labels = (String) issuesInfo.get("labels");
					if(caselist.size()>0) {
						for (String  casename : caselist) {
							
							String sql ="insert into temp_jira_status_tbl(casename,feature,surepay,casestatus,assignee,labels,datetime,stateflag) "
									  + "values('"+casename.trim()+"','"+Feature.trim()+"','"+surepay.trim()+"','"+caseStatus.trim()+"','"+assignee.trim()+"','"+labels.trim()+"',datetime('now', 'localtime'),0)";
							System.out.println(sql);
							//jiraSeleniumDaoImpl.insert(jdbc, sql);
							ps = conn.prepareStatement(sql);
							ps.execute();
						}
					}
				}
			}
			//merge into 
			String usql="";
			conn.commit();
			
			
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			conn.rollback();
		}
		
		
		
	}
	
	
	
	
	
	
	
	
}
