package com.alucn.weblab.service;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.InputStreamReader;
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
					//String target = (String) issuesInfo.get("target");
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
						//int i =0;
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
			                ps.setString(7, versions.trim());
			                ps.addBatch();
			                /*if (i % 1000 == 0) {
			                     ps.executeBatch();
			                     ps.clearBatch(); // 清空缓存
			                }
			                i++;*/
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
			//System.out.println(query);
			logger.info(query);
			String sql ="insert into jira_status_tbl (casename,feature,case_name_foregin,jira_id_old,jira_id_mid,jira_id_new,case_status_old,case_status_new,datatime) "
					+ "values(?,?,?,?,?,?,?,?,datetime('now', 'localtime'))";
			ps = conn.prepareStatement(sql);
			//int i = 0;
			String usql="update DftTag set case_status=?,jira_id=? where case_name=?";
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
				String case_status = hashMap.get("case_status").toString();
				if(		
						  ("PP".equals(ct))
						||("P".equals(ct)&&"PP".equals(case_status))
						||("O".equals(ct)&&"PP".equals(case_status))
						//||("O".equals(ct)&&("PP".equals(case_status)||"PD".equals(case_status)))
						//||("RT".equals(ct)&&("PP".equals(case_status)||"PRT".equals(case_status)))
						
					) {
					//System.out.println("case_status/temp: "+ct+" + target: "+case_status);
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
					ps.setString(7, case_status);
					ps.setString(8, ct);
					//ps.execute();
					ps.addBatch();
	                /* if (i % 1000 == 0) {
	                     ps.executeBatch();
	                     ps.clearBatch(); // 清空缓存
	                }*/
					//String usql="update DftTag_bak_20180626 set case_status='"+ct+"',jira_id='"+jira_id_new+"' where case_name='"+hashMap.get("case_name")+"'";
					//ps = conn.prepareStatement(usql);
					//ps.execute();
	                ups.setString(1, ct);
	                ups.setString(2, jira_id_new.toString());
	                ups.setString(3, hashMap.get("case_name").toString());
	                ups.addBatch();
	                /*if (i % 1000 == 0) {
	                	ups.executeBatch();
	                	ups.clearBatch(); // 清空缓存
	                }
	                i++;*/
				}
				if("RT".equals(ct)){
					//需求变动：
					//1. 如果是ReTag状态数据，需要替换文件中的一个字段（通过发送一段shell命令实现）。
					//2. 更新目标表内数据。
					//3. 如果之前更新过（jira_status_tbl内有数据），需要回滚为原有数据。
					String cmd="sed -i \"s/\\\"porting_release\\\": \\[.*\\]/\\\"porting_release\\\": \\[\\\""+hashMap.get("releases").toString()+"\\\"\\]/g\" /home/surepayftp/DftCase/"+hashMap.get("case_name").toString();
					logger.info(cmd);
					System.out.println(cmd);
					/*String result = execCmd(cmd, null);
			        System.out.println(result);*/
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
	
	
	public void testTempJiraTbl(Map map) throws SQLException {

		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB");
		JdbcUtil jdbc = null;
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ups = null;
		PreparedStatement rps = null;
		PreparedStatement bps = null;
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
			
			String rsql="update DftTag_bak_20180718 set release=? where case_name=?";
			rps = conn.prepareStatement(rsql);
			
			String bsql="update DftTag_bak_20180718 set case_status=? where case_name=?";
			bps = conn.prepareStatement(bsql);
			
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
						rps.setString(2, hashMap.get("case_name").toString());
						rps.addBatch();
						String cmd="sed -i \"s/\\\"porting_release\\\": \\[.*\\]/\\\"porting_release\\\": \\[\\\""+hashMap.get("releases").toString()+"\\\"\\]/g\" /home/surepayftp/DftCase/"+hashMap.get("case_name").toString();
						logger.info(cmd);
						String result = execCmd(cmd, null);
				        System.out.println(result);
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
	                		
	                		System.out.println(case_name_foregin+" has bak sys rollbak");
	                		
	                		bps.setString(1, hashMap2.get("case_status_old").toString());
	    	                bps.setString(2, hashMap.get("case_name").toString());
	    	                bps.addBatch();
	    	                
	    	                ps.setString(7, ct);
	    					ps.setString(8, hashMap2.get("case_status_old").toString());
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
			bps.executeBatch();
			bps.close();
			
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
				"select \r\n" + 
				"group_concat(distinct (casename||'('||case_status_old||'-->'||case_status_new||')')) casename,jira_id_mid \r\n" + 
				"from jira_status_tbl\r\n" + 
				"where stateflag = 0\r\n" + 
				"and isComment = 0\r\n" + 
				"group by jira_id_mid";
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
	/**
     * 执行系统命令, 返回执行结果
     *
     * @param cmd 需要执行的命令
     * @param dir 执行命令的子进程的工作目录, null 表示和当前主进程工作目录相同
     */
    public static String execCmd(String cmd, File dir) throws Exception {
        StringBuilder result = new StringBuilder();

        Process process = null;
        BufferedReader bufrIn = null;
        BufferedReader bufrError = null;

        try {
            // 执行命令, 返回一个子进程对象（命令在子进程中执行）
            process = Runtime.getRuntime().exec(cmd, null, dir);

            // 方法阻塞, 等待命令执行完成（成功会返回0）
            process.waitFor();

            // 获取命令执行结果, 有两个结果: 正常的输出 和 错误的输出（PS: 子进程的输出就是主进程的输入）
            bufrIn = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            bufrError = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"));

            // 读取输出
            String line = null;
            while ((line = bufrIn.readLine()) != null) {
                result.append(line).append('\n');
            }
            while ((line = bufrError.readLine()) != null) {
                result.append(line).append('\n');
            }

        } finally {
            closeStream(bufrIn);
            closeStream(bufrError);

            // 销毁子进程
            if (process != null) {
                process.destroy();
            }
        }

        // 返回执行结果
        return result.toString();
    }

    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception e) {
                // nothing
            }
        }
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
