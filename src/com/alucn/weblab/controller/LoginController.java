package com.alucn.weblab.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alucn.weblab.model.User;
import com.alucn.weblab.service.LoginService;

/**
 * @author haiqiw
 * 2017��6��2�� ����4:09:28
 * desc: user login
 */
@Controller
//@RequestMapping("/login")
public class LoginController {
	
	@Autowired
	private LoginService loginService;
	
	@RequestMapping(path = "/userLogin")
    public String login() {
        return "login";
    }
	
	@RequestMapping(path = "/userLogout")
    public String logout(HttpSession session) {
		String useName = session.getAttribute("login").toString();
		 if(null != useName && !"".equals(useName)){ 
			 session.removeAttribute("login"); 
	     }
        return "login";
    }
	
	@RequestMapping(path = "/userLoginCheckOut", method = RequestMethod.POST)
    public String loginCheckOut(User user, HttpSession session, Model model) {
		loginService.getUser(user);
		
		session.setAttribute("login", user.getUserName());
        return "forward:/getStatistics.do";
    }
	
	@RequestMapping(path = "/userLoginBackHome", method = RequestMethod.GET)
    public String userLoginBackHome() {
        return "forward:/getStatistics.do";
    }

	public LoginService getLoginService() {
		return loginService;
	}

	public void setLoginService(LoginService loginService) {
		this.loginService = loginService;
	}
	
}
