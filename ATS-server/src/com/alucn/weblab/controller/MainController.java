package com.alucn.weblab.controller;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alucn.weblab.service.MainService;


@Controller
//@RequestMapping("/main")
public class MainController {
	
	@Autowired
	private MainService mainService;
	
	@RequestMapping(value = "/getStatistics")
	public String getStatistics(Model model) throws Exception {
		model.addAllAttributes(mainService.getStatistics());
		return "main-info";
	}
	
	@RequestMapping(value = "/getCustomerCount")
	@ResponseBody
	public ArrayList<HashMap<String, Object>> getCustomerCount(Model model) throws Exception {
		return mainService.getCustomerCount();
	}
	@RequestMapping(value = "/getReleaseCount")
	@ResponseBody
	public ArrayList<HashMap<String, Object>> getReleaseCount(Model model) throws Exception {
		return mainService.getReleaseCount();
	}
	@RequestMapping(value = "/getMain")
	public String getMain(Model model) throws Exception {
		//model.addAllAttributes(mainService.getStatistics());
		return "main";
	}
	
	public MainService getMainService() {
		return mainService;
	}

	public void setMainService(MainService mainService) {
		this.mainService = mainService;
	}
}
