package com.alucn.weblab.controller;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.weblab.model.NUser;
import com.alucn.weblab.model.User;
import com.alucn.weblab.service.LoginService;
import com.alucn.weblab.service.ReleasesService;


@Controller
public class ReleasesController { 
	
	@Autowired(required=true)
	private ReleasesService releasesService;
	@Autowired
	private LoginService loginService;
	
	@RequestMapping(value="/getReleases")
	public String getQueryCaseInfo(Model model) throws Exception {
		ArrayList<HashMap<String, Object>> releases = releasesService.getReleases();
		//System.out.println("releases:==========="+releases);
		model.addAttribute("releases",releases);
		return "releases";
	}
	@RequestMapping(path = "/releasesLogin")
    public String login(Model model) {
		model.addAttribute("loginResult", "login");
        return "loginReleases";
    }
	
	@RequestMapping(value = "/releasesLoginCheckOut", method = RequestMethod.POST)
    public String loginCheckOut(NUser user, HttpSession session, Model model) throws Exception {
		String returnDec = "";
		if(loginService.authAdministrator(user)){
			session.setAttribute("auth", Constant.AUTH);
			model.addAttribute("loginResult", "success");
			session.setAttribute("login", user.getUsername());
			returnDec =  "redirect:/getReleases.do";
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
				session.setAttribute("login", user.getUsername());
				returnDec =  "redirect:/getReleases.do";
			}else{
				model.addAttribute("loginResult", "failed");
				returnDec = "loginReleases";
			}
		}
		
        return returnDec;
    }
	
	
}
