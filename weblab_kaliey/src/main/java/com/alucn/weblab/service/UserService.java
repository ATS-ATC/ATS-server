package com.alucn.weblab.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alucn.weblab.dao.impl.UserDaoImpl;
import com.alucn.weblab.utils.JDBCHelper;

@Service("userService")
public class UserService {
	
	@Autowired(required=true)
	private UserDaoImpl userDaoImpl;
	
	public ArrayList<HashMap<String, Object>> getAllUserInfoJson(String limit,String offset,String username, String sort, String sortOrder) throws Exception {
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		/*String sql = "select * from kaliey.n_user a "
				+ "left join kaliey.n_dept b on a.deptid=b.id and b.stateflag=0 "
				+"left join ( " + 
						"select user_id,group_concat(distinct b.role_name) roles from kaliey.n_user_role a " + 
						"left join kaliey.n_role b on a.role_id=b.id and b.stateflag=0 " + 
						"where a.stateflag=0 " + 
						"group by a.user_id " + 
				") c on a.id=c.user_id "
				+ "where 1=1 ";*/
		String sql = "select * from kaliey.n_user a  " + 
				"left join ( " + 
				"select user_id,group_concat(distinct b.dept_name ) depts,group_concat(distinct b.id) deptids from ( " + 
				"select * from kaliey.n_user_dept order by dept_id " + 
				") a " + 
				"left join kaliey.n_dept b on a.dept_id=b.id and b.stateflag=0 " + 
				"where a.stateflag=0 " + 
				"group by a.user_id " + 
				") b on a.id=b.user_id " + 
				"left join ( " + 
				"select user_id,group_concat(distinct b.role_name) roles from kaliey.n_user_role a " + 
				"left join kaliey.n_role b on a.role_id=b.id and b.stateflag=0 " + 
				"where a.stateflag=0 " + 
				"group by a.user_id " + 
				") c on a.id=c.user_id " + 
				"where 1=1 and a.id!=1 ";
		if(username!=null && !"".equals(username)) {
			sql=sql+"and a.username like '%"+username+"%' ";
		}
		if(sort!=null && !"".equals(sort)) {
			sql=sql+" order by "+sort+" "+sortOrder;
		}else {
			sql=sql+" order by deptid desc";
		}
		sql=sql+" limit "+offset+","+limit;
		JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
		ArrayList<HashMap<String, Object>> query = userDaoImpl.query(jdbc, sql);
		return query;
	}
	public int getAllUserInfoJsonCount(String username) throws Exception {
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
	    JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
		String sql = "select count(*) co from kaliey.n_user where 1=1 and id!=1 ";
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
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
	    JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
		String sql = "select * from kaliey.n_role where stateflag=0 ";
		ArrayList<HashMap<String, Object>> query = userDaoImpl.query(jdbc, sql);
		return query;
	}
	public ArrayList<HashMap<String, Object>> getAllDept() throws Exception {
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
	    JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
		String sql = "select * from kaliey.n_dept where stateflag=0 ";
		ArrayList<HashMap<String, Object>> query = userDaoImpl.query(jdbc, sql);
		return query;
	}
	public ArrayList<HashMap<String, Object>> getRolePermission(String rolename) throws Exception {
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		String sql = "select a.id,a.role_name,c.permission_name from kaliey.n_role a " + 
				"left join kaliey.n_role_permission b on a.id=b.role_id and b.stateflag=0 " + 
				"left join kaliey.n_permission c on c.id=b.permission_id and c.stateflag=0 " + 
				"where 1=1 " + 
				"and a.stateflag=0 " + 
				"and a.role_name='"+rolename+"'";
		JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
		ArrayList<HashMap<String, Object>> query = userDaoImpl.query(jdbc, sql);
		return query;
	}

	public ArrayList<HashMap<String, Object>> getUserInfoById(String id) throws Exception {
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		/*String sql = "select a.id,a.username,a.deptid,a.stateflag,b.roles,c.dept_name from kaliey.n_user a  " + 
				"left join ( " + 
				"select user_id,group_concat(distinct b.role_name) roles from kaliey.n_user_role a  " + 
				"left join kaliey.n_role b on a.role_id=b.id and b.stateflag=0 " + 
				"where a.stateflag=0 " + 
				"group by a.user_id " + 
				") b on a.id=b.user_id " + 
				"left join kaliey.n_dept c on a.deptid=c.id and c.stateflag=0 "+
				"where 1=1 "+
				"and a.id='"+id+"'";*/
		String sql = "select a.id,a.username,a.deptid,a.stateflag,b.roles,c.depts,c.deptids from kaliey.n_user a " + 
				"left join ( " + 
				"select user_id,group_concat(distinct b.role_name) roles from kaliey.n_user_role a  " + 
				"left join kaliey.n_role b on a.role_id=b.id and b.stateflag=0 " + 
				"where a.stateflag=0 " + 
				"group by a.user_id " + 
				") b on a.id=b.user_id " + 
				"left join ( " + 
				"select user_id,group_concat(distinct b.dept_name ) depts,group_concat(distinct b.id) deptids from ( " + 
				"select * from kaliey.n_user_dept order by dept_id " + 
				") a " + 
				"left join kaliey.n_dept b on a.dept_id=b.id and b.stateflag=0 " + 
				"where a.stateflag=0 " + 
				"group by a.user_id " + 
				") c on a.id=c.user_id " + 
				"where 1=1 "+
				"and a.id='"+id+"'";
		JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
		ArrayList<HashMap<String, Object>> query = userDaoImpl.query(jdbc, sql);
		return query;
	}
	public String editUserInfo(String id, String erole, String edept, String estateflag) {
		
		/*
		 * System.out.println("editUserInfo >> id >> "+id);
		 * System.out.println("editUserInfo >> erole >> "+erole);
		 * System.out.println("editUserInfo >> edept >> "+edept);
		 * System.out.println("editUserInfo >> estateflag >> "+estateflag);
		 */
		
		
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc;
		try {
			jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}*/
		//第一步：通过id获取账户信息
	    JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
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
				sql="update kaliey.n_user set stateflag="+stateflag+" where id="+id;
				try {
					userDaoImpl.update(jdbc, sql);
				} catch (Exception e) {
					e.printStackTrace();
					return "fail";
				}
			}
			if(edept!=null&&!"".equals(edept)) {
				/*sql="update kaliey.n_user set deptid="+edept+" where id="+id;
				try {
					System.err.println("editUserInfo >> deptid >> "+sql);
					userDaoImpl.update(jdbc, sql);
				} catch (Exception e) {
					e.printStackTrace();
					return "fail";
				}*/
				String[] split = edept.split(",");
				List<String> edeptList = arrayToList(split);
				
				String edepts = (String) userInfoById.get(0).get("deptids");
				String[] split2 = edepts.split(",");
				List<String> edeptsList = arrayToList(split2);
				List<String> midList = arrayToList(edeptsList);
				
				
				//需要删除的部门（）
				edeptsList.removeAll(edeptList);
				
				if(edeptsList.size()>0) {
					String inString ="";
					for (int i =0 ;i<edeptsList.size();i++) {
						if(i==edeptsList.size()-1) {
							inString=inString+"'"+edeptsList.get(i)+"'";
						}else {
							inString=inString+"'"+edeptsList.get(i)+"',";
						}
					}
					//delete from kaliey.n_user_role where user_id='1' and role_id in(select id from kaliey.n_role where role_name in("super","user"));
					sql="delete from kaliey.n_user_dept where user_id='"+id+"' and dept_id in(select id from kaliey.n_dept where id in("+inString+"))";
					try {
						userDaoImpl.delete(jdbc, sql);
					} catch (Exception e) {
						e.printStackTrace();
						return "fail";
					}
				}
				
				//需要新加的部门
				edeptList.removeAll(midList);
				
				if(edeptList.size()>0) {
					String inString ="";
					for (int i =0 ;i<edeptList.size();i++) {
						if(i==edeptList.size()-1) {
							inString=inString+"'"+edeptList.get(i)+"'";
						}else {
							inString=inString+"'"+edeptList.get(i)+"',";
						}
					}
					sql="insert into kaliey.n_user_dept (user_id,dept_id) " + 
							"select "+id+",id from kaliey.n_dept " + 
							"where id in("+inString+") " + 
							"and id not in ( " + 
							"select dept_id from kaliey.n_user_dept where user_id="+id+" and stateflag=0 " + 
							")";
					try {
						userDaoImpl.insert(jdbc, sql);
					} catch (Exception e) {
						e.printStackTrace();
						return "fail";
					}
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
				rolesList.removeAll(eroleList);
				if(rolesList.size()>0) {
					String inString ="";
					for (int i =0 ;i<rolesList.size();i++) {
						if(i==rolesList.size()-1) {
							inString=inString+"'"+rolesList.get(i)+"'";
						}else {
							inString=inString+"'"+rolesList.get(i)+"',";
						}
					}
					//delete from kaliey.n_user_role where user_id='1' and role_id in(select id from kaliey.n_role where role_name in("super","user"));
					sql="delete from kaliey.n_user_role where user_id='"+id+"' and role_id in(select id from kaliey.n_role where role_name in("+inString+"))";
					try {
						userDaoImpl.delete(jdbc, sql);
					} catch (Exception e) {
						e.printStackTrace();
						return "fail";
					}
				}
				
				//需要新加的角色
				eroleList.removeAll(midList);
				
				if(eroleList.size()>0) {
					String inString ="";
					for (int i =0 ;i<eroleList.size();i++) {
						if(i==eroleList.size()-1) {
							inString=inString+"'"+eroleList.get(i)+"'";
						}else {
							inString=inString+"'"+eroleList.get(i)+"',";
						}
					}
					sql="insert into kaliey.n_user_role (user_id,role_id) " + 
							"select "+id+",id from kaliey.n_role " + 
							"where role_name in("+inString+") " + 
							"and id not in ( " + 
							"select role_id from kaliey.n_user_role where user_id="+id+" and stateflag=0 " + 
							")";
					try {
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
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		String sql = "select a.id,a.dept_name,a.remark,a.stateflag,ifnull(b.ccount,\"0\") ccount from kaliey.n_dept a "
				+"left join(select dept_id,count(1) ccount from kaliey.n_user_dept group by dept_id) b on a.id=b.dept_id "
				+ "where 1=1 ";
		if(deptname!=null && !"".equals(deptname)) {
			sql=sql+"and a.dept_name like '%"+deptname+"%' ";
		}
		sql=sql+"limit "+offset+","+limit;
		JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
		ArrayList<HashMap<String, Object>> query = userDaoImpl.query(jdbc, sql);
		return query;
	}
	public int getAllDeptInfoJsonCount(String deptname) throws Exception {
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		String sql = "select count(1) ccount from kaliey.n_dept "
				+ "where 1=1 ";
		if(deptname!=null && !"".equals(deptname)) {
			sql=sql+"and dept_name like '%"+deptname+"%' ";
		}
		JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
		ArrayList<HashMap<String, Object>> query = userDaoImpl.query(jdbc, sql);
		if(query.size()>0) {
			return Integer.parseInt((String)query.get(0).get("ccount"));
		}else {
			return 0; 
		}
	}
	public void insertDeptInfo(String adeptname, String aremark) throws Exception {
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		String sql ="insert into kaliey.n_dept(dept_name,remark) values('"+adeptname+"','"+aremark+"')";
		JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
		userDaoImpl.insert(jdbc, sql);
	}
	public void deleteDeptInfo(String id) throws Exception {
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
	    JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
		String sql ="delete from kaliey.n_dept where id="+id;
		userDaoImpl.delete(jdbc, sql);
	}
	public ArrayList<HashMap<String, Object>> checkOnlineUser(String id) throws Exception {
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		String sql = "select a.id,ifnull(b.ccount,\"0\") ccount from kaliey.n_dept a "
				+"left join(select dept_id,count(1) ccount from kaliey.n_user_dept group by dept_id) b on a.id=b.dept_id "
				+ "where 1=1 ";
		if(id!=null && !"".equals(id)) {
			sql=sql+"and a.id="+id+" ";
		}
		JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
		ArrayList<HashMap<String, Object>> query = userDaoImpl.query(jdbc, sql);
		return query;
	}
	public Map<String,Object> editDeptInfo(String id, String edeptname, String eremark, String estateflag) throws Exception {
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		
		
		String sql = "select a.id,a.dept_name,a.remark,a.stateflag,ifnull(b.ccount,\"0\") ccount from kaliey.n_dept a " + 
				"left join(select dept_id,count(1) ccount from kaliey.n_user_dept group by dept_id) b on a.id=b.dept_id " + 
				"where 1=1 and a.id="+id;
		JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
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
				sql="select id from kaliey.n_dept where stateflag=0 and dept_name='"+query.get(0).get("dept_name")+"'";
				ArrayList<HashMap<String,Object>> query2 = userDaoImpl.query(jdbc, sql);
				if(query2.size()>0) {
					resultMap.put("result", "fail");
					resultMap.put("msg", "The department name already exists..");
					return resultMap;
				}
			}else if (estateflag.equals("disable")) {
				stateflag=""+System.currentTimeMillis();
			}
			sql="update kaliey.n_dept set stateflag='"+stateflag+"' where id="+id;
			try {
				userDaoImpl.update(jdbc, sql);
			} catch (Exception e) {
				e.printStackTrace();
				resultMap.put("result", "fail");
				resultMap.put("msg", "Exception.");
				return resultMap;
			}
		}
		if(edeptname!=null&&!"".equals(edeptname)) {
			
			sql="select id from kaliey.n_dept where stateflag=0 and dept_name='"+edeptname+"'";
			ArrayList<HashMap<String, Object>> query2 = userDaoImpl.query(jdbc, sql);
			if(query2.size()>0) {
				resultMap.put("result", "fail");
				resultMap.put("msg", "The department name already exists.");
				return resultMap;
			}
			sql="update kaliey.n_dept set dept_name='"+edeptname+"' where id="+id;
			try {
				userDaoImpl.update(jdbc, sql);
			} catch (Exception e) {
				e.printStackTrace();
				resultMap.put("result", "fail");
				resultMap.put("msg", "Exception.");
				return resultMap;
			}
		}
		if(eremark!=null&&!"".equals(eremark)) {
			sql="update kaliey.n_dept set remark='"+eremark+"' where id="+id;
			try {
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
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		String sql = "select * from kaliey.n_role a " 
				+ "left join (select role_id,count(1) ccount from kaliey.n_user_role group by role_id) b on a.id=b.role_id  "
				+ "where 1=1 ";
		if(rolename!=null && !"".equals(rolename)) {
			sql=sql+"and a.role_name like '%"+rolename+"%' ";
		}
		sql=sql+"limit "+offset+","+limit;
		JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
		ArrayList<HashMap<String, Object>> query = userDaoImpl.query(jdbc, sql);
		return query;
	}
	public int getAllRoleInfoJsonCount(String rolename) throws Exception {
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		String sql = "select count(1) ccount from kaliey.n_role "
				+ "where 1=1 ";
		if(rolename!=null && !"".equals(rolename)) {
			sql=sql+"and role_name like '%"+rolename+"%' ";
		}
		JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
		ArrayList<HashMap<String, Object>> query = userDaoImpl.query(jdbc, sql);
		if(query.size()>0) {
			return Integer.parseInt((String)query.get(0).get("ccount"));
		}else {
			return 0; 
		}
	}
	public String deleteUser(String id) {
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = null;
		try {
			jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		} catch (Exception e1) {
			e1.printStackTrace();
		}*/
	    JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
		String sql = "delete from kaliey.n_user where id="+id;
		try {
			userDaoImpl.delete(jdbc, sql);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}
}
