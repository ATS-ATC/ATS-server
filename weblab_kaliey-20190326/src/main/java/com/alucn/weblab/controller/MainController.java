package com.alucn.weblab.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

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
	
	@RequestMapping(value = "/getWelcomeInfo")
	public String getWelcomeInfo(HttpSession session,Model model) throws Exception {
		String useName = session.getAttribute("login").toString();
		model.addAttribute("caseCount", mainService.getWelcomeInfo(useName));
		return "welcome";
	}
	
	
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
		ArrayList<HashMap<String, Object>> releases = mainService.getReleaseCount();
		System.out.println("releases:==============="+releases);
		ArrayList<HashMap<String, Object>> temp_fixation = new ArrayList<HashMap<String, Object>>();
		ArrayList<HashMap<String, Object>> temp_active = new ArrayList<HashMap<String, Object>>();
		for (HashMap<String, Object> release : releases) {
			String releaseName = (String) release.get("release");
			String[] arr = {"SP29.12","SP29.15","SP29.16","SP29.17","SP29.18","SP29.19","SP31.1","SP31.2"};
			List<String> list=Arrays.asList(arr);
			if(list.contains(releaseName)) {
				temp_fixation.add(release);
			}else {
				temp_active.add(release);
			}
		}
		temp_fixation.addAll(temp_active);
		
		return temp_fixation;
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
