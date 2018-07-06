package com.alucn.weblab.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alucn.weblab.service.ErrorCaseInfoService;

/**
 * @author haiqiw
 * 2017年6月23日 下午1:57:22
 * desc:ErrorCaseInfoController
 */

@Controller
//@RequestMapping("/errorCaseInfo")
public class ErrorCaseInfoController {
	
	@Autowired(required=true)
	private ErrorCaseInfoService errorCaseInfoService;

	@RequestMapping(path = "/getErrorCaseInfo")
	public String getErrorCaseInfo(HttpSession session, Model model) throws Exception{
		String userName = session.getAttribute("login").toString();
		Map<String,String> failCaseList = errorCaseInfoService.getErrorCaseInfo(userName, session.getAttribute("auth").toString());
		model.addAttribute("failCaseList",failCaseList);
		return "errorCaseInfo";
	}
	@RequestMapping(value="/getErrorCaseInfoTable")
	@ResponseBody
	public Map<String,Object> getErrorCaseInfoTable(HttpSession session,int limit, int offset) throws Exception{
		
		Map<String,Object> returnMap = new HashMap<String,Object>();
		String userName = session.getAttribute("login").toString();
		String auth = session.getAttribute("auth").toString();
		ArrayList<HashMap<String, Object>> failCaseList = errorCaseInfoService.getErrorCaseInfoTable(userName, auth);
		returnMap.put("rows", failCaseList);
		returnMap.put("total ", 12);
		return returnMap;
	}
	
	@RequestMapping(path = "/getErrorCaseInfoDetails")
	public String getServerInfoDetails(HttpSession session, String featureName, Model model) throws Exception{
		ArrayList<HashMap<String, Object>> errorCaseList = errorCaseInfoService.getErrorCaseInfo(featureName, session.getAttribute("login").toString(), session.getAttribute("auth").toString());
		ArrayList<HashMap<String, Object>> errorReasonList = errorCaseInfoService.getErrorCaseReason();
		ArrayList<HashMap<String, Object>> errorCaseListHis = errorCaseInfoService.getErrorCaseReasonHis();
		model.addAttribute("featureName",featureName);
		model.addAttribute("errorCaseList",errorCaseList);
		model.addAttribute("errorReasonList",errorReasonList);
		model.addAttribute("errorCaseListHis",errorCaseListHis);
		
		return "errorCaseInfoDetails";
	}
	
	@RequestMapping(path = "/setMarkCase", method = RequestMethod.POST)
	public void setMarkCase(HttpSession session, String featureName, String errorcases, String failedreasons, PrintWriter out) throws Exception{
		errorCaseInfoService.setMarkCase(session.getAttribute("login").toString(), featureName, errorcases, failedreasons);
		out.write("Successful operation!");
	}
}
