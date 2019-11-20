package com.alucn.weblab.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.weblab.dao.impl.UserDaoImpl;
import com.alucn.weblab.model.NUser;
import com.alucn.weblab.utils.JDBCHelper;
import com.alucn.weblab.utils.MD5Util;
import com.alucn.casemanager.server.common.util.DeEncode;
import com.alucn.casemanager.server.common.util.Ldap;

@Service("loginService")
public class LoginService {
	
	@Autowired(required=true)
	private UserDaoImpl userDaoImpl;
	
	public ArrayList<HashMap<String, Object>> findUrlPermissions() throws Exception {
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
	    JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
		String sql = "select * from kaliey.n_permission_url where stateflag=0 and type='menu'";
		ArrayList<HashMap<String, Object>> query = userDaoImpl.query(jdbc, sql);
		return query;
	}
	@Cacheable(value = "roles")
	public ArrayList<HashMap<String, Object>> getRolesByName(String username) throws Exception{
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
	    JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
		String sql = "select * from kaliey.n_user a " + 
				"left join kaliey.n_user_role b on a.id=b.user_id and b.stateflag=0 " + 
				"left join kaliey.n_role c on b.role_id=c.id and c.stateflag=0 " + 
				"where a.stateflag=0 " + 
				"and a.username='"+username.trim()+"'";
		ArrayList<HashMap<String, Object>> query = userDaoImpl.query(jdbc, sql);
		return query;
	}
	@Cacheable(value = "permissions")
	public ArrayList<HashMap<String, Object>> getPermissionsByUserName(String username) throws Exception{
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		String sql = "select distinct d.permission_name from kaliey.n_user a "
				+ "left join kaliey.n_user_role b on a.id=b.user_id "
				+ "left join kaliey.n_role_permission c on b.role_id=c.role_id "
				+ "left join kaliey.n_permission d on d.id=c.permission_id "
				+ "where a.stateflag=0 "
				+ "and b.stateflag=0 "
				+ "and c.stateflag=0 "
				+ "and d.stateflag=0 "
				+ "and a.username='"+username.trim()+"'";
		JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
		ArrayList<HashMap<String, Object>> query = userDaoImpl.query(jdbc, sql);
		return query;
	}
	
	public void createLdapUser(NUser nuser) throws Exception {
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		String password = MD5Util.md5(nuser.getPassword());
		//String password = nuser.getPassword();
		String fsql = "select id from kaliey.n_user where username='"+nuser.getUsername().trim()+"'";;
		JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
		ArrayList<HashMap<String, Object>> flag = userDaoImpl.query(jdbc, fsql);
		if(flag.size()>0) {
			return;
		}
		String sql = "insert into kaliey.n_user (username,password) values ('"+nuser.getUsername().trim()+"','"+password.trim()+"')";
		userDaoImpl.insert(jdbc, sql);
		String qsql = "select id from kaliey.n_user where stateflag=0 and username='"+nuser.getUsername().trim()+"'";;
		ArrayList<HashMap<String, Object>> query = userDaoImpl.query(jdbc, qsql);
		int id = Integer.parseInt((String)query.get(0).get("id"));
		//默认创建的为普通用户  role_id=3
		String rsql = "insert into kaliey.n_user_role (user_id,role_id) values ("+id+","+3+")";
		userDaoImpl.insert(jdbc, rsql);
		//默认创建的为普通部门  role_id=1
		String dsql = "insert into kaliey.n_user_dept (user_id,dept_id) values ("+id+","+1+")";
		userDaoImpl.insert(jdbc, dsql);
	} 
	//不排除逻辑删除账号
	@Cacheable(value = "userAll")
	public ArrayList<HashMap<String, Object>> queryAllNUser(NUser nuser) throws Exception{
    	/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		String sql = "select * from kaliey.n_user where username='"+nuser.getUsername().trim()+"'";
		JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
		ArrayList<HashMap<String, Object>> result = userDaoImpl.query(jdbc, sql);
        return result;
	}
	//只取状态正常的账号
    public ArrayList<HashMap<String, Object>> queryUserFromEmail(String email) throws Exception{
        String sql = "select user_name from cases_info_db.user_info where to_reporter <> '' and mail = '"+email.trim()+"'";
        JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
        ArrayList<HashMap<String, Object>> result = userDaoImpl.query(jdbc, sql);
        return result;
    }
	
	@Cacheable(value = "user")
	public ArrayList<HashMap<String, Object>> queryNUser(NUser nuser) throws Exception{
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		String sql = "select U.id, U.username, U.`password`, U.stateflag,D.dept_id AS deptid from kaliey.n_user as U left join kaliey.n_user_dept as D on U.id = D.user_id where U.stateflag = 0 and U.username = '"+nuser.getUsername().trim()+"'";
		JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
		ArrayList<HashMap<String, Object>> result = userDaoImpl.query(jdbc, sql);
		return result;
	}
	
	
	public boolean getUser(NUser user) throws Exception{
    	/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		String sql = "SELECT userName FROM AdminList where userName='"+user.getUsername()+"'";
		JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
		ArrayList<HashMap<String, Object>> result = userDaoImpl.query(jdbc, sql);
        if(result!=null && result.size()!=0){
            return true;
        }
        return false;
	}
	public boolean getAuthProxy(NUser user) {
    	PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL("http://135.242.16.160:8990/auth.jsp");
            // 锟津开猴拷URL之锟斤拷锟斤拷锟斤拷锟�
            URLConnection conn = realUrl.openConnection();
            // 锟斤拷锟斤拷通锟矫碉拷锟斤拷锟斤拷锟斤拷锟斤拷
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 锟斤拷锟斤拷POST锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 锟斤拷取URLConnection锟斤拷锟斤拷锟接︼拷锟斤拷锟斤拷锟斤拷
            out = new PrintWriter(conn.getOutputStream());
            // 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�
            //out.print("userid=yefeng&password=123456&version=" + version + "&data={\"DELETE\":[],\"INSERT\":[],\"UPDATE\":[]}");
            
            //String encryptResult = DeEncode.getBase64(Passwd);
            //String PassTemp = DeEncode.parseByte2HexStr(encryptResult);
            
            String encryptResult = DeEncode.getBase64(user.getPassword());
            String PassTemp = DeEncode.parseByte2HexStr(encryptResult);
            //out.print("user=lkhuang" + "&password=" + java.net.URLEncoder.encode(PassTemp,"UTF-8"));
            String reqStr = "user=" + java.net.URLEncoder.encode(user.getUsername(),"UTF-8") + "&password="+ java.net.URLEncoder.encode(PassTemp,"UTF-8");
            out.print(reqStr);
            // flush锟斤拷锟斤拷锟斤拷幕锟斤拷锟�
            out.flush();
            
            // 锟斤拷锟斤拷BufferedReader锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷取URL锟斤拷锟斤拷应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            
            if(result.equals("login_true"))
            {
                return true;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Request Auth: " + e.toString(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        //使锟斤拷finally锟斤拷锟斤拷锟截憋拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
            }
        }
        return false;
    }

	public boolean authUser(NUser user) throws Exception{
	    String ldapProxy = System.getenv("LDAP_PROXY");
	    if("Y".equalsIgnoreCase(ldapProxy))
	    {
	        return getAuthProxy(user);
	    }
	    Ldap ldapAuth = new Ldap();
        if(ldapAuth.getAuth(user.getUsername(),user.getPassword()).equals(Constant.AUTHSUCCESS))
        {
            return true;
        }
        return false;
        
	}
	public boolean authAdministrator(NUser user){
		if(user.getUsername().equals("root") && MD5Util.md5(user.getPassword()).equals("0ab9965a1da1500c7a293652ba814c57")){
		//if(user.getUsername().equals("root") && user.getPassword().equals("26fa931348833ba971aa0d772c0fb24567192f1ecad3e4dd3d7013d8d5e5aef3")){
			return true;
		}
		/*if(user.getUsername().equals("tom") && MD5Util.md5(user.getPassword()).equals("0ab9965a1da1500c7a293652ba814c57")){
			//if(user.getUsername().equals("root") && user.getPassword().equals("0ab9965a1da1500c7a293652ba814c57")){
			return true;
		}*/
		return false;
	}
	
	public UserDaoImpl getUserDaoImpl() {
		return userDaoImpl;
	}
	
	public void setUserDaoImpl(UserDaoImpl userDaoImpl) {
		this.userDaoImpl = userDaoImpl;
	}
	@Cacheable(value = "deptIds")
	public ArrayList<HashMap<String, Object>> getDeptIdsByUserName(String username) throws Exception {
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		String sql = "select distinct dept_id from kaliey.n_user_dept "
				+ "where stateflag=0 "
				+ "and user_id in (select id from kaliey.n_user where stateflag=0 and username='"+username+"')";
		JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
		ArrayList<HashMap<String, Object>> result = userDaoImpl.query(jdbc, sql);
		return result;
	}
	@Cacheable(value = "depts")
	public ArrayList<HashMap<String, Object>> getDeptsByUserName(String username) throws Exception {
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		String sql = "select a.dept_id,b.dept_name from kaliey.n_user_dept a " + 
				"left join kaliey.n_dept b on a.dept_id=b.id " + 
				"where a.stateflag=0 " + 
				"and a.user_id in (select id from kaliey.n_user where stateflag=0 and username='"+username+"')";
		JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
		ArrayList<HashMap<String, Object>> result = userDaoImpl.query(jdbc, sql);
		return result;
	}
	@Cacheable(value = "deptAdm")
	public ArrayList<HashMap<String, Object>> getDeptsByAdmin() throws Exception {
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		String sql = "select id dept_id,dept_name from kaliey.n_dept where stateflag=0";
		JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
		ArrayList<HashMap<String, Object>> result = userDaoImpl.query(jdbc, sql);
		return result;
	}
/*	public ArrayList<HashMap<String, Object>> getDeptByUserName(String username) throws Exception {
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String sql = "select * from kaliey.n_user a left join kaliey.n_dept b on a.deptid=b.id and b.stateflag=0 where a.stateflag=0 and a.username='"+username+"'";
		ArrayList<HashMap<String, Object>> result = userDaoImpl.query(jdbc, sql);
		return result;
	}
*/
	@Cacheable(value = "dept")
	public ArrayList<HashMap<String, Object>> getDeptById(String deptid) throws Exception {
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		String sql = "select * from kaliey.n_dept where id='"+deptid+"'";
		JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
		ArrayList<HashMap<String, Object>> result = userDaoImpl.query(jdbc, sql);
		return result;
	}

	public static void main(String[] args) throws Exception {
		LoginService loginService = new LoginService();
		ArrayList<HashMap<String, Object>> deptsByUserName = loginService.getDeptsByUserName("root");
	}
	
	
}
