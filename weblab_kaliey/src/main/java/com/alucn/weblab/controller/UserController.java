package com.alucn.weblab.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alucn.weblab.service.UserService;

@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(path = "/getUserInfo")
    public String getUserInfo()  {
        return "userInfo";
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
	@RequestMapping(path = "/getRolePermission")
	@ResponseBody
	public ArrayList<HashMap<String, Object>> getRolePermission(HttpServletRequest request,Model model) throws Exception {
		String rolename = request.getParameter("rolename")==null?"":request.getParameter("rolename").toString().trim();
		
		ArrayList<HashMap<String, Object>> rolePermission = userService.getRolePermission(rolename);
		System.out.println("getRolePermission >> "+rolePermission);
		return rolePermission;
	}
	
}
