package com.alucn.weblab.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.weblab.dao.impl.UserDaoImpl;

@Service("userService")
public class UserService {
	
	@Autowired(required=true)
	private UserDaoImpl userDaoImpl;
	
	public ArrayList<HashMap<String, Object>> getAllUserInfoJson(String limit,String offset,String username) throws Exception {
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String sql = "select * from n_user a "
				+ "left join n_dept b on a.deptid=b.id and b.stateflag=0 "
				+"left join ( " + 
						"select user_id,group_concat(distinct b.role_name) roles from n_user_role a " + 
						"left join n_role b on a.role_id=b.id and b.stateflag=0 " + 
						"where a.stateflag=0 " + 
						"group by a.user_id " + 
				") c on a.id=c.user_id "
				+ "where 1=1 ";
		if(username!=null && !"".equals(username)) {
			sql=sql+"and a.username like '%"+username+"%' ";
		}
		sql=sql+"limit "+offset+","+limit;
		System.err.println("UserService >> getAllUserInfoJson >> sql "+sql);
		ArrayList<HashMap<String, Object>> query = userDaoImpl.query(jdbc, sql);
		return query;
	}
	public int getAllUserInfoJsonCount(String username) throws Exception {
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String sql = "select count(*) co from n_user where 1=1 ";
		if(username!=null && !"".equals(username)) {
			sql=sql+"and username like '%"+username+"%' ";
		}
		ArrayList<HashMap<String, Object>> query = userDaoImpl.query(jdbc, sql);
		int co =0;
		if(query.size()>0) {
			co = Integer.parseInt((String) query.get(0).get("co"));
		}
		return co;
	}
	
	public UserDaoImpl getUserDaoImpl() {
		return userDaoImpl;
	}
	
	public void setUserDaoImpl(UserDaoImpl userDaoImpl) {
		this.userDaoImpl = userDaoImpl;
	}
	public ArrayList<HashMap<String, Object>> getAllRoles() throws Exception {
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String sql = "select * from n_role where stateflag=0 ";
		ArrayList<HashMap<String, Object>> query = userDaoImpl.query(jdbc, sql);
		return query;
	}
	public ArrayList<HashMap<String, Object>> getAllDept() throws Exception {
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String sql = "select * from n_dept where stateflag=0 ";
		ArrayList<HashMap<String, Object>> query = userDaoImpl.query(jdbc, sql);
		return query;
	}
	public ArrayList<HashMap<String, Object>> getRolePermission(String rolename) throws Exception {
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String sql = "select a.id,a.role_name,c.permission_name from n_role a " + 
				"left join n_role_permission b on a.id=b.role_id and b.stateflag=0 " + 
				"left join n_permission c on c.id=b.permission_id and c.stateflag=0 " + 
				"where 1=1 " + 
				"and a.stateflag=0 " + 
				"and a.role_name='"+rolename+"'";
		ArrayList<HashMap<String, Object>> query = userDaoImpl.query(jdbc, sql);
		return query;
	}

	
	
	
}
