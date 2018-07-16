package com.alucn.weblab.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
		PreparedStatement ups = null;
		try {
			jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
			conn = jdbc.getConnection();
			conn.setAutoCommit(false);
			//ps = conn.prepareStatement();
			
			String dsql="delete from temp_jira_status_tbl";
			//jiraSeleniumDaoImpl.delete(jdbc, dsql);
			ps = conn.prepareStatement(dsql);
			ps.execute();
			ps.close();
			
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
						String sql ="insert into temp_jira_status_tbl(casename,feature,surepay,casestatus,assignee,labels,datetime,stateflag) "
								  + "values(?,?,?,?,?,?,datetime('now', 'localtime'),0)";
						ps = conn.prepareStatement(sql);
						int i =0;
						for (String  casename : caselist) {
							
							//String sql ="insert into temp_jira_status_tbl(casename,feature,surepay,casestatus,assignee,labels,datetime,stateflag) "
							//	  + "values('"+casename.trim()+"','"+Feature.trim()+"','"+surepay.trim()+"','"+caseStatus.trim()+"','"+assignee.trim()+"','"+labels.trim()+"',datetime('now', 'localtime'),0)";
							//System.out.println(sql);
							//jiraSeleniumDaoImpl.insert(jdbc, sql);
							//ps = conn.prepareStatement(sql);
							//ps.execute();
							ps.setString(1, casename.trim());
			                ps.setString(2, Feature.trim());
			                ps.setString(3, surepay.trim());
			                ps.setString(4, caseStatus.trim());
			                ps.setString(5, assignee.trim());
			                ps.setString(6, labels.trim());
			                ps.addBatch();
			                if (i % 1000 == 0) {
			                     ps.executeBatch();
			                     ps.clearBatch(); // 清空缓存
			                }
			                i++;
						}
						ps.executeBatch();
						ps.close();
					}
				}
			}
			//merge into 
			String msql="select a.case_name,a.case_status,a.jira_id,b.* from DftTag_bak_20180716 a\r\n" + 
					"left join (\r\n" + 
					"select case casestatus\r\n" + 
					"when 'Pre-Pending' then 'PP' \r\n" + 
					"when 'Pending' then 'P'\r\n" + 
					"when 'Resubmit' then 'R'\r\n" + 
					"when 'Pre-Delete' then 'PD'\r\n" + 
					"when 'Delete' then 'D'\r\n" + 
					"when 'Pre-Retag' then 'PRT'\r\n" + 
					"when 'Retag' then 'RT'\r\n" + 
					"end ct,*,(\r\n" + 
					"select count(1) \r\n" + 
					"from temp_jira_status_tbl b \r\n" + 
					"where casename=b.casename \r\n" + 
					"and surepay<b.surepay \r\n" + 
					") rn\r\n" + 
					"from temp_jira_status_tbl \r\n" + 
					"where rn =0\r\n" + 
					") b  on a.case_name=b.feature||'/'||b.casename||'.json'\r\n" + 
					"where 1=1\r\n" + 
					"and b.stateflag=0\r\n" + 
					"and b.casestatus!='<Case Status>'\r\n" + 
					"and a.case_status!=b.ct\r\n" + 
					"and b.surepay||'/'||b.feature||'/'||b.casename not in(\r\n" + 
					"select distinct jira_id_mid||'/'||feature||'/'||casename from jira_status_tbl where stateflag=0\r\n" + 
					")";
			
			ArrayList<HashMap<String,Object>> query = jiraSeleniumDaoImpl.query(jdbc, msql);
			//System.out.println(query);
			String sql ="insert into jira_status_tbl (casename,feature,case_name_foregin,jira_id_old,jira_id_mid,jira_id_new,case_status_old,case_status_new,datatime) "
					+ "values(?,?,?,?,?,?,?,?,datetime('now', 'localtime'))";
			ps = conn.prepareStatement(sql);
			int i = 0;
			String usql="update DftTag_bak_20180626 set case_status=?,jira_id=? where case_name=?";
			ups = conn.prepareStatement(usql);
			for (HashMap<String, Object> hashMap : query) {
				//System.out.println(hashMap.toString());
				String ct = (String) hashMap.get("ct");
				String jira_id = (String)hashMap.get("jira_id");
				StringBuffer jira_id_new = new StringBuffer();
				if("".equals(jira_id)|| jira_id==null) {
					jira_id="";
					jira_id_new.append(hashMap.get("surepay"));
				}else if (jira_id.contains(",")) {
					String[] split = jira_id.split(",");
					if(!split[split.length-1].equals(hashMap.get("surepay").toString())) {
						jira_id_new.append(jira_id).append(",").append(hashMap.get("surepay")); 
					}else {
						jira_id_new.append(jira_id);
					}
				}else if(!jira_id.contains(",")) {
					if(jira_id.equals(hashMap.get("surepay"))) {
						jira_id_new.append(jira_id);
					}else {
						jira_id_new.append(jira_id).append(",").append(hashMap.get("surepay")); 
					}
				}
				//System.out.println("jira_id_new:======"+jira_id_new+"   jira_id :======="+jira_id);
				if("PP".equals(ct)) {
					/*String sql ="insert into jira_status_tbl (casename,feature,case_name_foregin,jira_id_old,jira_id_mid,jira_id_new,case_status_old,case_status_new,datatime) "
							+ "values('"+hashMap.get("casename")+"','"+hashMap.get("feature")+"','"+hashMap.get("case_name")+"'"
									+ ",'"+jira_id+"','"+hashMap.get("surepay")+"','"+jira_id_new+"','"+hashMap.get("case_status")+"','"+ct+"',datetime('now', 'localtime'))";
					System.out.println(sql);
					ps = conn.prepareStatement(sql);*/
					ps.setString(1, hashMap.get("casename").toString());
					ps.setString(2, hashMap.get("feature").toString());
					ps.setString(3, hashMap.get("case_name").toString());
					ps.setString(4, jira_id);
					ps.setString(5, hashMap.get("surepay").toString());
					ps.setString(6, jira_id_new.toString());
					ps.setString(7, hashMap.get("case_status").toString());
					ps.setString(8, ct);
					//ps.execute();
					ps.addBatch();
	                if (i % 1000 == 0) {
	                     ps.executeBatch();
	                     ps.clearBatch(); // 清空缓存
	                }
	                i++;
					//String usql="update DftTag_bak_20180626 set case_status='"+ct+"',jira_id='"+jira_id_new+"' where case_name='"+hashMap.get("case_name")+"'";
					//ps = conn.prepareStatement(usql);
					//ps.execute();
	                ups.setString(1, ct);
	                ups.setString(2, jira_id_new.toString());
	                ups.setString(3, hashMap.get("case_name").toString());
	                ups.addBatch();
	                if (i % 1000 == 0) {
	                	ups.executeBatch();
	                	ups.clearBatch(); // 清空缓存
	                }
				}
				
				
			}
			ps.executeBatch();
			ps.close();
			ups.executeBatch();
			ups.close();
			
			
			
			
			conn.commit();
			
			
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			conn.rollback();
		}
		
		
		
	}
	
	public static void main(String[] args) {
		String msql="select a.case_name,a.case_status,b.* from DftTag_bak_20180626 a\r\n" + 
				"left join (\r\n" + 
				"select case casestatus\r\n" + 
				"when 'Pre-Pending' then 'PP' \r\n" + 
				"when 'Pending' then 'P'\r\n" + 
				"when 'Resubmit' then 'R'\r\n" + 
				"when 'Pre-Delete' then 'PD'\r\n" + 
				"when 'Delete' then 'D'\r\n" + 
				"when 'Pre-Retag' then 'PRT'\r\n" + 
				"when 'Retag' then 'RT'\r\n" + 
				"end ct,*\r\n" + 
				"from temp_jira_status_tbl\r\n" + 
				") b  on a.case_name=b.feature||'/'||b.casename||'.json'\r\n" + 
				"where 1=1\r\n" + 
				"and b.stateflag=0\r\n" + 
				"and b.casestatus!='<Case Status>'\r\n" + 
				"and a.case_status!=b.ct";
		System.out.println(msql);
	}
	
	
	
	
	
	
}
