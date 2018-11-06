package com.alucn.weblab.service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.taskdefs.Delete;
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

	public ArrayList<HashMap<String, Object>> getUserInfoById(String id) throws Exception {
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String sql = "select a.id,a.username,a.deptid,a.stateflag,b.roles,c.dept_name from n_user a  " + 
				"left join ( " + 
				"select user_id,group_concat(distinct b.role_name) roles from n_user_role a  " + 
				"left join n_role b on a.role_id=b.id and b.stateflag=0 " + 
				"where a.stateflag=0 " + 
				"group by a.user_id " + 
				") b on a.id=b.user_id " + 
				"left join n_dept c on a.deptid=c.id and c.stateflag=0 "+
				"where 1=1 "+
				"and a.id='"+id+"'";
		ArrayList<HashMap<String, Object>> query = userDaoImpl.query(jdbc, sql);
		return query;
	}
	public String editUserInfo(String id, String erole, String edept, String estateflag) {
		
		System.out.println("editUserInfo >> id >> "+id);
		System.out.println("editUserInfo >> erole >> "+erole);
		System.out.println("editUserInfo >> edept >> "+edept);
		System.out.println("editUserInfo >> estateflag >> "+estateflag);
		
		
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc;
		try {
			jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
		//第一步：通过id获取账户信息
		ArrayList<HashMap<String, Object>> userInfoById = new ArrayList<HashMap<String, Object>>();
		try {
			userInfoById = getUserInfoById(id);
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
		//editUserInfo >> userInfoById >> [{roles=super, deptid=2, stateflag=0, dept_name=headquarters, id=2, username=lkhuang}]
		//System.out.println("editUserInfo >> userInfoById >> "+userInfoById);
		if(userInfoById.size()==1) {
			String sql ="";
			//直接修改部门和stateflag
			if(estateflag!=null&&!"".equals(estateflag)) {
				String stateflag="";
				if(estateflag.equals("normal")) {
					stateflag="0";
				}else if (estateflag.equals("disable")) {
					stateflag=""+System.currentTimeMillis();
				}
				sql="update n_user set stateflag="+stateflag+" where id="+id;
				try {
					System.err.println("editUserInfo >> stateflag >> "+sql);
					userDaoImpl.update(jdbc, sql);
				} catch (Exception e) {
					e.printStackTrace();
					return "fail";
				}
			}
			if(edept!=null&&!"".equals(edept)) {
				sql="update n_user set deptid="+edept+" where id="+id;
				try {
					System.err.println("editUserInfo >> deptid >> "+sql);
					userDaoImpl.update(jdbc, sql);
				} catch (Exception e) {
					e.printStackTrace();
					return "fail";
				}
			}
			//第二步：对比旧有信息与前端传来的信息差异
			if(erole!=null&&!"".equals(erole)) {
				String[] split = erole.split(",");
				List<String> eroleList = arrayToList(split);
				
				String roles = (String) userInfoById.get(0).get("roles");
				String[] split2 = roles.split(",");
				List<String> rolesList = arrayToList(split2);
				//未完待续>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.
				//第三步：通过对比得来的差异进行更新
				List<String> midList = arrayToList(rolesList);
				
				//需要删除的角色（）
				System.out.println("editUserInfo >> removeAll before >> rolesList "+rolesList);
				rolesList.removeAll(eroleList);
				System.out.println("editUserInfo >> removeAll before >>  rolesList "+rolesList);
				if(rolesList.size()>0) {
					String inString ="";
					for (int i =0 ;i<rolesList.size();i++) {
						if(i==rolesList.size()-1) {
							inString=inString+"'"+rolesList.get(i)+"'";
						}else {
							inString=inString+"'"+rolesList.get(i)+"',";
						}
					}
					//delete from n_user_role where user_id='1' and role_id in(select id from n_role where role_name in("super","user"));
					sql="delete from n_user_role where user_id='"+id+"' and role_id in(select id from n_role where role_name in("+inString+"))";
					try {
						System.err.println("editUserInfo >> delete >> "+sql);
						userDaoImpl.delete(jdbc, sql);
					} catch (Exception e) {
						e.printStackTrace();
						return "fail";
					}
				}
				
				//需要新加的角色
				System.out.println("editUserInfo >> removeAll before >> eroleList "+eroleList);
				eroleList.removeAll(midList);
				System.out.println("editUserInfo >> removeAll after >> eroleList "+eroleList);
				
				if(eroleList.size()>0) {
					String inString ="";
					for (int i =0 ;i<eroleList.size();i++) {
						if(i==eroleList.size()-1) {
							inString=inString+"'"+eroleList.get(i)+"'";
						}else {
							inString=inString+"'"+eroleList.get(i)+"',";
						}
					}
					sql="insert into n_user_role (user_id,role_id) " + 
							"select "+id+",id from n_role " + 
							"where role_name in("+inString+") " + 
							"and id not in ( " + 
							"select role_id from n_user_role where user_id="+id+" and stateflag=0 " + 
							")";
					try {
						System.err.println("editUserInfo >> insert >> "+sql);
						userDaoImpl.insert(jdbc, sql);
					} catch (Exception e) {
						e.printStackTrace();
						return "fail";
					}
				}
			}
			
		}
		
		return "success";
	}
	public List<String> arrayToList(String[] array){
		List<String> list = new ArrayList<String>();
		for (String str : array) {
			list.add(str);
		}
		return list;
	}
	public List<String> arrayToList(List<String> array){
		List<String> list = new ArrayList<String>();
		for (String str : array) {
			list.add(str);
		}
		return list;
	}
	public ArrayList<HashMap<String, Object>> getAllDeptInfoJson(String limit, String offset, String deptname) throws Exception {
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String sql = "select a.id,a.dept_name,a.remark,a.stateflag,ifnull(b.ccount,\"0\") ccount from n_dept a "
				+"left join(select deptid,count(1) ccount from n_user group by deptid) b on a.id=b.deptid "
				+ "where 1=1 ";
		if(deptname!=null && !"".equals(deptname)) {
			sql=sql+"and a.dept_name like '%"+deptname+"%' ";
		}
		sql=sql+"limit "+offset+","+limit;
		System.err.println("UserService >> getAllDeptInfoJson >> sql "+sql);
		ArrayList<HashMap<String, Object>> query = userDaoImpl.query(jdbc, sql);
		return query;
	}
	public int getAllDeptInfoJsonCount(String deptname) throws Exception {
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String sql = "select count(1) ccount from n_dept "
				+ "where 1=1 ";
		if(deptname!=null && !"".equals(deptname)) {
			sql=sql+"and dept_name like '%"+deptname+"%' ";
		}
		System.err.println("UserService >> getAllDeptInfoJsonCount >> sql "+sql);
		ArrayList<HashMap<String, Object>> query = userDaoImpl.query(jdbc, sql);
		if(query.size()>0) {
			return Integer.parseInt((String)query.get(0).get("ccount"));
		}else {
			return 0; 
		}
	}
	public void insertDeptInfo(String adeptname, String aremark) throws Exception {
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String sql ="insert into n_dept(dept_name,remark) values('"+adeptname+"','"+aremark+"')";
		System.err.println("UserService >> insertDeptInfo >> sql "+sql);
		userDaoImpl.insert(jdbc, sql);
	}
	public void deleteDeptInfo(String id) throws Exception {
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String sql ="delete from n_dept where id="+id;
		System.err.println("UserService >> deleteDeptInfo >> sql "+sql);
		userDaoImpl.delete(jdbc, sql);
	}
	public ArrayList<HashMap<String, Object>> checkOnlineUser(String id) throws Exception {
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String sql = "select a.id,ifnull(b.ccount,\"0\") ccount from n_dept a "
				+"left join(select deptid,count(1) ccount from n_user group by deptid) b on a.id=b.deptid "
				+ "where 1=1 ";
		if(id!=null && !"".equals(id)) {
			sql=sql+"and a.id="+id+" ";
		}
		System.err.println("UserService >> checkOnlineUser >> sql "+sql);
		ArrayList<HashMap<String, Object>> query = userDaoImpl.query(jdbc, sql);
		return query;
	}
	public Map<String,Object> editDeptInfo(String id, String edeptname, String eremark, String estateflag) throws Exception {
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		
		
		String sql = "select a.id,a.dept_name,a.remark,a.stateflag,ifnull(b.ccount,\"0\") ccount from n_dept a " + 
				"left join(select deptid,count(1) ccount from n_user group by deptid) b on a.id=b.deptid " + 
				"where 1=1 and a.id="+id;
		ArrayList<HashMap<String, Object>> query = userDaoImpl.query(jdbc, sql);
		if(query.size()==0) {
			resultMap.put("result", "fail");
			resultMap.put("msg", "cannot find dept entity by this id : "+id);
			return resultMap;
		}
		if(estateflag!=null&&!"".equals(estateflag)) {
			String scount = (String) query.get(0).get("ccount");
			int ccount = Integer.parseInt(scount);
			if(ccount>0) {
				resultMap.put("result", "fail");
				resultMap.put("msg", "There are online users who cannot change the status.");
				return resultMap;
			}
			String stateflag="";
			if(estateflag.equals("normal")) {
				stateflag="0";
				sql="select id from n_dept where stateflag=0 and dept_name='"+query.get(0).get("dept_name")+"'";
				ArrayList<HashMap<String,Object>> query2 = userDaoImpl.query(jdbc, sql);
				if(query2.size()>0) {
					resultMap.put("result", "fail");
					resultMap.put("msg", "The department name already exists..");
					return resultMap;
				}
			}else if (estateflag.equals("disable")) {
				stateflag=""+System.currentTimeMillis();
			}
			sql="update n_dept set stateflag='"+stateflag+"' where id="+id;
			try {
				System.err.println("editUserInfo >> stateflag >> "+sql);
				userDaoImpl.update(jdbc, sql);
			} catch (Exception e) {
				e.printStackTrace();
				resultMap.put("result", "fail");
				resultMap.put("msg", "Exception.");
				return resultMap;
			}
		}
		if(edeptname!=null&&!"".equals(edeptname)) {
			
			sql="select id from n_dept where stateflag=0 and dept_name='"+edeptname+"'";
			ArrayList<HashMap<String, Object>> query2 = userDaoImpl.query(jdbc, sql);
			if(query2.size()>0) {
				resultMap.put("result", "fail");
				resultMap.put("msg", "The department name already exists.");
				return resultMap;
			}
			sql="update n_dept set dept_name='"+edeptname+"' where id="+id;
			try {
				System.err.println("editUserInfo >> edeptname >> "+sql);
				userDaoImpl.update(jdbc, sql);
			} catch (Exception e) {
				e.printStackTrace();
				resultMap.put("result", "fail");
				resultMap.put("msg", "Exception.");
				return resultMap;
			}
		}
		if(eremark!=null&&!"".equals(eremark)) {
			sql="update n_dept set remark='"+eremark+"' where id="+id;
			try {
				System.err.println("editUserInfo >> eremark >> "+sql);
				userDaoImpl.update(jdbc, sql);
			} catch (Exception e) {
				e.printStackTrace();
				resultMap.put("result", "fail");
				resultMap.put("msg", "Exception.");
				return resultMap;
			}
		}
		resultMap.put("result", "success");
		return resultMap;
	}
	public ArrayList<HashMap<String, Object>> getAllRoleInfoJson(String limit, String offset, String rolename) throws Exception {
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String sql = "select * from n_role a " 
				+ "left join (select role_id,count(1) ccount from n_user_role group by role_id) b on a.id=b.role_id  "
				+ "where 1=1 ";
		if(rolename!=null && !"".equals(rolename)) {
			sql=sql+"and a.role_name like '%"+rolename+"%' ";
		}
		sql=sql+"limit "+offset+","+limit;
		System.err.println("UserService >> getAllRoleInfoJson >> sql "+sql);
		ArrayList<HashMap<String, Object>> query = userDaoImpl.query(jdbc, sql);
		return query;
	}
	public int getAllRoleInfoJsonCount(String rolename) throws Exception {
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String sql = "select count(1) ccount from n_role "
				+ "where 1=1 ";
		if(rolename!=null && !"".equals(rolename)) {
			sql=sql+"and role_name like '%"+rolename+"%' ";
		}
		System.err.println("UserService >> getAllRoleInfoJsonCount >> sql "+sql);
		ArrayList<HashMap<String, Object>> query = userDaoImpl.query(jdbc, sql);
		if(query.size()>0) {
			return Integer.parseInt((String)query.get(0).get("ccount"));
		}else {
			return 0; 
		}
	}
	public String deleteUser(String id) {
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = null;
		try {
			jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String sql = "delete from n_user where id="+id;
		System.err.println("UserService >> getAllRoleInfoJsonCount >> sql "+sql);
		try {
			userDaoImpl.delete(jdbc, sql);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}
}
