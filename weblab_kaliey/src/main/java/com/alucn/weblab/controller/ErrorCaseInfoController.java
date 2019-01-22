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
		String sort = request.getParameter("sort")==null?"":request.getParameter("sort").toString().trim();
		String sortOrder = request.getParameter("sortOrder")==null?"":request.getParameter("sortOrder").toString().trim();
		
		Map<String,Object> returnMap = new HashMap<String,Object>();
		String userName = session.getAttribute("login").toString();
		//String auth = session.getAttribute("auth").toString();
		Subject subject = SecurityUtils.getSubject();  
        boolean hasRole = subject.hasRole("admin");
        boolean hasSRole = subject.hasRole("super");
		boolean checkAllCase = false;
		if (hasRole || hasSRole) {
			checkAllCase = true;
		}
		ArrayList<HashMap<String, Object>> failCaseList = errorCaseInfoService.getErrorCaseInfoTable(userName,feature, checkAllCase,offset,limit,sort,sortOrder);
		int errorCaseInfoTableCount = errorCaseInfoService.getErrorCaseInfoTableCount(userName,feature, checkAllCase);
		returnMap.put("rows", failCaseList);
		returnMap.put("total", errorCaseInfoTableCount);
		return returnMap;
	}
	
	@RequestMapping(path = "/getErrorCaseInfoDetails")
	public String getServerInfoDetails(HttpSession session, String featureName, Model model) throws Exception{
		
		String userName = session.getAttribute("login").toString();
		Subject subject = SecurityUtils.getSubject();  
        boolean hasARole = subject.hasRole("admin");
        boolean hasSRole = subject.hasRole("super");
		boolean checkAllCase = false;
		if (hasARole || hasSRole) {
		//if (hasARole) {
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
	@RequestMapping(path = "/getErrorCaseInfoDetails4")
	public String getServerInfoDetails4(HttpServletRequest request,HttpSession session, String featureName, Model model) throws Exception{
		
		String userName = session.getAttribute("login").toString();
		Subject subject = SecurityUtils.getSubject();  
		boolean hasARole = subject.hasRole("admin");
		boolean hasSRole = subject.hasRole("super");
		boolean checkAllCase = false;
		if (hasARole || hasSRole) {
			checkAllCase = true;
		}
		ArrayList<HashMap<String, Object>> errorCaseList = errorCaseInfoService.getErrorCaseInfo4(featureName, userName, checkAllCase);
		ArrayList<HashMap<String, Object>> errorReasonList = errorCaseInfoService.getErrorCaseReason();
		model.addAttribute("featureName",featureName);
		model.addAttribute("errorCaseList",errorCaseList);
		model.addAttribute("errorReasonList",errorReasonList);
		
		return "errorCaseInfoDetails4";
	}
	
	@RequestMapping(path = "/setMarkCase", method = RequestMethod.POST)
	public void setMarkCase(HttpSession session, String featureName, String errorcases, String failedreasons, PrintWriter out) throws Exception{
		errorCaseInfoService.setMarkCase(session.getAttribute("login").toString(), featureName, errorcases, failedreasons);
		out.write("Successful operation!");
	}
	@RequestMapping(path = "/setMarkCaseInfo", method = RequestMethod.POST)
	public void setMarkCaseInfo(HttpServletRequest request,HttpSession session, PrintWriter out) throws Exception{
		String featureName = request.getParameter("featureName")==null?"":request.getParameter("featureName").toString().trim();
		String caseList = request.getParameter("caseList")==null?"":request.getParameter("caseList").toString().trim();
		String failType = request.getParameter("failType")==null?"":request.getParameter("failType").toString().trim();
		String failDesc = request.getParameter("failDesc")==null?"":request.getParameter("failDesc").toString().trim();
		String tagTime = request.getParameter("tagTime")==null?"":request.getParameter("tagTime").toString().trim();
		/**
			System.out.println("featureName : "+featureName);
			System.out.println("caseList : "+caseList);
			System.out.println("failType : "+failType);
			System.out.println("failDesc : "+failDesc);
			System.out.println("tagTime : "+tagTime);
		
		  	featureName : 73796
			caseList : 73796/ej7274.json,73796/ej7277.json
			failType : case issue
			failDesc : 12
			tagTime : 2019-01-08 10:45:19
		 */
		if(featureName==null || "".equals(featureName)) {
			out.write("featureName is null,please refresh page.");
			return;
		}
		if(caseList==null || "".equals(caseList)) {
			out.write("caseList is null,please refresh page.");
			return;
		}
		if(failType==null || "".equals(failType)) {
			out.write("failedreasons is null,please refresh page.");
			return;
		}
		if(failDesc==null || "".equals(failDesc)) {
			out.write("failDesc is null,please refresh page.");
			return;
		}
		if(failType.contains("code bug")||failType.contains("case issue")) {
			if(tagTime==null || "".equals(tagTime)) {
				out.write("failDesc is null,please refresh page.");
				return;
			}
		}
		//update datesource
		String userName = session.getAttribute("login").toString();
		errorCaseInfoService.setMarkCaseInfo(userName, featureName, caseList, failType, failDesc, tagTime);
		out.write("Successful operation!");
	}
}
