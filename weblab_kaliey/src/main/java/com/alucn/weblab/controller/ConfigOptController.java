package com.alucn.weblab.controller;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
	
	@RequestMapping(path = "/uodateConfig")
    @ResponseBody
    public Map<String,Object> uodateConfig(HttpSession session, HttpServletRequest request,Model model) throws Exception {
        Map<String,Object> resultMap = new HashMap<String,Object>();
        String userName = session.getAttribute("login").toString();
        String con_key = request.getParameter("con_key")==null?"":request.getParameter("con_key").toString().trim();
        String con_value = request.getParameter("con_value")==null?"":request.getParameter("con_value").toString().trim();
        String result = configOptService.updateConfig(userName, con_key, con_value);
        
    
        resultMap.put("result", result);  
        return resultMap;
        
    }

}
