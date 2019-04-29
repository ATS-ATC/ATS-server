package com.alucn.weblab.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.weblab.model.NUser;
import com.alucn.weblab.service.LoginService;
import com.alucn.weblab.utils.MD5Util;

@Controller
//@RequestMapping("/login")
public class LoginController {
	
	@Autowired
	private LoginService loginService;
	
	@RequestMapping(path = "/userLogin")
    public String login(HttpSession session,Model model) {
		/*Object loginAttribute = session.getAttribute("login");
		if("".equals(loginAttribute)) {*/
			model.addAttribute("loginResult", "login");
			long token=System.currentTimeMillis();
			session.setAttribute("token", token);
	        return "login";
		/*}else {
			System.err.println("loginAttribute   >>   "+loginAttribute);
			return "redirect:/getMain.do";
		}*/
    }
	
	@RequestMapping(path = "/userLogout")
    public String logout(HttpSession session, Model model) {
		model.addAttribute("loginResult", "login");
		String useName = session.getAttribute("login").toString();
		 if(null != useName && !"".equals(useName)){ 
			 Subject subject = SecurityUtils.getSubject();
	         subject.logout();
			 /*session.removeAttribute("login");
			 session.invalidate();*/
	     }
		//session.invalidate();
		/*long token=System.currentTimeMillis();
		session.setAttribute("token", token);
        return "login";*/
		return "redirect:/";
    }
	
	@RequestMapping(value = "/userLoginCheckOut", method = RequestMethod.POST)
    public String loginCheckOut(NUser nuser, HttpSession session, Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		String Reqtoken = request.getParameter("Reqtoken")==null?"":request.getParameter("Reqtoken").toString().trim();
		String loginToken = ""+session.getAttribute("token");
		if (Reqtoken==null || loginToken==null || !loginToken.equals(Reqtoken)) { 
			Object loginAttribute = session.getAttribute("login");
			if(!"".equals(loginAttribute)) {
				return "redirect:/getMain.do";
			}
			return "login";
		}else {
			session.removeAttribute("token");
		}
		String returnDec = "";
		//单点登录root
		if(loginService.authAdministrator(nuser)){
			session.setAttribute("auth", Constant.AUTH);
			model.addAttribute("loginResult", "success");
			session.setAttribute("login", nuser.getUsername());
			
			Subject subject = SecurityUtils.getSubject();
			UsernamePasswordToken token = new UsernamePasswordToken(nuser.getUsername(), MD5Util.md5(nuser.getPassword()));
			//UsernamePasswordToken token = new UsernamePasswordToken(nuser.getUsername(), nuser.getPassword());
			try {
				subject.login(token);
			} catch (Exception e) {
				e.printStackTrace();
				return "login";
			}
			
			returnDec =  "redirect:/getMain.do";
		}else{
			//取ldap服务器登陆结果
			boolean authResult = loginService.authUser(nuser);
			if(authResult){
				//登陆成功,后验证是否在数据库内，如果不在则创建基本用户
				ArrayList<HashMap<String, Object>> nUser = loginService.queryNUser(nuser);
				if(nUser.size()>0){
					//在数据库内，可能为逻辑删除，也可能是正常
					session.setAttribute("auth", Constant.AUTH);
				}else{
					session.setAttribute("auth", "errorCases");
					//不在数据库内，
					//验证是否为逻辑删除
					ArrayList<HashMap<String,Object>> allNUser = loginService.queryAllNUser(nuser);
					//如果能查到，则说明该用户是被逻辑删除的用户
					if(allNUser.size()>0) {
						return "login";
					}else {
						//1、创建用户，
						//2、赋予基本用户权限
						loginService.createLdapUser(nuser);
					}
					
				}
				//3、放入session权限
				//取出内部的权限放入session
				Subject subject = SecurityUtils.getSubject();
				UsernamePasswordToken token = new UsernamePasswordToken(nuser.getUsername(), MD5Util.md5(nuser.getPassword()));
				//UsernamePasswordToken token = new UsernamePasswordToken(nuser.getUsername(), nuser.getPassword());
				try {
					subject.login(token);
				} catch (Exception e) {
					e.printStackTrace();
					return "login";
				}
				model.addAttribute("loginResult", "success");
				session.setAttribute("login", nuser.getUsername());
				
				returnDec =  "redirect:/getMain.do";
			}else{
				model.addAttribute("loginResult", "failed");
				returnDec = "login";
			}
		}
		
        return returnDec;
    }
	
	@RequestMapping(path = "/userLoginBackHome", method = RequestMethod.GET)
    public String userLoginBackHome() {
        return "redirect:/getMain.do";
    }

	public LoginService getLoginService() {
		return loginService;
	}

	public void setLoginService(LoginService loginService) {
		this.loginService = loginService;
	}
	
}
