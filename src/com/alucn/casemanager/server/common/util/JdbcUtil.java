package com.alucn.casemanager.server.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;
import com.alucn.casemanager.server.listener.MainListener;


/**
 * jdbc connect
 * @author wanghaiqi
 *
 */
public class JdbcUtil {
	private static Logger logger = Logger.getLogger(JdbcUtil.class);
	private static Properties prop = new Properties();
	private Properties jdbc = null;
	static {
		InputStream in =null;
		try {
			String confFile=MainListener.configFilesPath+File.separator+"dbconfig.properties";
			in = new FileInputStream(confFile); 
			prop.load(in);  
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (in != null) {  
                try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            } 
		}
	}
	
	
	public JdbcUtil(String dataSourceName,String dbFilePath) throws Exception {
		jdbc = new Properties();
		jdbc.put("driverClassName", prop.getProperty(dataSourceName+".driverClassName"));
		jdbc.put("url", prop.getProperty(dataSourceName+".url")+dbFilePath);
	}
	
	
	/**
	 * getConnection
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException 
	 */
	
	public Connection getConnection() throws SQLException, ClassNotFoundException {
		Connection connection = DriverManager.getConnection(jdbc.getProperty("url"));
		Class.forName(jdbc.getProperty("driverClassName")); 
		return connection;
	}
	
	
	/**
	 * execute i d u
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException 
	 */
	public int executeUpdate(String sql, Object[] params) throws SQLException, ClassNotFoundException {
		int rtn = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		conn = getConnection();
		conn.setAutoCommit(false);  
		pstmt = conn.prepareStatement(sql);
		if(params != null && params.length > 0) {
			for(int i = 0; i < params.length; i++) {
				pstmt.setObject(i + 1, params[i]);  
			}
		}
		rtn = pstmt.executeUpdate();
		conn.commit();
		closeConn(conn, pstmt, null);
		return rtn;
	}
	
	
	/**
	 * executeQuery
	 * @param sql
	 * @param params
	 * @param callback
	 * @throws Exception
	 */
	public void executeQuery(String sql, Object[] params, 
			QueryCallback callback) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		conn = getConnection();
		pstmt = conn.prepareStatement(sql);
		
		if(params != null && params.length > 0) {
			for(int i = 0; i < params.length; i++) {
				pstmt.setObject(i + 1, params[i]);   
			}
		}
		rs = pstmt.executeQuery();
		callback.process(rs);  
		closeConn(conn, pstmt, rs);
	}
	
	/**
	 * executeBatch
	 * @param sql
	 * @param paramsList
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException 
	 */
	public int[] executeBatch(String sql, List<Object[]> paramsList) throws SQLException, ClassNotFoundException {
		int[] rtn = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		conn = getConnection();
		conn.setAutoCommit(false);  
		pstmt = conn.prepareStatement(sql);
		
		if(paramsList != null && paramsList.size() > 0) {
			for(Object[] params : paramsList) {
				for(int i = 0; i < params.length; i++) {
					pstmt.setObject(i + 1, params[i]);  
				}
				pstmt.addBatch();
			}
		}
		
		rtn = pstmt.executeBatch();
		conn.commit();
		closeConn(conn, pstmt, null);
		return rtn;
	}
	
	public void executeSql(String sql) throws ClassNotFoundException {
		Connection conn = null;
		Statement stm = null;
		try {
			conn = getConnection();
			stm = conn.createStatement();
			conn.setAutoCommit(false);
			stm.executeUpdate(sql);
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				closeConn(conn, stm, null);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}
	
	
	public static interface QueryCallback {
		void process(ResultSet rs) throws Exception;
	}
	
	
	
	private static void closeConn(Connection conn, Statement stmt, ResultSet rs)  throws SQLException {
		if(rs != null){
			rs.close();
		 }
		if(stmt != null){   
			stmt.close();
		}
		if(conn != null){
			conn.close();
		}
	}
	
	public ArrayList<HashMap<String,Object>> query(String sql) throws SQLException, ClassNotFoundException{
		ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
		Connection conn = null;
		Statement stm = null;
		ResultSet rs = null;
		
		conn =  getConnection();
		stm = conn.createStatement();
		rs = stm.executeQuery(sql);
		while(rs.next()){
			ResultSetMetaData rsmd = rs.getMetaData();
			HashMap<String,Object> map = new HashMap<String, Object>();
			int columnCount = rsmd.getColumnCount();
			for(int i =1 ; i<=columnCount ;i++){
				String columnName = rsmd.getColumnLabel(i);
				map.put(columnName, rs.getString(columnName));
			}
			list.add(map);
		}
		closeConn(conn, stm, rs);
		return list;
	}
	
	
    public  List<Map<String, Object>> findModeResult(String sql, List<Object> params) throws Exception{  
    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    	Connection conn =null;
    	PreparedStatement pstmt  =  null;
    	ResultSet resultSet =null;
        int index = 1;  
         conn = getConnection();
		 pstmt = (PreparedStatement) conn.prepareStatement(sql);
		 
        if(params != null && !params.isEmpty()){  
            for(int i = 0; i<params.size(); i++){  
                pstmt.setObject(index++, params.get(i));  
            }  
        }  
        resultSet = pstmt.executeQuery();  
        ResultSetMetaData metaData = resultSet.getMetaData();  
        int cols_len = metaData.getColumnCount();  
        while(resultSet.next()){  
            Map<String, Object> map = new HashMap<String, Object>();  
            for(int i=0; i<cols_len; i++){  
                String cols_name = metaData.getColumnName(i+1);  
               if(null==cols_name){
            	   logger.warn( cols_name+"��colsName is null...");
               }
                Object cols_value = resultSet.getObject(cols_name);  
                if(cols_value == null){  
                    cols_value = "";  
                }  
                map.put(cols_name, cols_value);  
            }  
            list.add(map);  
        }  
    	closeConn(conn, pstmt, resultSet);
        return list;  
    }
	
    
   
    public  boolean updateByPreparedStatement(String sql, List<Object>params) throws SQLException, ClassNotFoundException{ 
        boolean flag = false;  
        int result = -1;  
        Connection conn =null;
    	PreparedStatement pstmt  =  null;
    	conn = getConnection();
    	pstmt = (PreparedStatement) conn.prepareStatement(sql);
		 int index = 1;  
	        if(params != null && !params.isEmpty()){  
	            for(int i=0; i<params.size(); i++){  
	                pstmt.setObject(index++, params.get(i));  
	            }  
	        }  
	        result = pstmt.executeUpdate();  
	        flag = result > 0 ? true : false;  
		closeConn(conn, pstmt, null);
        return flag;  
    }  
}
