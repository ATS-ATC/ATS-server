package com.alucn.weblab.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alucn.casemanager.server.common.LdapAuthentication;
import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.weblab.dao.impl.UserDaoImpl;
import com.alucn.weblab.model.NUser;
import com.alucn.weblab.utils.MD5Util;

@Service("loginService")
public class LoginService {
	
	@Autowired(required=true)
	private UserDaoImpl userDaoImpl;
	
	public ArrayList<HashMap<String, Object>> findUrlPermissions() throws Exception {
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String sql = "select * from n_permission_url where stateflag=0 and type='menu'";
		ArrayList<HashMap<String, Object>> query = userDaoImpl.query(jdbc, sql);
		return query;
	}
	public ArrayList<HashMap<String, Object>> getPermissionsByUserName(String username) throws Exception{
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String sql = "select distinct d.permission_name from n_user a "
				+ "left join n_user_role b on a.id=b.user_id "
				+ "left join n_role_permission c on b.role_id=c.role_id "
				+ "left join n_permission d on d.id=c.permission_id "
				+ "where a.stateflag=0 "
				+ "and b.stateflag=0 "
				+ "and c.stateflag=0 "
				+ "and d.stateflag=0 "
				+ "and a.username='"+username.trim()+"'";
		//System.err.println("getPermissionsByUserName:"+sql);
		ArrayList<HashMap<String, Object>> query = userDaoImpl.query(jdbc, sql);
		return query;
	}
	
	public void createLdapUser(NUser nuser) throws Exception {
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String password = MD5Util.md5(nuser.getPassword());
		String sql = "insert into n_user (username,password,deptid) values ('"+nuser.getUsername()+"','"+password+"',"+1+")";
		userDaoImpl.insert(jdbc, sql);
		String qsql = "select id from n_user where stateflag=0 and username='"+nuser.getUsername()+"'";;
		ArrayList<HashMap<String, Object>> query = userDaoImpl.query(jdbc, qsql);
		int id = Integer.parseInt((String)query.get(0).get("id"));
		//默认创建的为普通用户  role_id=3
		String rsql = "insert into n_user_role (user_id,role_id) values ("+id+","+3+")";
		userDaoImpl.insert(jdbc, rsql);
	} 
	//不排除逻辑删除账号
	public ArrayList<HashMap<String, Object>> queryAllNUser(NUser nuser) throws Exception{
    	String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String sql = "select * from n_user where username='"+nuser.getUsername()+"'";
		ArrayList<HashMap<String, Object>> result = userDaoImpl.query(jdbc, sql);
        return result;
	}
	//只取状态正常的账号
	public ArrayList<HashMap<String, Object>> queryNUser(NUser nuser) throws Exception{
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String sql = "select * from n_user where stateflag=0 and username='"+nuser.getUsername()+"'";
		ArrayList<HashMap<String, Object>> result = userDaoImpl.query(jdbc, sql);
		return result;
	}
	
	
	
	public boolean getUser(NUser user) throws Exception{
    	String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String sql = "SELECT userName FROM AdminList where userName='"+user.getUsername()+"'";
		ArrayList<HashMap<String, Object>> result = userDaoImpl.query(jdbc, sql);
        if(result!=null && result.size()!=0){
            return true;
        }
        return false;
	}
	
	public boolean authUser(NUser user) throws Exception{
	    LdapAuthentication ldapAuth = new LdapAuthentication(user.getUsername(),user.getPassword());
        String authResult = ldapAuth.getAuth();
        if(authResult.equals(Constant.AUTHSUCCESS)){
            return true;
        }
        return false;
	}
	
	public boolean authAdministrator(NUser user){
		if(user.getUsername().equals("root") && MD5Util.md5(user.getPassword()).equals("0ab9965a1da1500c7a293652ba814c57")){
			return true;
		}
		return false;
	}
	
	public UserDaoImpl getUserDaoImpl() {
		return userDaoImpl;
	}
	
	public void setUserDaoImpl(UserDaoImpl userDaoImpl) {
		this.userDaoImpl = userDaoImpl;
	}

	
	
	
}
