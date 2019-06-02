package com.alucn.weblab.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
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
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alucn.casemanager.server.listener.MainListener;

public class JDBCHelper {
	
	private static Logger logger = Logger.getLogger(JDBCHelper.class);
	private static ConcurrentHashMap<String,DruidDataSource> dataSourceMap = new ConcurrentHashMap<String,DruidDataSource>();
	private static Properties prop = new Properties();
	private static JDBCHelper instance = null;
	public static ThreadLocal<String> DataSourceName = new ThreadLocal<String>();
	private static ConcurrentHashMap<String,JDBCHelper> dataMap = new ConcurrentHashMap<String,JDBCHelper>();
	
	static {
		InputStream in =null;
		try {
			String confFile = "";
			String configPath = System.getenv("WEBLAB_CONF");
			if(configPath == null){
			    in = JDBCHelper.class.getResourceAsStream("/dbconfig.properties");
				
			}else{//server
				confFile = configPath +File.separator+"dbconfig.properties";
				logger.error("load confFile: " + confFile);
				in = new FileInputStream(confFile); 
			}
			
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
	
	public static JDBCHelper getInstance(String dataSourceName) {
		if(StringUtils.isEmpty(dataSourceName)){
		    logger.error("please specify the database name");
		}
		DataSourceName.set(dataSourceName+".");
		if(dataMap.get(DataSourceName.get()) == null) {
			synchronized(JDBCHelper.class) {
				if(dataMap.get(DataSourceName.get()) == null) {
					try {
                        instance = new JDBCHelper();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        logger.error(e);
                    }
					dataMap.put(DataSourceName.get(), instance);
				}
			}
		}
		return dataMap.get(DataSourceName.get());
	}
	
	private JDBCHelper() throws Exception {
		Properties jdbc = new Properties();
		jdbc.put("driverClassName", prop.getProperty(DataSourceName.get()+"driverClassName"));
		jdbc.put("url", prop.getProperty(DataSourceName.get()+"url"));
		jdbc.put("username", prop.getProperty(DataSourceName.get()+"username"));
		jdbc.put("password", prop.getProperty(DataSourceName.get()+"password"));
		jdbc.put("filters", prop.getProperty(DataSourceName.get()+"filters"));
		jdbc.put("initialSize", prop.getProperty(DataSourceName.get()+"initialSize"));
		jdbc.put("maxActive", prop.getProperty(DataSourceName.get()+"maxActive"));
		jdbc.put("maxWait", prop.getProperty(DataSourceName.get()+"maxWait"));
		jdbc.put("timeBetweenEvictionRunsMillis", prop.getProperty(DataSourceName.get()+"timeBetweenEvictionRunsMillis"));
		jdbc.put("minEvictableIdleTimeMillis", prop.getProperty(DataSourceName.get()+"minEvictableIdleTimeMillis"));
		jdbc.put("validationQuery", prop.getProperty(DataSourceName.get()+"validationQuery"));
		jdbc.put("testWhileIdle", prop.getProperty(DataSourceName.get()+"testWhileIdle"));
		jdbc.put("testOnBorrow", prop.getProperty(DataSourceName.get()+"testOnBorrow"));
		jdbc.put("testOnReturn", prop.getProperty(DataSourceName.get()+"testOnReturn"));
		jdbc.put("poolPreparedStatements", prop.getProperty(DataSourceName.get()+"poolPreparedStatements"));
		jdbc.put("maxPoolPreparedStatementPerConnectionSize", prop.getProperty(DataSourceName.get()+"maxPoolPreparedStatementPerConnectionSize"));
		jdbc.put("removeAbandoned", prop.getProperty(DataSourceName.get()+"removeAbandoned"));
		jdbc.put("removeAbandonedTimeout", prop.getProperty(DataSourceName.get()+"removeAbandonedTimeout"));
		dataSourceMap.put(DataSourceName.get(), (DruidDataSource)DruidDataSourceFactory.createDataSource(jdbc));
	}
	
	
	/**
	 * <p>Title: getConnection</p>  
	 * <p>Description: </p>  
	 * @return
	 * @throws SQLException
	 */
	public synchronized Connection getConnection() throws SQLException {
		return dataSourceMap.get(DataSourceName.get()).getConnection();
	}
	
	
	/**
	 * <p>Title: executeUpdate</p>  
	 * <p>Description: insert update delete</p>  
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public int executeUpdate(String sql, Object[] params) throws SQLException {
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
	 * <p>Title: executeQuery</p>  
	 * <p>Description: select</p>  
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
	 * <p>Title: executeBatch</p>  
	 * <p>Description: </p>  
	 * @param sql
	 * @param paramsList
	 * @return
	 * @throws SQLException
	 */
	public int[] executeBatch(String sql, List<Object[]> paramsList) throws SQLException {
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
	
	/**  
	* <p>Title: QueryCallback</p>  
	* <p>Description: </p>  
	* @author haiqiw  
	* @date 2019-1-8  
	*/ 
	public static interface QueryCallback {
		
		/**
		 * <p>Title: process</p>  
		 * <p>Description: </p>  
		 * @param rs
		 * @throws Exception
		 */
		void process(ResultSet rs) throws Exception;
		
	}
	
	
	/**
	 * <p>Title: closeConn</p>  
	 * <p>Description: close</p>  
	 * @param conn
	 * @param stmt
	 * @param rs
	 * @throws SQLException
	 */
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
	/**
	 * <p>Title: query</p>  
	 * <p>Description: select result to map </p>  
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	
	public int executeSql(String sql) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        int count = 0;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            conn.setAutoCommit(false);
            ps.executeUpdate();
            count = ps.getUpdateCount();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            conn.rollback();
        } finally {
            closeConn(conn, ps, null);
        }
        return count;
    }
    public int executeSqlReturnId(String sql) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int id=-1;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            conn.setAutoCommit(false);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs != null&&rs.next()) {
                id=rs.getInt(1);
            }
            //System.out.println("kaliey jdbc : " + sql);
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            conn.rollback();
        } finally {
            closeConn(conn, ps, rs);
        }
        return id;
    }
    
	public ArrayList<HashMap<String,Object>> query(String sql) throws SQLException{
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
	
	/**
	 * <p>Title: findModeResult</p>  
	 * <p>Description: </p>  
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
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
            	   logger.warn( cols_name+"：colsName is null--------------------------------");
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
	
    
    /**
     * <p>Title: updateByPreparedStatement</p>  
     * <p>Description: </p>  
     * @param sql
     * @param params
     * @return
     * @throws SQLException
     */
    public  boolean updateByPreparedStatement(String sql, List<Object>params) throws SQLException{ 
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