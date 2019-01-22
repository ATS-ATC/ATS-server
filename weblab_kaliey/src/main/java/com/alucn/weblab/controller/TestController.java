package com.alucn.weblab.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alucn.casemanager.server.common.CaseConfigurationCache;

import net.sf.json.JSONArray;
@Controller
public class TestController {
	@RequestMapping(path = "/testSingletonCaseProperties")
	@ResponseBody
	public JSONArray searchInfo(Model model) throws Exception{
		JSONArray jsonArray = CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock, true, null);
		System.out.println(jsonArray);
		return jsonArray;
	}
	@RequestMapping(path = "/demo")
	public String test(Model model) throws Exception{
		return "demo";
	}
	
}
