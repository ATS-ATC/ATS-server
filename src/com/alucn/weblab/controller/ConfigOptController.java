package com.alucn.weblab.controller;

import java.io.PrintWriter;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alucn.weblab.service.ConfigOptService;

/**
 * @author haiqiw
 * 2017年6月23日 下午1:53:05
 * desc:ConfigOptController
 */
@Controller
public class ConfigOptController {
	
	@Autowired(required=true)
	private ConfigOptService configOptService;
	
	@RequestMapping(path = "/config")
	public String getConfig(Model model) throws Exception{
		model.addAttribute("config",configOptService.getConfig());
		return "configMan";
	}
	
	@RequestMapping(path = "/uodateConfig", method = RequestMethod.POST)
	public void updateConfig(String click, String configKey, String configValue, HttpSession session, PrintWriter out) throws Exception{
		String userName = session.getAttribute("login").toString();
		out.write(configOptService.updateConfig(userName, configKey, configValue));
	}

}
