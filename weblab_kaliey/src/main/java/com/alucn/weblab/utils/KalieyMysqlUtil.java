package com.alucn.weblab.utils;


import java.beans.PropertyVetoException;
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
import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;

@Component
public class KalieyMysqlUtil {
	
	private static volatile KalieyMysqlUtil dbConnection;
    private ComboPooledDataSource cpds;
	
    private KalieyMysqlUtil() {
        try {
            /**通过属性文件获取数据库连接的参数值**/
            Properties properties = new Properties();
            Class clazz = KalieyMysqlUtil.class;
            InputStream in = clazz.getResourceAsStream("/kaliey-jdbc.properties");
            properties.load(in);
            
            /**获取属性文件中的值**/
            String driverClassName = properties.getProperty("driverClass");
            String url = properties.getProperty("url");
            String username = properties.getProperty("user");
            String password = properties.getProperty("password");

            /**数据库连接池对象**/
            cpds = new ComboPooledDataSource();

            /**设置数据库连接驱动**/
            cpds.setDriverClass(driverClassName);
            /**设置数据库连接地址**/
            cpds.setJdbcUrl(url);
            /**设置数据库连接用户名**/
            cpds.setUser(username);
            /**设置数据库连接密码**/
            cpds.setPassword(password);

            /**初始化时创建的连接数,应在minPoolSize与maxPoolSize之间取值.默认为3**/
            cpds.setInitialPoolSize(3);
            /**连接池中保留的最大连接数据.默认为15**/
            cpds.setMaxPoolSize(10);
            /**当连接池中的连接用完时，C3PO一次性创建新的连接数目;**/
            cpds.setAcquireIncrement(1);
            /**隔多少秒检查所有连接池中的空闲连接,默认为0表示不检查;**/
            cpds.setIdleConnectionTestPeriod(60);
            /**最大空闲时间,超过空闲时间的连接将被丢弃.为0或负数据则永不丢弃.默认为0;**/
            cpds.setMaxIdleTime(3000);

            /**因性能消耗大请只在需要的时候使用它。如果设为true那么在每个connection提交的
             时候都将校验其有效性。建议使用idleConnectionTestPeriod或automaticTestTable
             等方法来提升连接测试的性能。Default: false**/
            cpds.setTestConnectionOnCheckout(true);

            /**如果设为true那么在取得连接的同时将校验连接的有效性。Default: false **/
            cpds.setTestConnectionOnCheckin(true);
            /**定义在从数据库获取新的连接失败后重复尝试获取的次数，默认为30;**/
            cpds.setAcquireRetryAttempts(30);
            /**两次连接中间隔时间默认为1000毫秒**/
            cpds.setAcquireRetryDelay(1000);
            /** 获取连接失败将会引起所有等待获取连接的线程异常,
             但是数据源仍有效的保留,并在下次调用getConnection()的时候继续尝试获取连接.如果设为true,
             那么尝试获取连接失败后该数据源将申明已经断开并永久关闭.默认为false**/
            cpds.setBreakAfterAcquireFailure(true);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }

    }
    
    
    /**
     * 获取数据库连接对象，单例
     *
     * @return
     */
    //@Bean
    public static KalieyMysqlUtil getInstance() {
    	//System.out.println("synchronized create bean");
        if (dbConnection == null) {
            synchronized (KalieyMysqlUtil.class) {
                if (dbConnection == null) {
                    dbConnection = new KalieyMysqlUtil();
                }
            }
        }
        return dbConnection;
    }

    /**
     * 获取数据库连接
     *
     * @return 数据库连接
     */
    public Connection getConnection() throws SQLException {
        return cpds.getConnection();
    }

    /**
     * finalize()方法是在垃圾收集器删除对象之前对这个对象调用的。
     *
     * @throws Throwable
     */
    protected void finalize() throws Throwable {
        DataSources.destroy(cpds);
        super.finalize();
    }
	//url
	/*private static String url = null;
	//user
	private static String user = null;
	//password
	private static String password = null;
	//驱动程序类
	private static String driverClass = null;
	*//**
	 * 只注册一次，静态代码块
	 *//*
	static{
	
		//注册驱动程序
		try {
			
			*//**
			 * 读取jdbc.properties文件
			 *//*
			//1)创建Properties对象
			Properties prop = new Properties();
			//构造输入流
			*//**
			 * 相对路径：  .  这个点代表当前目录。当前目录本质上是java命令运行的目录
			 * java项目：  在ecplise中，当前目录指向项目的根目录。
			 * web项目： 当前目录指向%tomcat%/bin目录
			 *   1)结论： 在web项目不能使用相对路径
			 *   
			 *   web项目中加载配置文件： ServletContext.getRealPath()  /  getResourceAsStream() 这种方式对于jdbcUtil这个工具而言，放到java项目中找不到ServletContext对象，不通用的！
			 *   2)不能使用ServletContext读取文件
			 *   
			 *   3）使用类路径方式读取配置文件
			 *   
			 *//*
			//1)获取类的对象
		    Class clazz = KalieyMysqlUtil.class;
		    //2) 使用类路径的读取方法去读取文件
		    *//**
		     *   这个斜杠：代表项目的类路径的根目录。  类路径： 查询类的目录/路径
		     *   java项目下： 类路径的根目录，指向项目的bin目录
		     *   web项目下：类路径的根目录，指向项目的WEB-INF/classes目录
		     *   
		     *   只有把配置文件放在src目录的根目录下，那么这些文件就会自动拷贝到项目的类路径根目录下。
		     *//*
		    InputStream in = clazz.getResourceAsStream("/kaliey-jdbc.properties");
		    // File f = new File(clazz.getResource("/").getFile());
			// System.out.println(f.getAbsolutePath());
			//构造输入流
		    // InputStream in = new FileInputStream("./src/jdbc.properties");
			//2)加载文件
			prop.load(in);
			//3)读取文件内容
			url = prop.getProperty("url");
			user = prop.getProperty("user");
			password = prop.getProperty("password");
			driverClass = prop.getProperty("driverClass");
			
			// 测试是否从properties中读到
			
			System.out.println(url);
			System.out.println(user);
			System.out.println(password);
			System.out.println(driverClass);
			
			
			Class.forName(driverClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	
	
	/**
	 * 获取连接方法
	 */
	/*public static Connection getConnection(){
		try {
			Connection conn = DriverManager.getConnection(url, user, password);
			return conn;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}*/
	
	/**
	 * 释放资源的方法
	 */
	public static void close(Statement stmt,Connection conn){
		if(stmt!=null){
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		
		if(conn!=null){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * 释放资源的方法
	 */
	public static void close(ResultSet rs,Statement stmt,Connection conn){
		if(rs!=null){
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		if(stmt!=null){
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		
		if(conn!=null){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}
	
	public static ArrayList<HashMap<String,Object>> query(String sql) {
		ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
		Connection connection = null;
		try {
			connection = KalieyMysqlUtil.getInstance().getConnection();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		try {
			PreparedStatement prepareStatement = connection.prepareStatement(sql);
			ResultSet rs = prepareStatement.executeQuery();
			//System.out.println("kaliey jdbc : " + sql);
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
			close(rs, prepareStatement, connection);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	public static void executeSql(String sql) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = KalieyMysqlUtil.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			conn.setAutoCommit(false);
			ps.executeUpdate();
			//System.out.println("kaliey jdbc : " + sql);
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			conn.rollback();
		} finally {
			close(null,ps,conn);
		}
	}
	public static int executeSqlReturnId(String sql) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		int id=-1;
		try {
			conn = KalieyMysqlUtil.getInstance().getConnection();
			ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			conn.setAutoCommit(false);
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs != null&&rs.next()) {
			    id=rs.getInt(1);
			}
			//System.out.println("kaliey jdbc : " + sql);
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			conn.rollback();
		} finally {
			close(null,ps,conn);
		}
		return id;
	}
	//使用动态参数，更新数据-->插入记录，更新记录，删除记录（不需要返回）
	/*
	* String sql="insert into t_book (bookname,author,publisher,create_date) values(,,,)";
	*JDBCutil.executeUpdate(sql,book.getBookname(),book.getAuthor(),book.getPublisher(),book.getCreate_date());
	*/
	public static void executeUpdate(String sql,Object...params){//
		Connection conn = null;
		PreparedStatement ps=null;
		try {
			conn = KalieyMysqlUtil.getInstance().getConnection();
			ps=conn.prepareStatement(sql);
			if(null!=params)for(int i=0;i<params.length;i++){
				ps.setObject(i+1, params[i]);
			}
			//System.out.println("kaliey jdbc : " + sql);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			close(null,ps,conn);
		}
	}
	public static void executeBatch(String sql,List<List> params){//
		Connection conn = null;
		PreparedStatement ps=null;
		try {
			conn = KalieyMysqlUtil.getInstance().getConnection();
			ps=conn.prepareStatement(sql);
			if(null!=params) {
				for (List list : params) {
					for(int i = 0;i<list.size();i++) {
						ps.setObject(i+1, list.get(i));
					}
					ps.addBatch();
					//System.out.println("kaliey jdbc : " + sql);
					ps.executeBatch();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			close(null,ps,conn);
		}
	}
	public static void main(String[] args) throws SQLException {
		String title = "test2";
		String server = "test2";
		String login = "test2";
		/*ArrayList<HashMap<String, Object>> query = KalieyMysqlUtil.query("select * from case_depends");
		System.out.println(query);*/
		String isql ="insert into kaliey.n_rerunning_case_tbl(title,server_info,query_condition,author,`create_time`) values('"+title +"','"+server+"','','"+login+"',now())";
		int id = KalieyMysqlUtil.executeSqlReturnId(isql);
		System.out.println(id);
	}
	
}
