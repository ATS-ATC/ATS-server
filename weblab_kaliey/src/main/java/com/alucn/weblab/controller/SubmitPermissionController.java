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
import com.alucn.weblab.service.SubmitPermissionService;
import org.apache.log4j.Logger;
/**
 * @author haiqiw
 * 2017年6月23日 下午1:57:22
 * desc:ErrorCaseInfoController
 */

@Controller
//@RequestMapping("/errorCaseInfo")
public class SubmitPermissionController {
    private static Logger logger = Logger.getLogger(SubmitPermissionController.class);
	@Autowired(required=true)
	private SubmitPermissionService submitPermissionService;

	@RequestMapping(path = "/getSubmitPermission")
	public String getSubmitPermission(HttpSession session, Model model) throws Exception{
	    model.addAllAttributes(submitPermissionService.getSubmitPermission());
		return "submitPermission";
	}
	
	@RequestMapping(path = "/getSubmitPermissionInfo")
    @ResponseBody
    public Map<String,Object> getSubmitPermissionInfo(HttpSession session, HttpServletRequest request,Model model) throws Exception {
        Map<String,Object> resultMap = new HashMap<String,Object>();
        String login_name = session.getAttribute("login").toString();
        String limit = request.getParameter("limit")==null?"":request.getParameter("limit").toString().trim();
        String offset = request.getParameter("offset")==null?"":request.getParameter("offset").toString().trim();
        String condition = request.getParameter("condition")==null?"":request.getParameter("condition").toString().trim();
        
        ArrayList<HashMap<String, Object>> allSubmitPermissionInfo = submitPermissionService.getSubmitPermissionInfo(limit,offset,condition, login_name);
        int allSubmitPermissionInfoCount = submitPermissionService.getSubmitPermissionInfoCount(condition, login_name);
        
        resultMap.put("rows", allSubmitPermissionInfo);
        resultMap.put("total", allSubmitPermissionInfoCount);
        
        return resultMap;
        
    }
	
	@RequestMapping(path = "/deleteSubmitPermission")
    @ResponseBody
    public Map<String,Object> deleteSubmitPermission(HttpSession session, HttpServletRequest request,Model model) throws Exception {
        Map<String,Object> resultMap = new HashMap<String,Object>();
        String login_name = session.getAttribute("login").toString();
        String feature_number = request.getParameter("feature_number")==null?"":request.getParameter("feature_number").toString().trim();
        String user_name = request.getParameter("user_name")==null?"":request.getParameter("user_name").toString().trim();
        String case_type = request.getParameter("type")==null?"":request.getParameter("type").toString().trim();
        String result = submitPermissionService.deleteSubmitPermission(feature_number, user_name, login_name, case_type);
        
    
        resultMap.put("result", result);  
        return resultMap;
        
    }
	
	
	@RequestMapping(path = "/updateSubmitPermission")
    @ResponseBody
    public Map<String,Object> updateSubmitPermission(HttpSession session, HttpServletRequest request,Model model) throws Exception {
        Map<String,Object> resultMap = new HashMap<String,Object>();
        String login_name = session.getAttribute("login").toString();
        
        String feature_number = request.getParameter("feature_number")==null?"":request.getParameter("feature_number").toString().trim();
        String user_name = request.getParameter("user_name")==null?"":request.getParameter("user_name").toString().trim();
        String case_type = request.getParameter("type")==null?"":request.getParameter("type").toString().trim();
        String field = request.getParameter("field")==null?"":request.getParameter("field").toString().trim();
        String value = request.getParameter("value")==null?"":request.getParameter("value").toString().trim();
        String new_value = request.getParameter(field)==null?"":request.getParameter(field).toString().trim();
        
        logger.warn("value: " + value);
        String result = submitPermissionService.updateSubmitPermission(feature_number, user_name, login_name, case_type, field, value, new_value);
        
    
        resultMap.put("result", result);  
        return resultMap;
        
    }
	
	@RequestMapping(path = "/newSubmitPermission")
    @ResponseBody
    public Map<String,Object> newSubmitPermission(HttpSession session, HttpServletRequest request,Model model) throws Exception {
        Map<String,Object> resultMap = new HashMap<String,Object>();
        String login_name = session.getAttribute("login").toString();
        
        String feature_number = request.getParameter("feature_number")==null?"":request.getParameter("feature_number").toString().trim();
        String user_name = request.getParameter("user_name")==null?"":request.getParameter("user_name").toString().trim();
        String case_type = request.getParameter("scenario")==null?"":request.getParameter("scenario").toString().trim();
        String case_num = request.getParameter("auto_case_num")==null?"":request.getParameter("auto_case_num").toString().trim();
        String ftc_date = request.getParameter("ftc_date")==null?"":request.getParameter("ftc_date").toString().trim();
        String is_special = request.getParameter("is_special")==null?"":request.getParameter("is_special").toString().trim();
        String sec_data_num = request.getParameter("sec_data_num")==null?"":request.getParameter("sec_data_num").toString().trim();
        
        logger.warn("sec_data_num: " + sec_data_num);
        String result = submitPermissionService.newSubmitPermission(user_name, feature_number, case_num, ftc_date,is_special, sec_data_num, case_type, login_name);
        
    
        resultMap.put("result", result);  
        return resultMap;
        
    }
	
	
	
}
