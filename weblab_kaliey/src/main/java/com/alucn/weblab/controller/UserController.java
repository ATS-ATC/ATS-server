package com.alucn.weblab.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alucn.weblab.service.UserService;
import com.sun.jna.platform.unix.X11.XClientMessageEvent.Data;

@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(path = "/getUserInfo")
    public String getUserInfo()  {
        return "userInfo";
    }
	@RequestMapping(path = "/getDeptInfo")
	public String getDeptInfo()  {
		return "deptInfo";
	}
	@RequestMapping(path = "/getRolesInfo")
	public String getRolesInfo()  {
		return "rolesInfo";
	}
	@RequestMapping(path = "/getRoleInfoJson")
	@ResponseBody
	public Map<String,Object> getRoleInfoJson(HttpServletRequest request,Model model) throws Exception {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		
		String limit = request.getParameter("limit")==null?"":request.getParameter("limit").toString().trim();
		String offset = request.getParameter("offset")==null?"":request.getParameter("offset").toString().trim();
		String rolename = request.getParameter("rolename")==null?"":request.getParameter("rolename").toString().trim();
		
		ArrayList<HashMap<String, Object>> allUserInfoJson = userService.getAllRoleInfoJson(limit,offset,rolename);
		int allUserInfoJsonCount = userService.getAllRoleInfoJsonCount(rolename);
		
		resultMap.put("rows", allUserInfoJson);
		resultMap.put("total", allUserInfoJsonCount);
		
		return resultMap;
		
	}
	
	
	
	@RequestMapping(path = "/deleteDeptInfo")
	@ResponseBody
	public Map<String,Object> deleteDeptInfo(HttpServletRequest request,Model model) throws Exception {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		
		String id = request.getParameter("id")==null?"":request.getParameter("id").toString().trim();
		
		if(id==null||"".equals(id)) {
			resultMap.put("result", "fail");
			resultMap.put("msg", "The id cannot be empty.");
			return resultMap;
		}
		try {
			
			ArrayList<HashMap<String, Object>> checkOnlineUser = userService.checkOnlineUser(id);
			if(checkOnlineUser.size()>0) {
				String scount = (String) checkOnlineUser.get(0).get("ccount");
				int ccount = Integer.parseInt(scount);
				if(ccount>0) {
					resultMap.put("result", "fail");
					resultMap.put("msg", "There are "+ccount+" online users that cannot be deleted");
					return resultMap;
				}
			}
			
			userService.deleteDeptInfo(id);
			resultMap.put("result", "success");
		}catch (Exception e) {
			e.printStackTrace();
			resultMap.put("result", "fail");
			resultMap.put("msg", "Exception.");
			return resultMap;
		}
		return resultMap;
	}
	@RequestMapping(path = "/addDeptInfo")
	@ResponseBody
	public Map<String,Object> addDeptInfo(HttpServletRequest request,Model model) throws Exception {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		
		String adeptname = request.getParameter("adeptname")==null?"":request.getParameter("adeptname").toString().trim();
		String aremark = request.getParameter("aremark")==null?"":request.getParameter("aremark").toString().trim();
		
		if(adeptname==null||"".equals(adeptname)) {
			resultMap.put("result", "fail");
			resultMap.put("msg", "The deptname cannot be empty.");
			return resultMap;
		}
		if(aremark==null||"".equals(aremark)) {
			resultMap.put("result", "fail");
			resultMap.put("msg", "The aremark cannot be empty.");
			return resultMap;
		}
		
		try {
			int allDeptInfoJsonCount = userService.getAllDeptInfoJsonCount(adeptname);
			if(allDeptInfoJsonCount>0) {
				resultMap.put("result", "fail");
				resultMap.put("msg", "The deptname is occupied.");
				return resultMap;
			}
			
			userService.insertDeptInfo(adeptname,aremark);
			resultMap.put("result", "success");
		}catch (Exception e) {
			e.printStackTrace();
			resultMap.put("result", "fail");
			resultMap.put("msg", "Exception.");
			return resultMap;
		}
		
		return resultMap;
		
	}
	@RequestMapping(path = "/getDeptInfoJson")
	@ResponseBody
	public Map<String,Object> getDeptInfoJson(HttpServletRequest request,Model model) throws Exception {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		
		String limit = request.getParameter("limit")==null?"":request.getParameter("limit").toString().trim();
		String offset = request.getParameter("offset")==null?"":request.getParameter("offset").toString().trim();
		String deptname = request.getParameter("deptname")==null?"":request.getParameter("deptname").toString().trim();
		
		ArrayList<HashMap<String, Object>> allUserInfoJson = userService.getAllDeptInfoJson(limit,offset,deptname);
		int allUserInfoJsonCount = userService.getAllDeptInfoJsonCount(deptname);
		
		resultMap.put("rows", allUserInfoJson);
		resultMap.put("total", allUserInfoJsonCount);
		
		return resultMap;
		
	}
	@RequestMapping(path = "/getUserInfoJson")
	@ResponseBody
	public Map<String,Object> getUserInfoJson(HttpServletRequest request,Model model) throws Exception {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		
		String limit = request.getParameter("limit")==null?"":request.getParameter("limit").toString().trim();
		String offset = request.getParameter("offset")==null?"":request.getParameter("offset").toString().trim();
		String username = request.getParameter("username")==null?"":request.getParameter("username").toString().trim();
		
		ArrayList<HashMap<String, Object>> allUserInfoJson = userService.getAllUserInfoJson(limit,offset,username);
		int allUserInfoJsonCount = userService.getAllUserInfoJsonCount(username);
		
		resultMap.put("rows", allUserInfoJson);
		resultMap.put("total", allUserInfoJsonCount);
		
		return resultMap;
		
	}
	@RequestMapping(path = "/getAllRoles")
	@ResponseBody
	public ArrayList<HashMap<String, Object>> getAllRoles(HttpServletRequest request,Model model) throws Exception {
		ArrayList<HashMap<String, Object>> allRoles = userService.getAllRoles();
		System.out.println("getAllRoles >> "+allRoles);
		return allRoles;
	}
	@RequestMapping(path = "/getAllDept")
	@ResponseBody
	public ArrayList<HashMap<String, Object>> getAllDept(HttpServletRequest request,Model model) throws Exception {
		ArrayList<HashMap<String, Object>> allDept = userService.getAllDept();
		System.out.println("getAllDept >> "+allDept);
		return allDept;
	}
	/*@RequestMapping(path = "/getRolePermission")
	@ResponseBody
	public ArrayList<HashMap<String, Object>> getRolePermission(HttpServletRequest request,Model model) throws Exception {
		String rolename = request.getParameter("rolename")==null?"":request.getParameter("rolename").toString().trim();
		
		ArrayList<HashMap<String, Object>> rolePermission = userService.getRolePermission(rolename);
		System.out.println("getRolePermission >> "+rolePermission);
		return rolePermission;
	}*/
	@RequestMapping(path = "/editDeptInfo")
	@ResponseBody
	public Map<String,Object>  editDeptInfo(HttpServletRequest request) throws Exception {
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		
		String id = request.getParameter("id")==null?"":request.getParameter("id").toString().trim();
		String eremark = request.getParameter("eremark")==null?"":request.getParameter("eremark").toString().trim();
		String edeptname = request.getParameter("edeptname")==null?"":request.getParameter("edeptname").toString().trim();
		String estateflag = request.getParameter("estateflag")==null?"":request.getParameter("estateflag").toString().trim();
		
		/*
		System.out.println("editUserInfo >> id >> "+id);
		System.out.println("editUserInfo >> edeptname >> "+edeptname);
		System.out.println("editUserInfo >> eremark >> "+eremark);
		System.out.println("editUserInfo >> estateflag >> "+estateflag);
		*/
		
		/*
		editUserInfo >> id >> 4
		editUserInfo >> edeptname >> test2
		editUserInfo >> eremark >> test2
		editUserInfo >> estateflag >> disable
		*/
		
		if(id!=null&&!"".equals(id)) {
			resultMap = userService.editDeptInfo(id,edeptname,eremark,estateflag);
		}
		return resultMap;
	}
	@RequestMapping(path = "/editUserInfo")
	@ResponseBody
	public String  editUserInfo(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id")==null?"":request.getParameter("id").toString().trim();
		String erole = request.getParameter("erole")==null?"":request.getParameter("erole").toString().trim();
		String edept = request.getParameter("edept")==null?"":request.getParameter("edept").toString().trim();
		String estateflag = request.getParameter("estateflag")==null?"":request.getParameter("estateflag").toString().trim();
		
		//System.out.println("editUserInfo >> id >> "+id);
		//System.out.println("editUserInfo >> erole >> "+erole);
		//System.out.println("editUserInfo >> edept >> "+edept);
		//System.out.println("editUserInfo >> estateflag >> "+estateflag);
		String editUserInfo="fail";
		if(id!=null&&!"".equals(id)) {
			editUserInfo = userService.editUserInfo(id,erole,edept,estateflag);
		}
		return editUserInfo;
	}
	@RequestMapping(path = "/deleteUser")
	@ResponseBody
	public String  deleteUser(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id")==null?"":request.getParameter("id").toString().trim();
		
		String editUserInfo="fail";
		if(id!=null&&!"".equals(id)) {
			editUserInfo = userService.deleteUser(id);
		}
		return editUserInfo;
	}
}
