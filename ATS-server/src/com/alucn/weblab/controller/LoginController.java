package com.alucn.weblab.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alucn.casemanager.server.common.constant.Constant;
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
    public String login(Model model) {
		model.addAttribute("loginResult", "login");
        return "login";
    }
	
	@RequestMapping(path = "/userLogout")
    public String logout(HttpSession session, Model model) {
		model.addAttribute("loginResult", "login");
		String useName = session.getAttribute("login").toString();
		 if(null != useName && !"".equals(useName)){ 
			 session.removeAttribute("login");
			 session.invalidate();
	     }
        return "login";
    }
	
	@RequestMapping(value = "/userLoginCheckOut", method = RequestMethod.POST)
    public String loginCheckOut(User user, HttpSession session, Model model) throws Exception {
		String returnDec = "";
		if(loginService.authAdministrator(user)){
			session.setAttribute("auth", Constant.AUTH);
			model.addAttribute("loginResult", "success");
			session.setAttribute("login", user.getUserName());
			returnDec =  "forward:/getMain.do";
		}else{
			boolean authResult = loginService.authUser(user);
			if(authResult){
				authResult = loginService.getUser(user);
				if(authResult){
					session.setAttribute("auth", Constant.AUTH);
				}else{
					session.setAttribute("auth", "errorCases");
				}
				model.addAttribute("loginResult", "success");
				session.setAttribute("login", user.getUserName());
				returnDec =  "forward:/getMain.do";
			}else{
				model.addAttribute("loginResult", "failed");
				returnDec = "login";
			}
		}
		
        return returnDec;
    }
	
	@RequestMapping(path = "/userLoginBackHome", method = RequestMethod.GET)
    public String userLoginBackHome() {
        return "forward:/getMain.do";
    }

	public LoginService getLoginService() {
		return loginService;
	}

	public void setLoginService(LoginService loginService) {
		this.loginService = loginService;
	}
	
}
