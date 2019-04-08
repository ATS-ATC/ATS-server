package com.alucn.casemanager.server.listener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


import com.alucn.casemanager.server.common.CaseConfigurationCache;
import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.util.HttpReq;
import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.casemanager.server.common.util.ParamUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class ServerListener implements ServletContextListener {

	public void initialize() throws Exception {

		System.out.println("ServerListener >> contextInitialized");
		new Thread(new Runnable() {
			public void run() {
				//Connection connection = null;
				try {
					/*Class.forName("org.sqlite.JDBC");
					String CaseInfoDB = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
					connection = DriverManager.getConnection("jdbc:sqlite:" + CaseInfoDB);
					String sql ="select * from serverList where status='Installing'";
					PreparedStatement prep = connection.prepareStatement(sql);
					ResultSet executeQuery = prep.executeQuery();
					System.out.println("Installing server executeQuery >> "+executeQuery);*/
					
					
					String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
					JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
					String queryConfigs = "select * from serverList";
					//String queryConfigs = "select * from serverList where status='Installing'";
					ArrayList<HashMap<String,Object>> configs = jdbc.query(queryConfigs);
					
					System.out.println("Installing server >> "+configs);
					if(configs.size()>0) {
						List<String> serverListDB = new ArrayList<>();
						Map<String, String> serverListMDB = new HashMap<String, String>();
						
						for (HashMap<String, Object> map : configs) {
							String serverName = ""+map.get("serverName");
							String deptid = ""+map.get("deptid");
							serverListDB.add(serverName);
							serverListMDB.put(serverName, deptid);
						}
						int count = 0;
						while(true) {
							System.out.println("while >> "+serverListDB);
							if(serverListDB.size()==0) {
								System.out.println("while >> serverListDB.size()==0  >>  break");
								break;
							}else {
								if(count>=60) {//等待10分钟，如果十分钟还没有则在内存中放入lost状态
									JSONArray infos = new JSONArray();
									//在其他insert的地方添加字段
									for (String serverName : serverListDB) {
										infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"deptid\":\""+serverListMDB.get(serverName)+"\",\"serverName\":\""+serverName+"\",\"serverIp\":\"0.0.0.0\",\"serverRelease\":\"SP**.*\",\"serverProtocol\":\"*\",\"serverType\": \"Line\",\"serverMate\": \"Standalone\",\"mateServer\": \"N\",\"setName\": \"set1\",\"serverSPA\":[],\"serverRTDB\":[]},\"taskStatus\":{\"status\":\"Lost\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
									}
									for(int i=0; i<infos.size(); i++){
										CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock,false,infos.getJSONObject(i).getJSONObject(Constant.BODY));
									}
									break;
								}
								List serverListSYS = new ArrayList<>();
								JSONArray currServersStatus = CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock,true,null);
								System.out.println("currServersStatus  >> "+currServersStatus);
								if(currServersStatus.size()>0) {
									for(int i=0; i<currServersStatus.size();i++){
										JSONObject tmpJsonObject = currServersStatus.getJSONObject(i);
										String serverName = tmpJsonObject.getJSONObject(Constant.LAB).getString(Constant.SERVERNAME);
										serverListSYS.add(serverName);
									}
									System.out.println("serverListDB >> "+serverListDB);
									serverListDB.removeAll(serverListSYS);
									System.out.println("serverListDB >> removeAll >> "+serverListDB);
								}
								count++;
								Thread.sleep(1000*10); //10s检查一次
							}
						}
					}else {
						Thread.currentThread().interrupt();
						System.out.println("serverList is null , break thread.");
						return;
					}
				}catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
		}).start();
	
	}
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub

	}
	public static void addToSYSLabStatus(JSONArray currServersStatus,String server,String status) {
		for (int i = 0; i < currServersStatus.size(); i++) {
			JSONObject serverBody = currServersStatus.getJSONObject(i);
			String serverName = serverBody.getJSONObject(Constant.LAB).getString(Constant.SERVERNAME);
			if(server.equals(serverName)) {
				serverBody.getJSONObject(Constant.TASKSTATUS).put(Constant.STATUS, status);
				CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock,false,serverBody);
			}
		}
	}
	public static List<Map<Object, Object>> resultSetToList(ResultSet rs) throws java.sql.SQLException {   
        if (rs == null)   
            return Collections.EMPTY_LIST;   
        ResultSetMetaData md = rs.getMetaData(); //得到结果集(rs)的结构信息，比如字段数、字段名等   
        int columnCount = md.getColumnCount(); //返回此 ResultSet 对象中的列数   
        List<Map<Object, Object>> list = new ArrayList();   
        Map rowData = new HashMap();   
        while (rs.next()) {   
         rowData = new HashMap(columnCount);   
         for (int i = 1; i <= columnCount; i++) {   
                 rowData.put(md.getColumnName(i), rs.getObject(i));   
         }   
         list.add(rowData);   
         System.out.println("list:" + list.toString());   
        }   
        return list;   
	}
}
