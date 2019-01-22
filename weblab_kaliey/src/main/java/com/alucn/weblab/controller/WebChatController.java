package com.alucn.weblab.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebChatController {
	
	@RequestMapping(value = "/chat")
	public String chat(HttpServletRequest request,HttpSession session,Model model) throws Exception {
		String username =  (String) session.getAttribute("login");
		if(!"".equals(username)) {
			session.setAttribute("username", username);
		}else {
			session.setAttribute("username", ""+Math.random());
		}
		return "chat";
	}
	
}
