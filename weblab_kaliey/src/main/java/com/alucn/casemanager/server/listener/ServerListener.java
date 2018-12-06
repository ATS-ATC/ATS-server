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
import com.alucn.casemanager.server.common.util.ParamUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class ServerListener implements ServletContextListener {

	public void initialize() throws Exception {

		System.out.println("ServerListener >> contextInitialized");
		new Thread(new Runnable() {
			public void run() {
				Connection connection = null;
				try {
					Class.forName("org.sqlite.JDBC");
					String CaseInfoDB = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
					connection = DriverManager.getConnection("jdbc:sqlite:" + CaseInfoDB);
					String sql ="select * from serverList where status='Installing'";
					PreparedStatement prep = connection.prepareStatement(sql);
					ResultSet executeQuery = prep.executeQuery();
					List<Map<Object, Object>> resultSetToList = new ArrayList<Map<Object, Object>>();
					if(executeQuery.next()) {
						resultSetToList = resultSetToList(executeQuery);
					}
					System.out.println("Installing server >> "+resultSetToList);
					if(resultSetToList.size()>0) {
						List<String> serverListDB = new ArrayList<>();
						for (Map<Object, Object> map : resultSetToList) {
							String serverName = ""+map.get("serverName");
							serverListDB.add(serverName);
						}
						while(true) {
							if(serverListDB.size()==0) { 
								break;
							}else {
								List serverListSYS = new ArrayList<>();
								JSONArray currServersStatus = CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock,true,null);
								if(currServersStatus.size()>0) {
									for(int i=0; i<currServersStatus.size();i++){
										JSONObject tmpJsonObject = currServersStatus.getJSONObject(i);
										String serverName = tmpJsonObject.getJSONObject(Constant.LAB).getString(Constant.SERVERNAME);
										serverListSYS.add(serverName);
									}
									serverListSYS.retainAll(serverListDB);
									if(serverListSYS.size()>0) {
										System.out.println("serverListSYS >> "+serverListSYS);
										System.out.println("serverListDB >> "+serverListDB);
										for (Object object : serverListSYS) {
											//改内存为installing
											addToSYSLabStatus(currServersStatus,""+object,"Installing");
											//起线程监控状态
											new Thread(new Runnable() {
												@Override
												public void run() {
													while(true) {
														String installLabResult = HttpReq.reqUrl("http://135.251.249.124:9333/spadm/default/labapi/dailylab/"+object+".json").getJSONArray("content").getJSONObject(0).getString("status");
														if(Constant.REINSTALLLABSUCCESS.equals(installLabResult)){
															addToSYSLabStatus(currServersStatus,""+object,"Idle");
															break;
														}else if(Constant.REINSTALLLABFAIL.equals(installLabResult)){
															addToSYSLabStatus(currServersStatus,""+object,"Failed");
															break;
														}
														try {
															Thread.sleep(1000*60);
														} catch (InterruptedException e) {
															e.printStackTrace();
														}
													}
													Thread.currentThread().interrupt();
													System.out.println("serverList is installed , break thread.");
													return;
												}
											}).start();
											serverListDB.remove(""+object);
										}
									}
								}
								Thread.sleep(1000*10);
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
