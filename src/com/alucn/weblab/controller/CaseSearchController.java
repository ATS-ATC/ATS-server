package com.alucn.weblab.controller;

import java.io.PrintWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import com.alucn.weblab.service.CaseSearchService;

/**
 * @author haiqiw
 * 2017年8月7日 上午10:10:05
 * desc:CaseSearchController
 */
@Controller
public class CaseSearchController {

	@Autowired(required=true)
	private CaseSearchService caseSearchService;
	
	
	@RequestMapping(path = "/searchInfo")
	public String searchInfo(Model model) throws Exception{
		model.addAllAttributes(caseSearchService.getCaseSearch());
		return "caseSearch";
	}
	
	@RequestMapping(path = "/searchCaseInfo")
	public void searchCaseInfo(Model model, String condition,  PrintWriter out) throws Exception{
		System.out.println(condition);
		out.write(caseSearchService.searchCaseInfo(condition));
	}
}
