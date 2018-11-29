package com.alucn.weblab.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alucn.casemanager.server.common.CaseConfigurationCache;
import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.model.ServerSort;
import com.alucn.casemanager.server.common.util.HttpReq;
import com.alucn.weblab.model.NServer;
import com.alucn.weblab.model.NUser;
import com.alucn.weblab.service.LoginService;
import com.alucn.weblab.service.ServerInfoService;
import com.alucn.weblab.service.SpaAndRtdbManService;
import com.alucn.weblab.utils.StringUtil;
import com.alucn.weblab.utils.TimeUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
//@RequestMapping("/serverInfo")
public class ServerInfoController {
	private static Logger logger = Logger.getLogger(ServerInfoController.class);
	@Autowired
	private LoginService loginService;
	@Autowired
	private ServerInfoService serverInfoService;
	@Autowired(required=true)
	private SpaAndRtdbManService spaAndRtdbManService;
	
	/*@RequestMapping(path = "/getServerInfo")
	public String getServerInfo(Model model){
		Map<String,Set<Map<String,JSONObject>>> infos = serverInfoService.getServerInfo();
		model.addAttribute("infos", infos);
		return "serverInfo";
	}*/
	
	//lab变动日志记录表
	@RequestMapping(path = "/getServerStatusLog")
	public String getServerStatusLog(Model model){
		return "serverStatusLog";
	}
	@RequestMapping(path = "/getServerStatusLogJson")
	@ResponseBody
	public Map<String,Object> getServerStatusLogJson(HttpServletRequest request,Model model,HttpSession session) throws Exception {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		
		String limit = request.getParameter("limit")==null?"":request.getParameter("limit").toString().trim();
		String offset = request.getParameter("offset")==null?"":request.getParameter("offset").toString().trim();
		String serverName = request.getParameter("serverName")==null?"":request.getParameter("serverName").toString().trim();
		String username = (String) session.getAttribute("login");
		ArrayList<HashMap<String, Object>> deptByUserName = loginService.getDeptByUserName(username);
		String deptid="";
		if(deptByUserName.size()==1) {
			deptid = (String) deptByUserName.get(0).get("deptid");
		}
		/*Subject subject = SecurityUtils.getSubject();  
		boolean hasRole = subject.hasRole("admin");*/
		ArrayList<HashMap<String, Object>> labLogJson = serverInfoService.getServerStatusLogJson(limit,offset,serverName,deptid);
		int labLogJsonCount = serverInfoService.getServerStatusLogJsonCount(limit, offset, serverName, deptid);
		resultMap.put("rows", labLogJson);
		resultMap.put("total", labLogJsonCount);
		
		return resultMap;
		
	}
	
	
	
	@RequestMapping(value="/getServerInfoJson")
	@ResponseBody
	public ArrayList<HashMap<String, Object>> getServerInfoJson(HttpSession session) throws Exception{
		
		ArrayList<HashMap<String, Object>> resultList = new ArrayList<HashMap<String, Object>>();
		String username = (String) session.getAttribute("login");
		ArrayList<HashMap<String, Object>> deptByUserName = loginService.getDeptByUserName(username);
		String deptid="";
		if(deptByUserName.size()==1) {
			deptid = (String) deptByUserName.get(0).get("deptid");
		}
		Subject subject = SecurityUtils.getSubject();  
        boolean hasRole = subject.hasRole("admin");
		Map<String,Set<ServerSort>> infos = serverInfoService.getServerInfo();
		//logger.info("infos:==============="+infos);
		int id =1;
		for(String info :infos.keySet()) {
			int pid = 0;
			//父级目录
			HashMap<String,Object> superMap = new LinkedHashMap<String, Object>();
			superMap.put("id", id);
			superMap.put("pid", pid);
			superMap.put("name", info);
			superMap.put("status", 0);
			superMap.put("type", "set");
			superMap.put("serverIp", "");
			superMap.put("serverRelease", "");
			superMap.put("serverProtocol", "");
			superMap.put("serverType", "");
			superMap.put("serverMate", "");
			superMap.put("mateServer", "");
			superMap.put("deptname", "");
			superMap.put("hodingtime", "");
			
			resultList.add(superMap);
			pid=id;
			id++;
			//子级目录
			Set<ServerSort> sets = infos.get(info);
			for (ServerSort set : sets) {
				Map<String, JSONObject> maps = set.getMap();
				for(String map :maps.keySet()) {
					HashMap<String,Object> childMap = new LinkedHashMap<String, Object>();
					id++;
					JSONObject jsonObject = maps.get(map);
					//logger.info("===============");
					//logger.info(jsonObject);
					//{"lab":{"serverName":"BJRMS21C","serverIp":"135.242.17.206","serverRelease":"SP17.9","serverProtocol":"ITU","serverType":"Line","serverMate":"Primary","mateServer":"BJRMS21D","setName":"set2","serverSPA":["AethosTest","CDRPP311","CDRPPGW311","DIAMCL179","DROUTER179","ECTRL179","ENWTPPS179","EPAY179","EPPSA179","EPPSM179","GATEWAY179","NWTCOM111","NWTGSM066"],"serverRTDB":["SCRRTDBV7","AECIDB179","SGLDB28H","TIDDB28C","GPRSSIM08","AIRTDB179","CTRTDB179","HTIDDB179","PMOUDB179","PROMDB179","SIMDB179","SYDB179","GCIPL312","VTXDB179","SHRTDB28F","CDBRTDB","RCNRDB173","HMRTDB173","SESSDB311","ACMDB104","SIMIDXDB","FSNDB173","UARTDB287","RERTDB279","SFFDB28C","GCURDB","SLTBLRTDB","ID2MDN01","GTMDB28A"]},"taskStatus":{"status":"Idle","runningCase":""},"taskResult":{"success":[],"fail":[]}}
					//{"lab":{"serverName":"BJRMS21D","serverIp":"135.242.17.206","serverRelease":"SP17.9","serverProtocol":"ITU","serverType":"Line","serverMate":"Secondary","mateServer":"BJRMS21C","setName":"set2","serverSPA":["AethosTest","CDRPP311","CDRPPGW311","DIAMCL179","DROUTER179","ECTRL179","ENWTPPS179","EPAY179","EPPSA179","EPPSM179","GATEWAY179","NWTCOM111","NWTGSM066"],"serverRTDB":["SCRRTDBV7","AECIDB179","SGLDB28H","TIDDB28C","GPRSSIM08","AIRTDB179","CTRTDB179","HTIDDB179","PMOUDB179","PROMDB179","SIMDB179","SYDB179","GCIPL312","VTXDB179","SHRTDB28F","CDBRTDB","RCNRDB173","HMRTDB173","SESSDB311","ACMDB104","SIMIDXDB","FSNDB173","UARTDB287","RERTDB279","SFFDB28C","GCURDB","SLTBLRTDB","ID2MDN01","GTMDB28A"]},"taskStatus":{"status":"Idle","runningCase":""},"taskResult":{"success":[],"fail":[]}}
					//"taskStatus":{"status":"Idle","runningCase":""}
					JSONObject lab = jsonObject.getJSONObject("lab");
					//logger.info(lab);
					//logger.info("===============");
					String serverName = lab.getString("serverName");
					String serverIp = lab.getString("serverIp");
					String serverRelease = lab.getString("serverRelease");
					String serverProtocol = lab.getString("serverProtocol");
					String serverType = lab.getString("serverType");
					String serverMate = lab.getString("serverMate");
					String mateServer = lab.getString("mateServer");
					String sdeptid = lab.getString("deptid");
					
					long lasttime = lab.getLong("lasttime");
					long nowtime = new Date().getTime();
					String timeDifference = TimeUtil.getTimeDifference(nowtime, lasttime);
					//long hodingtime = nowtime-lasttime;
					
					ArrayList<HashMap<String, Object>> deptById = loginService.getDeptById(sdeptid);
					String deptname = "";
					if(deptById.size()>0) {
						deptname = (String) deptById.get(0).get("dept_name");
					}
					
					String labdeptid = "";
					try {
						labdeptid = lab.getString("deptid");
					}catch (Exception e) {
						e.printStackTrace();
					}
					String status = jsonObject.getJSONObject("taskStatus").getString("status");
					/*int stateflag = 1;
					if("Idle".equals(status)) {
						stateflag=0;
					}
					if("Dead".equals(status)) {
						stateflag=1;
					}
					if("Running".equals(status)) {
						stateflag=2;
					}
					if("Ready".equals(status)) {
						stateflag=3;
					}
					if("Finished".equals(status)) {
						stateflag=4;
					}*/
					childMap.put("id", id);
					childMap.put("pid", pid);
					childMap.put("name", serverName);
					//childMap.put("status", stateflag);
					childMap.put("status", status);
					childMap.put("type", "server");
					childMap.put("serverIp", serverIp);
					childMap.put("serverRelease", serverRelease);
					childMap.put("serverProtocol", serverProtocol);
					childMap.put("serverType", serverType);
					childMap.put("serverMate", serverMate);
					childMap.put("mateServer", mateServer);
					childMap.put("deptname", deptname);
					childMap.put("hodingtime", timeDifference);
					
					if(deptid.equals(labdeptid)||hasRole) {
						resultList.add(childMap);
					}
					//logger.info("resultList:========"+resultList);
					id++;
				}
			}
			
		}
		Set <Integer> setList = new HashSet();
		List <Integer> idList = new ArrayList<>();
		for (int i=0;i<resultList.size();i++) {
			int sid =  (int) resultList.get(i).get("id");
			int spid = (int) resultList.get(i).get("pid");
			String stype = (String) resultList.get(i).get("type");
			if("server".equals(stype)) {
				setList.add(spid);
			}
			else if("set".equals(stype)) {
				idList.add(sid);
			}
		}
		idList.removeAll(setList);
		//logger.info("idList >> "+idList);
		ArrayList<HashMap<String, Object>> midResultList = new ArrayList<HashMap<String, Object>>();
		
		for (HashMap<String, Object> hashMap : resultList) {
			int sid = (int) hashMap.get("id");
			if(!idList.contains(sid)) {
				midResultList.add(hashMap);
			}
		}
		
		return midResultList;
	}
	
	
	
	@RequestMapping(path = "/getServerInfo")
	public String getServerInfo(Model model, HttpSession session) throws Exception{
		
		//2
		//Map<String,Set<ServerSort>> infos = serverInfoService.getServerInfo();
		/*JSONArray jsonTree = new JSONArray();
		for (String info : infos.keySet()) {
			JSONObject value = new JSONObject();
			value.put("text", info);
			Set<ServerSort> sets = infos.get(info);
			logger.info(sets);
			JSONArray childTree = new JSONArray();
			for (ServerSort set : sets) {
				JSONObject child = new JSONObject();
				Map<String, JSONObject> maps = set.getMap();
				//logger.info(maps);
				for (String map : maps.keySet()) {
					child.put("text", map);
				}
				childTree.add(child);
			}
			value.put("nodes", childTree);
			jsonTree.add(value);
		}
		
		model.addAttribute("jsonTree", jsonTree);*/
		//1 
		//Map<String,Set<ServerSort>> infos = serverInfoService.getServerInfo();
		//model.addAttribute("infos", infos);
		
		//3
		
		
		
		//logger.info(infos);
		//{set1=[ServerSort [map={BJRMS21A={"lab":{"serverName":"BJRMS21A","serverIp":"135.242.17.206","serverRelease":"SP17.9","serverProtocol":"ITU","serverType":"Line","serverMate":"Standalone","mateServer":"N","setName":"set1","serverSPA":["AethosTest","CDRPP311","CDRPPGW311","DIAMCL179","DROUTER179","ECTRL179","ENWTPPS179","EPAY179","EPPSA179","EPPSM179","GATEWAY179","NWTCOM111","NWTGSM066"],"serverRTDB":["SCRRTDBV7","AECIDB179","SGLDB28H","TIDDB28C","GPRSSIM08","AIRTDB179","CTRTDB179","HTIDDB179","PMOUDB179","PROMDB179","SIMDB179","SYDB179","GCIPL312","VTXDB179","SHRTDB28F","CDBRTDB","RCNRDB173","HMRTDB173","SESSDB311","ACMDB104","SIMIDXDB","FSNDB173","UARTDB287","RERTDB279","SFFDB28C","GCURDB","SLTBLRTDB","ID2MDN01","GTMDB28A"]},"taskStatus":{"status":"Idle","runningCase":""},"taskResult":{"success":[],"fail":[]}}}], ServerSort [map={BJRMS21B={"lab":{"serverName":"BJRMS21B","serverIp":"135.242.17.206","serverRelease":"SP17.9","serverProtocol":"ITU","serverType":"Group","serverMate":"Standalone","mateServer":"N","setName":"set1","serverSPA":["AethosTest","CDRPP311","CDRPPGW311","DIAMCL179","DROUTER179","ECTRL179","ENWTPPS179","EPAY179","EPPSA179","EPPSM179","GATEWAY179","NWTCOM111","NWTGSM066"],"serverRTDB":["SCRRTDBV7","AECIDB179","SGLDB28H","TIDDB28C","GPRSSIM08","AIRTDB179","CTRTDB179","HTIDDB179","PMOUDB179","PROMDB179","SIMDB179","SYDB179","GCIPL312","VTXDB179","SHRTDB28F","CDBRTDB","RCNRDB173","HMRTDB173","SESSDB311","ACMDB104","SIMIDXDB","FSNDB173","UARTDB287","RERTDB279","SFFDB28C","GCURDB","SLTBLRTDB","ID2MDN01","GTMDB28A"]},"taskStatus":{"status":"Idle","runningCase":""},"taskResult":{"success":[],"fail":[]}}}], ServerSort [map={BJRMS21C={"lab":{"serverName":"BJRMS21C","serverIp":"135.242.17.206","serverRelease":"SP17.9","serverProtocol":"ITU","serverType":"Line","serverMate":"Primary","mateServer":"BJRMS21D","setName":"set1","serverSPA":["AethosTest","CDRPP311","CDRPPGW311","DIAMCL179","DROUTER179","ECTRL179","ENWTPPS179","EPAY179","EPPSA179","EPPSM179","GATEWAY179","NWTCOM111","NWTGSM066"],"serverRTDB":["SCRRTDBV7","AECIDB179","SGLDB28H","TIDDB28C","GPRSSIM08","AIRTDB179","CTRTDB179","HTIDDB179","PMOUDB179","PROMDB179","SIMDB179","SYDB179","GCIPL312","VTXDB179","SHRTDB28F","CDBRTDB","RCNRDB173","HMRTDB173","SESSDB311","ACMDB104","SIMIDXDB","FSNDB173","UARTDB287","RERTDB279","SFFDB28C","GCURDB","SLTBLRTDB","ID2MDN01","GTMDB28A"]},"taskStatus":{"status":"Idle","runningCase":""},"taskResult":{"success":[],"fail":[]}}, BJRMS21D={"lab":{"serverName":"BJRMS21D","serverIp":"135.242.17.206","serverRelease":"SP17.9","serverProtocol":"ITU","serverType":"Line","serverMate":"Secondary","mateServer":"BJRMS21C","setName":"set1","serverSPA":["AethosTest","CDRPP311","CDRPPGW311","DIAMCL179","DROUTER179","ECTRL179","ENWTPPS179","EPAY179","EPPSA179","EPPSM179","GATEWAY179","NWTCOM111","NWTGSM066"],"serverRTDB":["SCRRTDBV7","AECIDB179","SGLDB28H","TIDDB28C","GPRSSIM08","AIRTDB179","CTRTDB179","HTIDDB179","PMOUDB179","PROMDB179","SIMDB179","SYDB179","GCIPL312","VTXDB179","SHRTDB28F","CDBRTDB","RCNRDB173","HMRTDB173","SESSDB311","ACMDB104","SIMIDXDB","FSNDB173","UARTDB287","RERTDB279","SFFDB28C","GCURDB","SLTBLRTDB","ID2MDN01","GTMDB28A"]},"taskStatus":{"status":"Idle","runningCase":""},"taskResult":{"success":[],"fail":[]}}}], ServerSort [map={BJRMS21E={"lab":{"serverName":"BJRMS21E","serverIp":"135.242.17.206","serverRelease":"SP17.9","serverProtocol":"ITU","serverType":"Group","serverMate":"Primary","mateServer":"BJRMS21F","setName":"set1","serverSPA":["AethosTest","CDRPP311","CDRPPGW311","DIAMCL179","DROUTER179","ECTRL179","ENWTPPS179","EPAY179","EPPSA179","EPPSM179","GATEWAY179","NWTCOM111","NWTGSM066"],"serverRTDB":["SCRRTDBV7","AECIDB179","SGLDB28H","TIDDB28C","GPRSSIM08","AIRTDB179","CTRTDB179","HTIDDB179","PMOUDB179","PROMDB179","SIMDB179","SYDB179","GCIPL312","VTXDB179","SHRTDB28F","CDBRTDB","RCNRDB173","HMRTDB173","SESSDB311","ACMDB104","SIMIDXDB","FSNDB173","UARTDB287","RERTDB279","SFFDB28C","GCURDB","SLTBLRTDB","ID2MDN01","GTMDB28A"]},"taskStatus":{"status":"Idle","runningCase":""},"taskResult":{"success":[],"fail":[]}}, BJRMS21F={"lab":{"serverName":"BJRMS21F","serverIp":"135.242.17.206","serverRelease":"SP17.9","serverProtocol":"ITU","serverType":"Group","serverMate":"Secondary","mateServer":"BJRMS21E","setName":"set1","serverSPA":["AethosTest","CDRPP311","CDRPPGW311","DIAMCL179","DROUTER179","ECTRL179","ENWTPPS179","EPAY179","EPPSA179","EPPSM179","GATEWAY179","NWTCOM111","NWTGSM066"],"serverRTDB":["SCRRTDBV7","AECIDB179","SGLDB28H","TIDDB28C","GPRSSIM08","AIRTDB179","CTRTDB179","HTIDDB179","PMOUDB179","PROMDB179","SIMDB179","SYDB179","GCIPL312","VTXDB179","SHRTDB28F","CDBRTDB","RCNRDB173","HMRTDB173","SESSDB311","ACMDB104","SIMIDXDB","FSNDB173","UARTDB287","RERTDB279","SFFDB28C","GCURDB","SLTBLRTDB","ID2MDN01","GTMDB28A"]},"taskStatus":{"status":"Idle","runningCase":""},"taskResult":{"success":[],"fail":[]}}}]]}
		String username = (String) session.getAttribute("login");
		ArrayList<HashMap<String, Object>> deptByUserName = loginService.getDeptByUserName(username);
		String deptname="";
		String deptid="";
		if(deptByUserName.size()==1) {
			deptname = (String) deptByUserName.get(0).get("dept_name");
			deptid = (String) deptByUserName.get(0).get("deptid");
		}
		model.addAttribute("deptname", deptname);
		model.addAttribute("deptid", deptid);
		model.addAllAttributes(spaAndRtdbManService.getSpaAndRtdbInfo());
		JSONArray Servers = CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock, true, null);
		//result.put("Servers", Servers);
		//List setList = new ArrayList<>();
		Set sets = new HashSet<>();
		if(Servers.size()>0) {
			for (int i =0 ;i<Servers.size();i++) {
				JSONObject lab = Servers.getJSONObject(i).getJSONObject(Constant.LAB);
				String sdeptid ="";
				try {
					sdeptid = lab.getString("deptid");
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(deptid.equals(sdeptid)) {
					String setName = lab.getString("setName");
					sets.add(setName);
				}
			}
		}
		sets.add("default");
		model.addAttribute("sets", sets);
		return "serverInfo3";
	}
	/*@RequestMapping(path = "/testGetServerInfo")
	public String test(Model model){
		Map<String,Set<ServerSort>> infos = serverInfoService.getServerInfo();
		logger.info("-----------------------infos------------------------------");
		return null;
	}*/
	@RequestMapping(path = "/getServerDetails")
	public String getServerDetails(String serverName, Model model){
		Map<String,Set<Map<String,JSONObject>>> infos = serverInfoService.getServerInfoNosort();
		for(String key : infos.keySet()){
			Set<Map<String,JSONObject>> set = infos.get(key);
			Iterator<Map<String, JSONObject>> iterator = set.iterator();
			while(iterator.hasNext()){
				Map<String,JSONObject> serverOrMate = iterator.next();
				if(serverOrMate.get(serverName)!=null){
					model.addAttribute("info", JSONObject.fromObject(serverOrMate.get(serverName)));
				}
			}
		}
		return "serverDetails";
	}
	
	@RequestMapping(path = "/addlablog")
	public String addlablog(Model model) throws Exception{
		//model.addAllAttributes(spaAndRtdbManService.getSpaAndRtdbInfo());
		return "addlablog";
	}
	@RequestMapping(path = "/addlablogJson")
	@ResponseBody
	public Map<String,Object> addlablogJson(HttpServletRequest request,Model model,HttpSession session) throws Exception {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		
		String limit = request.getParameter("limit")==null?"":request.getParameter("limit").toString().trim();
		String offset = request.getParameter("offset")==null?"":request.getParameter("offset").toString().trim();
		String labname = request.getParameter("labname")==null?"":request.getParameter("labname").toString().trim();
		String username = (String) session.getAttribute("login");
		ArrayList<HashMap<String, Object>> deptByUserName = loginService.getDeptByUserName(username);
		String deptid="";
		if(deptByUserName.size()==1) {
			deptid = (String) deptByUserName.get(0).get("deptid");
		}
		ArrayList<HashMap<String, Object>> labLogJson = serverInfoService.getLabLogJson(limit,offset,labname,deptid);
		if(labLogJson.size()>0) {
			for (HashMap<String, Object> hashMap : labLogJson) {
				String createtime = (String) hashMap.get("createtime");
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				long lt = new Long(createtime);
		        Date date = new Date(lt);
				String format = simpleDateFormat.format(date);
				hashMap.put("createtime", format);
			}
		}
		int labLogJsonCount = serverInfoService.getLabLogJsonCount(labname,deptid);
		
		resultMap.put("rows", labLogJson);
		resultMap.put("total", labLogJsonCount);
		
		return resultMap;
		
	}
	@RequestMapping(path = "/addServerInfo")
	public String addServerInfo(Model model) throws Exception{
		model.addAllAttributes(spaAndRtdbManService.getSpaAndRtdbInfo());
		return "addServerInfo";
	}
	@RequestMapping(path = "/getLabInfo")
	@ResponseBody
	public Map<String, Object> getLabInfo(HttpSession session,Model model,HttpServletRequest request) throws Exception{
		String aeservername = request.getParameter("aeservername")==null?"":request.getParameter("aeservername").toString().trim();
		Map<String, Object> result = new HashMap<String, Object>();
		//通过lab的名称获取genClient所需要的信息返回
		JSONObject reqUrl = HttpReq.reqUrl("http://135.251.249.124:9333/spadm/default/labapi/dailylab/"+aeservername+".json");
		if(!reqUrl.isEmpty()) {
			JSONArray jsonArray = reqUrl.getJSONArray("content");
			if(jsonArray.size()>0) {
				
				String labname = jsonArray.getJSONObject(0).getString("labname");
				String status = jsonArray.getJSONObject(0).getString("status");
				String ips = jsonArray.getJSONObject(0).getString("ips");
				String ip ="";
				List<String> ipList = new ArrayList<>();
				if(ips.contains(",")) {
					String[] ipss = ips.split(",");
					for (String string : ipss) {
						ipList.add(string);
					}
					ip=ipss[0];
				}
				String ss7 = jsonArray.getJSONObject(0).getString("ss7");
				String enwtpps = jsonArray.getJSONObject(0).getString("enwtpps");
				String log = jsonArray.getJSONObject(0).getString("log");
				String free = jsonArray.getJSONObject(0).getString("free");
				String ptversion = jsonArray.getJSONObject(0).getString("log");
				if("Succeed".equals(status)) {
					result.put("ipList", ipList);
					result.put("ss7", ss7);
					result.put("enwtpps", enwtpps);
				}else if ("Installing".equals(status)) {
					result.put("result", "fail");
					result.put("msg", "lab is installing");
					return result;
				}else if ("Failed".equals(status)) {
					result.put("result", "fail");
					result.put("msg", "lab status failed");
					return result;
				}
			}else {
				result.put("result", "fail");
				result.put("msg", "cannot find the lab");
				return result;
			}
		}
		
		String username = session.getAttribute("login").toString();
		NUser user = new NUser();
		user.setUsername(username);
		ArrayList<HashMap<String, Object>> queryNUser =  new ArrayList<HashMap<String, Object>>();
		try {
			queryNUser = loginService.queryNUser(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String createid ="";
		String deptid ="";
		if(queryNUser.size()>0) {
			createid = (String) queryNUser.get(0).get("id");
			deptid = (String) queryNUser.get(0).get("deptid");
		}
		JSONArray Servers = CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock, true, null);
		//result.put("Servers", Servers);
		//List setList = new ArrayList<>();
		Set sets = new HashSet<>();
		if(Servers.size()>0) {
			for (int i =0 ;i<Servers.size();i++) {
				JSONObject lab = Servers.getJSONObject(i).getJSONObject(Constant.LAB);
				String sdeptid ="";
				try {
					sdeptid = lab.getString("deptid");
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(deptid.equals(sdeptid)) {
					String setName = lab.getString("setName");
					sets.add(setName);
				}
			}
		}
		sets.add("default");
		result.put("setList", sets);
		result.put("result", "success");
		return result;
	}
	//http://localhost:8080/weblab/genClient.do?labname=CHSP12B&ip=135.2.213.211&enwtpps=SP18.9&ss7=ITU&setname=set1
	@RequestMapping(path = "/genClient")
	@ResponseBody
	public Map<String, Object> genClient(HttpSession session,HttpServletRequest request) throws Exception{
		Map<String, Object> result = new HashMap<String, Object>();
		
		
		
		String labname = request.getParameter("labname")==null?"":request.getParameter("labname").toString().trim();
		String ip = request.getParameter("ip")==null?"":request.getParameter("ip").toString().trim();
		String enwtpps = request.getParameter("enwtpps")==null?"":request.getParameter("enwtpps").toString().trim();
		String ss7 = request.getParameter("ss7")==null?"":request.getParameter("ss7").toString().trim();
		String setname = request.getParameter("setname")==null?"":request.getParameter("setname").toString().trim();
		String username = session.getAttribute("login").toString();
		
		
		
		NUser user = new NUser();
		user.setUsername(username);
		ArrayList<HashMap<String, Object>> queryNUser =  new ArrayList<HashMap<String, Object>>();
		String deptid= "";
		String createid= "";
		try {
			queryNUser = loginService.queryNUser(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(queryNUser.size()>0) {
			deptid = (String) queryNUser.get(0).get("deptid");
			createid = (String) queryNUser.get(0).get("id");
		}
		String createtime =  new Date().getTime()+"";
		serverInfoService.addLabStatus("Installing", "exist", "", enwtpps, ss7, labname, "", "", ip, "", "", deptid, createid, createtime,"");
		try {
			logger.info("cd /home/huanglei && ./genClient.sh "+labname+" "+ip+" "+enwtpps+" "+ss7+" "+setname+" "+deptid);
			//为了测试注释掉执行部分
			Exec("cd /home/huanglei && ./genClient.sh "+labname+" "+ip+" "+enwtpps+" "+ss7+" "+setname+" "+deptid);
			serverInfoService.editLabStatus("Succeed", "", labname, new Date().getTime()+"", createtime);
			result.put("result", "success");
		}catch (Exception e) {
			e.printStackTrace();
			serverInfoService.editLabStatus("Failed", "", labname, new Date().getTime()+"", createtime);
			result.put("result", "fail");
		}
		return result;
	}
	public Map<String, Object> getlabGroupFlag(String labname,String deptid) throws Exception {
		Map<String, Object> result = new HashMap<>();
		if(deptid.equals("1")) {
			result.put("result", false);
			result.put("msg", "The default group does not allow lab creation.");
			return result;
		}
		JSONObject reqUrl = HttpReq.reqUrl("http://135.251.249.124:9333/spadm/default/labapi/kvmlabusage/"+labname+".json");
		if(!reqUrl.isEmpty()) {
			JSONArray jsonArray = reqUrl.getJSONArray("content");
			if(jsonArray.size()>0) {
				String labuser = jsonArray.getJSONObject(0).getString("labuser");
				NUser user = new NUser();
				user.setUsername(labuser.trim());
				ArrayList<HashMap<String,Object>> queryNUser = loginService.queryNUser(user);
				if(queryNUser.size()==1) {
					if(queryNUser.get(0).get("deptid").equals(deptid)) {
						result.put("result", true);
						return result;
					}else {
						result.put("result", false);
						result.put("msg", queryNUser.get(0).get("username")+" is not in your group.");
						return result;
					}
				}else {
					result.put("result", false);
					result.put("msg", queryNUser.get(0).get("username")+"  is not registered in the system.");
					return result;
				}
			}else {
				result.put("result", false);
				result.put("msg", labname+" information cannot be obtained.");
				return result;
			}
		}
		return result;
	}
	//通过labname获取lab的运行状态（内存中的状态）
	@RequestMapping(path = "/testStatus")
	@ResponseBody
	public Map<String, Object> getLabStatus(String labname){
		Map<String, Object> result = new HashMap<>();
		JSONArray Servers = CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock, true, null);
		//result.put("Servers", Servers);
		//List setList = new ArrayList<>();
		Set sets = new HashSet<>();
		if(Servers.size()>0) {
			for (int i =0 ;i<Servers.size();i++) {
				JSONObject lab = Servers.getJSONObject(i).getJSONObject(Constant.LAB);
				String status ="";
				try {
					status = lab.getString("status");
					logger.info(status);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
		return result;
	}
	@RequestMapping(path = "/installLab")
	@ResponseBody
	public Map<String, Object> installLab(HttpSession session,HttpServletRequest request) throws Exception{
		
		
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		
		String aservername = request.getParameter("aservername")==null?"":request.getParameter("aservername").toString().trim();
		String arelease = request.getParameter("arelease")==null?"":request.getParameter("arelease").toString().trim();
		String aprotocol = request.getParameter("aprotocol")==null?"":request.getParameter("aprotocol").toString().trim();
		/*String aservertype = request.getParameter("aservertype")==null?"":request.getParameter("aservertype").toString().trim();
		String amatetype = request.getParameter("amatetype")==null?"":request.getParameter("amatetype").toString().trim();*/
		String hdept = request.getParameter("hdept")==null?"":request.getParameter("hdept").toString().trim();
		String ainsflag = request.getParameter("ainsflag")==null?"":request.getParameter("ainsflag").toString().trim();
		String sspa = request.getParameter("sspa")==null?"":request.getParameter("sspa").toString().trim();
		String sdb = request.getParameter("sdb")==null?"":request.getParameter("sdb").toString().trim();
		String sset = request.getParameter("sset")==null?"":request.getParameter("sset").toString().trim();
		
		/*
		部门添加权限验证：
		
		   	super用户
			通过 labname 查 用户
				1、用户在系统内，查询部门，与传入的部门id比对
				2、用户不在系统内，返回失败
			
			admin用户
				不限制
			
		 */
		Subject subject = SecurityUtils.getSubject();  
		boolean isPermitted = subject.isPermitted("lab:create");
		boolean hasAdminRole = subject.hasRole("admin");
		//如果是超级管理员，不做校验
		if(!hasAdminRole) {
			if(!isPermitted) {
				result.put("result", "fail");
				result.put("msg", "Sorry ,You have no authority to operate the business, please contact the super administrator.");
				return result;
			}else {
				Map<String, Object> getlabGroupFlag = getlabGroupFlag(aservername,hdept);
				if(!(boolean)getlabGroupFlag.get("result")) {
					result.put("result", "fail");
					result.put("msg", getlabGroupFlag.get("msg"));
					return result;
				}
			}
		}
        
        /*
         安装前进行check
			如果lab正在运行（running）则不进行安装，返回失败
         */
		Map<String, Object> labStatus = getLabStatus(aservername);
        
		String db = StringUtil.formatJsonString(sdb);
		String spa = StringUtil.formatJsonString(sspa);
		
		String reqData = "{\"protocol\": \""+aprotocol+"\", "
        		+ "\"labname\": [\""+aservername+"\"], "
        		+ "\"DB\": "+db+", "
        		+ "\"mate\": \"N\", "
        		+ "\"release\": \""+arelease+"\", "
        		+ "\"SPA\": "+spa+", "
        		+ "\"ins_flag\": \""+ainsflag+"\"}";
		
		//logger.info(reqData);
		/*
		{
			"protocol": "ITU",
			"labname": ["CHSP12B"],
			"DB": ["SIMDB","ACMDB"],
			"mate": "N",
			"release": "SP18.9",
			"SPA": ["DROUTER","ENWTPPS","EPAY","EPPSA","EPPSM","NWTCOM","NWTGSM","DIAMCL"],
			"ins_flag": "0"
		}
		*/
		
		String resResult = HttpReq.reqUrl("http://135.251.249.124:9333/spadm/default/certapi/certtask.json", reqData);
		//String resResult = "OK";
		logger.info("installLab  >> resResult  >>  "+resResult);
		if("OK".equals(resResult)) {
			//logger.info("1");
			result.put("result", "success");
			result.put("msg", "Congratulations, Installation is underway, please check the log after 10 seconds.");
			//开启线程，检测状态，等到lab安装完成返回success时genclient
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					//logger.info("2");
					String addLabFlag="false";//用于添加lab动作记录到数据库
					int i = 0;
					int count = 0;
					long createtime=new Date().getTime();
					String createid ="";
					String deptid ="";
					String username = session.getAttribute("login").toString();
					NUser user = new NUser();
					user.setUsername(username);
					ArrayList<HashMap<String, Object>> queryNUser =  new ArrayList<HashMap<String, Object>>();
					try {
						queryNUser = loginService.queryNUser(user);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(queryNUser.size()>0) {
						createid = (String) queryNUser.get(0).get("id");
						deptid = (String) queryNUser.get(0).get("deptid");
					}
					while(true) {
						try {
							//等待10s
							Thread.sleep(1000*10);
							
							//Thread.sleep(1000*10);
							Date date = new Date();
							SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 
							logger.info(df.format(date)+ " >>  installLab >> "+aservername+" thread "+count);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						if(i>50) {
							//genClient.sh异常10次跳出循环
							break;
						}
						JSONObject reqUrl = HttpReq.reqUrl("http://135.251.249.124:9333/spadm/default/labapi/dailylab/"+aservername+".json");
						if(!reqUrl.isEmpty()) {
							JSONArray jsonArray = reqUrl.getJSONArray("content");
							if(jsonArray.size()>0) {
								
								String labname = jsonArray.getJSONObject(0).getString("labname");
								String status = jsonArray.getJSONObject(0).getString("status");
								//String status = "Succeed";
								String ips = jsonArray.getJSONObject(0).getString("ips");
								String ip ="";
								if(ips.contains(",")) {
									String[] ipss = ips.split(",");
									ip=ipss[0];
								}
								//String ss7 = jsonArray.getJSONObject(0).getString("ss7");
								//String enwtpps = jsonArray.getJSONObject(0).getString("enwtpps");
								String ss7 = aprotocol;
								String enwtpps = arelease;
								String log = jsonArray.getJSONObject(0).getString("log");
								String free = jsonArray.getJSONObject(0).getString("free");
								String ptversion = jsonArray.getJSONObject(0).getString("log");
								
								if(addLabFlag=="false") {	
									String labStatus = serverInfoService.addLabStatus(status,"new","",enwtpps,ss7,labname,db,free,ips,ptversion,spa,deptid,createid,createtime+"",ainsflag);
									//labStatus 返回success表示记录到数据库内成功，返回fail则表示添加到数据库失败<因为在线程中，所以暂时没啥用>
									//只添加一次
									addLabFlag="true";
								}
								if("Installing".equals(status) ) {
									SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 
									logger.info(df.format(new Date())+"  >>  Installing  >>"+i); 
									/*JSONArray infos = CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock, true, null);
									infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21F\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverTpye\": \"G\",\"serverMate\": \"S\",\"mateServer\": \"BJRMS21E\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
									for(int i=0; i<infos.size(); i++){
										CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock,false,infos.getJSONObject(i).getJSONObject(Constant.BODY));
									}*/
								}else if ("Succeed".equals(status)) {
									try {
										serverInfoService.editLabStatus(status,log,labname,new Date().getTime()+"",createtime+"");
										logger.info("cd /home/huanglei && ./genClient.sh "+labname+" "+ip+" "+enwtpps+" "+ss7+" "+sset+" "+deptid);
										logger.info("cd /home/huanglei && ./genClient.sh "+labname+" "+ip+" "+enwtpps+" "+ss7+" "+sset+" "+deptid);
										Exec("cd /home/huanglei && ./genClient.sh "+labname+" "+ip+" "+enwtpps+" "+ss7+" "+sset+" "+deptid);
										break;
									} catch (Exception e) {
										e.printStackTrace();
										i++;
										continue;
									}
								}else if ("Failed".equals(status)) {
									//如果失败，则直接跟新记录表，跳出循环
									try {
										serverInfoService.editLabStatus(status,log,labname,new Date().getTime()+"",createtime+"");
										break;
									} catch (Exception e) {
										e.printStackTrace();
										i++;
										continue;
									}
								}
							}
							
						}else {
							continue;
						}
						count++;
						try {
							//等待10分钟
							Thread.sleep(1000*60*10);
							logger.info("10m >> installLab >> "+aservername+" thread "+count);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
					
				}
			});
			thread.start();
		}
		if("BAD".equals(resResult)) {
			result.put("result", "fail");
			result.put("msg", "Do not accept installation requests, please contact the super administrator.");
		}
		return result;
	}
	public static String Exec(String cmd) throws Exception {
		String[] cmds = new String[] { "/bin/sh", "-c", cmd };
        Process ps = Runtime.getRuntime().exec(cmds);
        BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        String genClientLog = sb.toString();
        return genClientLog;
	}
	@RequestMapping(path = "/addServerDetails")
	public void addServerDetails(HttpSession session,NServer server) throws Exception{
		
		//检测角色 >>  /addServerInfo   roles[spuer,admin]
		//insert into n_permission_url(permission_name,url,type,remark) values('authc,roles["admin","super"]','/addServerInfo.do','menu','新增lab超级用户以上权限');		
		
		String useName = session.getAttribute("login").toString();
		NUser nuser = new NUser();
		nuser.setUsername(useName);
		ArrayList<HashMap<String, Object>> queryNUser = loginService.queryNUser(nuser);
		int id = Integer.parseInt(queryNUser.get(0).get("id").toString());
		int deptId = Integer.parseInt(queryNUser.get(0).get("deptid").toString());
		server.setCreateUserId(id);
		server.setCreateDeptId(deptId);
		//serverInfoService.addServerDetails(server);
		//添加server
		JSONObject reqdate = new JSONObject();
		reqdate.put("ins_flag", "0");
		reqdate.put("protocol", server.getServerProtocol());
		String labname = StringUtil.formatJsonString(server.getServerName());
		reqdate.put("labname", labname);
		String serverRTDB = server.getServerRTDB();
		String DB = StringUtil.formatJsonString(serverRTDB);
		reqdate.put("DB", DB);
		reqdate.put("mate", "N");
		reqdate.put("release", server.getServerRelease());
		String serverSPA = server.getServerSPA();
		String SPA = StringUtil.formatJsonString(serverSPA);
		reqdate.put("SPA", SPA);
		
		//{"ins_flag":"0","protocol":"ANSI","labname":["BJRMS21H"],"DB":["CTRTDB","XBRTDB","GNRTDB","SGLDB","SGLRTDB","SIMDB"],"mate":"N","release":"SP17.9","SPA":["DROUTER","ENWTPPS","EPAY","EPPSA","EPPSM","NWTCOM","NWTGSM","ECGS"]}
		logger.info("addServerDetails >> reqdate >> "+reqdate);
        String reqData = "{\"protocol\": \""+server.getServerProtocol()+"\", "
		        		+ "\"labname\": [\""+server.getServerName()+"\"], "
		        		+ "\"DB\": "+DB+", "
		        		+ "\"mate\": \"N\", "
		        		+ "\"release\": \""+server.getServerRelease()+"\", "
		        		+ "\"SPA\": "+SPA+", "
		        		+ "\"ins_flag\": \"0\"}";

        logger.info("addServerDetails >> reqData >> "+reqData);
        String resResult = HttpReq.reqUrl("http://135.251.249.124:9333/spadm/default/certapi/certtask.json", reqData);
        //String resResult = HttpReq.reqUrl("http://135.251.249.124:9333/spadm/default/certapi/certtask.json", reqdate.toString());
        logger.info("addServerInfo >> resResult >> "+resResult);
        
        
	}
	
	//定义一个方法：传入lab名字和用户的deptid，取判断是否可以使用该lab-->已经有方法完成此功能
	/*public Map<String, Object> isCanUseLab(String labname,String deptid) {
		Map<String, Object> result = new HashMap<String, Object>();
		if(labname==null||"".equals(labname)) {
			result.put("result", "false");
			result.put("msg", "labname is require.");
			return result;
		}
		if(deptid==null||"".equals(deptid)) {
			result.put("result", "false");
			result.put("msg", "deptid is require.");
			return result;
		}
		JSONObject reqUrl = HttpReq.reqUrl("http://135.251.249.124:9333/spadm/default/labapi/kvmlabusage/"+labname+".json");
		//http://135.251.249.124:9333/spadm/default/labapi/kvmlabusage/BJVM12B.json
		//"labuser": "Yang PAN",
		if(!reqUrl.isEmpty()) {
			JSONArray jsonArray = reqUrl.getJSONArray("content");
			if(jsonArray.size()>0) {
				String labuser = jsonArray.getJSONObject(0).getString("labuser");
				
			}else {
				result.put("result", "false");
				result.put("msg", "No lab was found.");
				return result;
			}
		}
		return result;
	}*/

	@RequestMapping(path = "/removeServerInfo")
	public void removeServerInfo(Model model, HttpSession session, String condition,  PrintWriter out) throws Exception{
		out.write(serverInfoService.removeServerInfo(condition));
	}
	
	@RequestMapping(path = "/removeServerDetails")
	public void removeServerDetails(NServer server) throws Exception{
		serverInfoService.addServerDetails(server);
	}
	
	@RequestMapping(path = "/updateServerInfo")
	public String updateServerInfo(String model) throws Exception{
		return "addServerInfo";
	}
	
	@RequestMapping(path = "/cancel")
	public void cancel(Model model, HttpSession session, String condition,  PrintWriter out) throws Exception{
		out.write(serverInfoService.cancel(condition));
	}
	
	public ServerInfoService getServerInfoService() {
		return serverInfoService;
	}

	public void setServerInfoService(ServerInfoService serverInfoService) {
		this.serverInfoService = serverInfoService;
	}
}
