package com.alucn.weblab.controller;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alucn.weblab.service.ReleasesService;


@Controller
public class ReleasesController { 
	
	@Autowired(required=true)
	private ReleasesService releasesService;

	@RequestMapping(value="/getReleases")
	public String getQueryCaseInfo(Model model) throws Exception {
		ArrayList<HashMap<String, Object>> releases = releasesService.getReleases();
		//System.out.println("releases:==========="+releases);
		model.addAttribute("releases",releases);
		return "releases";
	}
	
}
