package com.alucn.weblab.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
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
		/*String userName = session.getAttribute("login").toString();
		Map<String,String> failCaseList = errorCaseInfoService.getErrorCaseInfo(userName, session.getAttribute("auth").toString());
		model.addAttribute("failCaseList",failCaseList);*/
		return "errorCaseInfo";
	}
	@RequestMapping(value="/getErrorCaseInfoTable")
	@ResponseBody
	public Map<String,Object> getErrorCaseInfoTable(HttpServletRequest request,HttpSession session) throws Exception{
		String limit = request.getParameter("limit")==null?"":request.getParameter("limit").toString().trim();
		String offset = request.getParameter("offset")==null?"":request.getParameter("offset").toString().trim();
		String feature = request.getParameter("feature")==null?"":request.getParameter("feature").toString().trim();
		
		Map<String,Object> returnMap = new HashMap<String,Object>();
		String userName = session.getAttribute("login").toString();
		//String auth = session.getAttribute("auth").toString();
		Subject subject = SecurityUtils.getSubject();  
        boolean hasRole = subject.hasRole("admin");
		ArrayList<HashMap<String, Object>> failCaseList = errorCaseInfoService.getErrorCaseInfoTable(userName,feature, hasRole,offset,limit);
		int errorCaseInfoTableCount = errorCaseInfoService.getErrorCaseInfoTableCount(userName,feature, hasRole);
		returnMap.put("rows", failCaseList);
		returnMap.put("total", errorCaseInfoTableCount);
		return returnMap;
	}
	
	@RequestMapping(path = "/getErrorCaseInfoDetails")
	public String getServerInfoDetails(HttpSession session, String featureName, Model model) throws Exception{
		
		String userName = session.getAttribute("login").toString();
		Subject subject = SecurityUtils.getSubject();  
        boolean hasARole = subject.hasRole("admin");
        //boolean hasSRole = subject.hasRole("super");
		boolean checkAllCase = false;
		//if (hasARole || hasSRole) {
		if (hasARole) {
			checkAllCase = true;
		}
        
		//ArrayList<HashMap<String, Object>> errorCaseList = errorCaseInfoService.getErrorCaseInfo(featureName, session.getAttribute("login").toString(), session.getAttribute("auth").toString());
		ArrayList<HashMap<String, Object>> errorCaseList = errorCaseInfoService.getErrorCaseInfo(featureName, userName, checkAllCase);
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
