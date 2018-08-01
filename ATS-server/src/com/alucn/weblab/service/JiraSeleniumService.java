package com.alucn.weblab.service;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.weblab.dao.impl.JiraSeleniumDaoImpl;

@Service("jiraSeleniumService")
public class JiraSeleniumService {
	public static Logger logger = Logger.getLogger(JiraSeleniumService.class);
	@Autowired(required=true)
	private JiraSeleniumDaoImpl jiraSeleniumDaoImpl;
	
	public void setTempJiraTbl(Map map) throws SQLException {

		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB");
		JdbcUtil jdbc = null;
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ups = null;
		PreparedStatement rps = null;
		try {
			jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
			conn = jdbc.getConnection();
			conn.setAutoCommit(false);
			
			String dsql="delete from temp_jira_status_tbl";
			
			ps = conn.prepareStatement(dsql);
			ps.execute();
			ps.close();
			
			//int i =10/0;
			
			
			Set<String> keySet = map.keySet();
			for (String surepay : keySet) {
				Map<String, Object> issuesInfo = (Map<String, Object>) map.get(surepay);
				if(issuesInfo!=null) {
					String caseStatus = (String) issuesInfo.get("caseStatus");
					String Feature = (String) issuesInfo.get("Feature");
					List<String> caselist = (List) issuesInfo.get("caselist");
					String assignee = (String) issuesInfo.get("Assignee");
					String labels = (String) issuesInfo.get("labels");
					String versions = (String) issuesInfo.get("Versions");
					if(caselist.size()>0) {
						String sql ="insert into temp_jira_status_tbl(casename,feature,surepay,casestatus,assignee,labels,datetime,stateflag,releases) "
								  + "values(?,?,?,?,?,?,datetime('now', 'localtime'),0,?)";
						ps = conn.prepareStatement(sql);
						for (String  casename : caselist) {
							
							ps.setString(1, casename.trim());
			                ps.setString(2, Feature.trim());
			                ps.setString(3, surepay.trim());
			                ps.setString(4, caseStatus.trim());
			                ps.setString(5, assignee.trim());
			                ps.setString(6, labels.trim());
			                ps.setString(7, versions.trim());
			                ps.addBatch();
						}
						ps.executeBatch();
						ps.close();
					}
				}
			}
			conn.commit();
			//merge into 
			String msql="select a.case_name,a.case_status,a.jira_id,a.release,b.* from DftTag a\r\n" + 
					"left join (\r\n" + 
					"select case casestatus\r\n" + 
					"when 'Pre-Pending' then 'PP' \r\n" + 
					"when 'Pending' then 'P'\r\n" + 
					"when 'Resubmit' then 'R'\r\n" + 
					"when 'Pre-Delete' then 'PD'\r\n" + 
					"when 'Delete' then 'O'\r\n" + 
					"when 'Pre-ReTag' then 'PRT'\r\n" + 
					"when 'ReTag' then 'RT'\r\n" + 
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
			
			
			String jsql="select * from jira_status_tbl where stateflag=0";
			ArrayList<HashMap<String, Object>> jira = jiraSeleniumDaoImpl.query(jdbc, jsql);
			
			
			logger.info("msql:=="+msql);
			logger.info("query:=="+query);
			String sql ="insert into jira_status_tbl (casename,feature,case_name_foregin,jira_id_old,jira_id_mid,jira_id_new,case_status_old,case_status_new,datatime) "
					+ "values(?,?,?,?,?,?,?,?,datetime('now', 'localtime'))";
			ps = conn.prepareStatement(sql);
			String usql="update DftTag set case_status=?,jira_id=? where case_name=?";
			ups = conn.prepareStatement(usql);
			
			String rsql="update DftTag set release=?,jira_id=? where case_name=?";
			rps = conn.prepareStatement(rsql);
			
			for (HashMap<String, Object> hashMap : query) {
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
				String case_status = hashMap.get("case_status").toString();
				if(		
						  ("PP".equals(ct))
						||("P".equals(ct)&&"PP".equals(case_status))
						||("O".equals(ct)&&"PP".equals(case_status))
					) {
					ps.setString(1, hashMap.get("casename").toString());
					ps.setString(2, hashMap.get("feature").toString());
					ps.setString(3, hashMap.get("case_name").toString());
					ps.setString(4, jira_id);
					ps.setString(5, hashMap.get("surepay").toString());
					ps.setString(6, jira_id_new.toString());
					ps.setString(7, case_status);
					ps.setString(8, ct);
					ps.addBatch();
	                ups.setString(1, ct);
	                ups.setString(2, jira_id_new.toString());
	                ups.setString(3, hashMap.get("case_name").toString());
	                ups.addBatch();
				}
				
				//需求变动：
				//1. 如果是ReTag状态数据，需要替换文件中的一个字段（通过发送一段shell命令实现）。
				//2. 更新目标表内数据。
				//3. 如果之前更新过（jira_status_tbl内有数据），需要回滚为原有数据。
				if("RT".equals(ct)){
					//如果releases不同，需要更新表内字段，发送shell命令
					if(!hashMap.get("releases").toString().equals(hashMap.get("release").toString())){
						rps.setString(1, hashMap.get("releases").toString());
						rps.setString(2, jira_id_new.toString());
						rps.setString(3, hashMap.get("case_name").toString());
						rps.addBatch();
						String[] cmd={"/bin/sh","-c","sed -i \"s/\\\"porting_release\\\": \\[.*\\]/\\\"porting_release\\\": \\[\\\""+hashMap.get("releases").toString()+"\\\"\\]/g\" /home/surepayftp/DftCase/"+hashMap.get("case_name").toString()};
						logger.info(cmd.toString());
						exeCmd(cmd);
				        //System.out.println(result);
					}
	                //其余的判断是否有过更新，有-回滚-记录，无-记录
	                ps.setString(1, hashMap.get("casename").toString());
					ps.setString(2, hashMap.get("feature").toString());
					ps.setString(3, hashMap.get("case_name").toString());
					ps.setString(4, jira_id);
					ps.setString(5, hashMap.get("surepay").toString());
					ps.setString(6, jira_id_new.toString());
					
					boolean bak=true;
	                for (HashMap<String, Object> hashMap2 : jira) {
	                	String case_name_foregin = hashMap2.get("case_name_foregin").toString();
	                	if(hashMap.get("case_name").toString().equals(case_name_foregin)) {
	                		
	                		System.out.println(case_name_foregin+" has bak , system rollbak");
	                		
	    	                ps.setString(7, ct);
	    					ps.setString(8, hashMap2.get("case_status_old").toString());
	    					
	    					ups.setString(1, hashMap2.get("case_status_old").toString());
	    	                ups.setString(2, jira_id_new.toString());
	    	                ups.setString(3, hashMap.get("case_name").toString());
	    	                ups.addBatch();
	    	                
	    					bak=false;
	    					
	                		break;
	                	}
					}
	                if(bak) {
	                	ps.setString(7, case_status);
	                	ps.setString(8, ct);
	                }
					ps.addBatch();
	                
				}
				
			}
			ps.executeBatch();
			ps.close();
			ups.executeBatch();
			ups.close();
			rps.executeBatch();
			rps.close();
			
			conn.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
			conn.rollback();
		}
	}
	
	
	public void testTempJiraTbl(Map map) throws SQLException {

		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB");
		JdbcUtil jdbc = null;
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ups = null;
		PreparedStatement rps = null;
		try {
			jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
			conn = jdbc.getConnection();
			conn.setAutoCommit(false);
			
			String dsql="delete from temp_jira_status_tbl";
			
			ps = conn.prepareStatement(dsql);
			ps.execute();
			ps.close();
			
			//int i =10/0;
			
			
			Set<String> keySet = map.keySet();
			for (String surepay : keySet) {
				Map<String, Object> issuesInfo = (Map<String, Object>) map.get(surepay);
				if(issuesInfo!=null) {
					String caseStatus = (String) issuesInfo.get("caseStatus");
					String Feature = (String) issuesInfo.get("Feature");
					List<String> caselist = (List) issuesInfo.get("caselist");
					String assignee = (String) issuesInfo.get("Assignee");
					String labels = (String) issuesInfo.get("labels");
					String versions = (String) issuesInfo.get("Versions");
					if(caselist.size()>0) {
						String sql ="insert into temp_jira_status_tbl(casename,feature,surepay,casestatus,assignee,labels,datetime,stateflag,releases) "
								  + "values(?,?,?,?,?,?,datetime('now', 'localtime'),0,?)";
						ps = conn.prepareStatement(sql);
						for (String  casename : caselist) {
							
							ps.setString(1, casename.trim());
			                ps.setString(2, Feature.trim());
			                ps.setString(3, surepay.trim());
			                ps.setString(4, caseStatus.trim());
			                ps.setString(5, assignee.trim());
			                ps.setString(6, labels.trim());
			                ps.setString(7, versions.trim());
			                ps.addBatch();
						}
						ps.executeBatch();
						ps.close();
					}
				}
			}
			conn.commit();
			//merge into 
			String msql="select a.case_name,a.case_status,a.jira_id,a.release,b.* from DftTag_bak_20180718 a\r\n" + 
					"left join (\r\n" + 
					"select case casestatus\r\n" + 
					"when 'Pre-Pending' then 'PP' \r\n" + 
					"when 'Pending' then 'P'\r\n" + 
					"when 'Resubmit' then 'R'\r\n" + 
					"when 'Pre-Delete' then 'PD'\r\n" + 
					"when 'Delete' then 'O'\r\n" + 
					"when 'Pre-ReTag' then 'PRT'\r\n" + 
					"when 'ReTag' then 'RT'\r\n" + 
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
			
			
			String jsql="select * from jira_status_tbl where stateflag=0";
			ArrayList<HashMap<String, Object>> jira = jiraSeleniumDaoImpl.query(jdbc, jsql);
			
			
			logger.info("msql:=="+msql);
			logger.info("query:=="+query);
			String sql ="insert into jira_status_tbl (casename,feature,case_name_foregin,jira_id_old,jira_id_mid,jira_id_new,case_status_old,case_status_new,datatime) "
					+ "values(?,?,?,?,?,?,?,?,datetime('now', 'localtime'))";
			ps = conn.prepareStatement(sql);
			String usql="update DftTag_bak_20180718 set case_status=?,jira_id=? where case_name=?";
			ups = conn.prepareStatement(usql);
			
			String rsql="update DftTag_bak_20180718 set release=?,jira_id=? where case_name=?";
			rps = conn.prepareStatement(rsql);
			
			for (HashMap<String, Object> hashMap : query) {
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
				String case_status = hashMap.get("case_status").toString();
				if(		
						  ("PP".equals(ct))
						||("P".equals(ct)&&"PP".equals(case_status))
						||("O".equals(ct)&&"PP".equals(case_status))
					) {
					ps.setString(1, hashMap.get("casename").toString());
					ps.setString(2, hashMap.get("feature").toString());
					ps.setString(3, hashMap.get("case_name").toString());
					ps.setString(4, jira_id);
					ps.setString(5, hashMap.get("surepay").toString());
					ps.setString(6, jira_id_new.toString());
					ps.setString(7, case_status);
					ps.setString(8, ct);
					ps.addBatch();
	                ups.setString(1, ct);
	                ups.setString(2, jira_id_new.toString());
	                ups.setString(3, hashMap.get("case_name").toString());
	                ups.addBatch();
				}
				
				//需求变动：
				//1. 如果是ReTag状态数据，需要替换文件中的一个字段（通过发送一段shell命令实现）。
				//2. 更新目标表内数据。
				//3. 如果之前更新过（jira_status_tbl内有数据），需要回滚为原有数据。
				if("RT".equals(ct)){
					//如果releases不同，需要更新表内字段，发送shell命令
					if(!hashMap.get("releases").toString().equals(hashMap.get("release").toString())){
						rps.setString(1, hashMap.get("releases").toString());
						rps.setString(2, jira_id_new.toString());
						rps.setString(3, hashMap.get("case_name").toString());
						rps.addBatch();
						String[] cmd={"/bin/sh","-c","sed -i \"s/\\\"porting_release\\\": \\[.*\\]/\\\"porting_release\\\": \\[\\\""+hashMap.get("releases").toString()+"\\\"\\]/g\" /home/surepayftp/DftCase/"+hashMap.get("case_name").toString()};
						logger.info(cmd.toString());
						exeCmd(cmd);
				        //System.out.println(result);
					}
	                //其余的判断是否有过更新，有-回滚-记录，无-记录
	                ps.setString(1, hashMap.get("casename").toString());
					ps.setString(2, hashMap.get("feature").toString());
					ps.setString(3, hashMap.get("case_name").toString());
					ps.setString(4, jira_id);
					ps.setString(5, hashMap.get("surepay").toString());
					ps.setString(6, jira_id_new.toString());
					
					boolean bak=true;
	                for (HashMap<String, Object> hashMap2 : jira) {
	                	String case_name_foregin = hashMap2.get("case_name_foregin").toString();
	                	if(hashMap.get("case_name").toString().equals(case_name_foregin)) {
	                		
	                		System.out.println(case_name_foregin+" has bak , system rollbak");
	                		
	    	                ps.setString(7, ct);
	    					ps.setString(8, hashMap2.get("case_status_old").toString());
	    					
	    					ups.setString(1, hashMap2.get("case_status_old").toString());
	    	                ups.setString(2, jira_id_new.toString());
	    	                ups.setString(3, hashMap.get("case_name").toString());
	    	                ups.addBatch();
	    	                
	    					bak=false;
	    					
	                		break;
	                	}
					}
	                if(bak) {
	                	ps.setString(7, case_status);
	                	ps.setString(8, ct);
	                }
					ps.addBatch();
	                
				}
				
			}
			ps.executeBatch();
			ps.close();
			ups.executeBatch();
			ups.close();
			rps.executeBatch();
			rps.close();
			
			conn.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
			conn.rollback();
		}
	}
	public ArrayList<HashMap<String,Object>> getCommentJira() throws Exception{
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		Connection conn = jdbc.getConnection();
		
		String csql=
				"select group_concat(distinct (casename||'('||case_status_old||'-->'||\r\n" + 
				"case\r\n" + 
				"when case_status_new='RT' then 'RT-->'||case_status_old\r\n" + 
				"else case_status_new\r\n" + 
				"end\r\n" + 
				"||')')) casename,jira_id_mid\r\n" + 
				"from jira_status_tbl\r\n" + 
				"where stateflag=0\r\n" + 
				"and isComment = 0\r\n" + 
				"group by jira_id_mid;";
		ArrayList<HashMap<String,Object>> comment = jiraSeleniumDaoImpl.query(jdbc, csql);
		return comment;
	}
	public void updateCommentJira(String jira_id_mid) throws Exception{
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		if(!"".equals(jira_id_mid)) {
			String usql=
					"update jira_status_tbl set isComment=datetime('now','localtime')\r\n" + 
							"where stateflag=0\r\n" + 
							"and jira_id_mid='"+jira_id_mid+"'";
			jiraSeleniumDaoImpl.update(jdbc, usql);
		}else {
			logger.error("jira_id_mid is null,can not update");
		}
	}
	public static String exeCmd(String[] command) throws InterruptedException {
        String returnString = "";
        Process pro = null;
        Runtime runTime = Runtime.getRuntime();
        if (runTime == null) {
            System.err.println("Create runtime false!");
        }
        try {
            pro = runTime.exec(command);
            BufferedReader input = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            PrintWriter output = new PrintWriter(new OutputStreamWriter(pro.getOutputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                returnString = returnString + line + "\n";
            }
            input.close();
            output.close();
            pro.destroy();
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
        return returnString;
    }
    public static void main(String[] args) {
    	try {
			//String execCmd = execCmd("java -version",null);
			String cmd="sed -i \"s/\\\"porting_release\\\": \\[.*\\]/\\\"porting_release\\\": \\[\\\"SP31.2\\\"\\]/g\" /home/surepayftp/DftCase/78285/ft1554.json";
			System.out.println(cmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
