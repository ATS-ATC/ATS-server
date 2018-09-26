package com.alucn.weblab.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.tools.ant.taskdefs.Get;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite.EmptyWithAstNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alucn.casemanager.server.common.model.ServerSort;
import com.alucn.weblab.model.NServer;
import com.alucn.weblab.model.NUser;
import com.alucn.weblab.model.Server;
import com.alucn.weblab.service.LoginService;
import com.alucn.weblab.service.ServerInfoService;
import com.alucn.weblab.service.SpaAndRtdbManService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author haiqiw
 * desc: serverinfo
 */
@Controller
//@RequestMapping("/serverInfo")
public class ServerInfoController {
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
	
	@RequestMapping(value="/getServerInfoJson")
	@ResponseBody
	public ArrayList<HashMap<String, Object>> getServerInfoJson(){
		
		ArrayList<HashMap<String, Object>> resultList = new ArrayList<HashMap<String, Object>>();
		
		Map<String,Set<ServerSort>> infos = serverInfoService.getServerInfo();
		System.err.println("infos:==============="+infos);
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
					System.err.println("===============");
					System.err.println(jsonObject);
					//{"lab":{"serverName":"BJRMS21C","serverIp":"135.242.17.206","serverRelease":"SP17.9","serverProtocol":"ITU","serverType":"Line","serverMate":"Primary","mateServer":"BJRMS21D","setName":"set2","serverSPA":["AethosTest","CDRPP311","CDRPPGW311","DIAMCL179","DROUTER179","ECTRL179","ENWTPPS179","EPAY179","EPPSA179","EPPSM179","GATEWAY179","NWTCOM111","NWTGSM066"],"serverRTDB":["SCRRTDBV7","AECIDB179","SGLDB28H","TIDDB28C","GPRSSIM08","AIRTDB179","CTRTDB179","HTIDDB179","PMOUDB179","PROMDB179","SIMDB179","SYDB179","GCIPL312","VTXDB179","SHRTDB28F","CDBRTDB","RCNRDB173","HMRTDB173","SESSDB311","ACMDB104","SIMIDXDB","FSNDB173","UARTDB287","RERTDB279","SFFDB28C","GCURDB","SLTBLRTDB","ID2MDN01","GTMDB28A"]},"taskStatus":{"status":"Idle","runningCase":""},"taskResult":{"success":[],"fail":[]}}
					//{"lab":{"serverName":"BJRMS21D","serverIp":"135.242.17.206","serverRelease":"SP17.9","serverProtocol":"ITU","serverType":"Line","serverMate":"Secondary","mateServer":"BJRMS21C","setName":"set2","serverSPA":["AethosTest","CDRPP311","CDRPPGW311","DIAMCL179","DROUTER179","ECTRL179","ENWTPPS179","EPAY179","EPPSA179","EPPSM179","GATEWAY179","NWTCOM111","NWTGSM066"],"serverRTDB":["SCRRTDBV7","AECIDB179","SGLDB28H","TIDDB28C","GPRSSIM08","AIRTDB179","CTRTDB179","HTIDDB179","PMOUDB179","PROMDB179","SIMDB179","SYDB179","GCIPL312","VTXDB179","SHRTDB28F","CDBRTDB","RCNRDB173","HMRTDB173","SESSDB311","ACMDB104","SIMIDXDB","FSNDB173","UARTDB287","RERTDB279","SFFDB28C","GCURDB","SLTBLRTDB","ID2MDN01","GTMDB28A"]},"taskStatus":{"status":"Idle","runningCase":""},"taskResult":{"success":[],"fail":[]}}
					//"taskStatus":{"status":"Idle","runningCase":""}
					JSONObject lab = jsonObject.getJSONObject("lab");
					System.err.println(lab);
					System.err.println("===============");
					String serverName = lab.getString("serverName");
					String serverIp = lab.getString("serverIp");
					String serverRelease = lab.getString("serverRelease");
					String serverProtocol = lab.getString("serverProtocol");
					String serverType = lab.getString("serverType");
					String serverMate = lab.getString("serverMate");
					String mateServer = lab.getString("mateServer");
					String status = jsonObject.getJSONObject("taskStatus").getString("status");
					int stateflag = 1;
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
					}
					childMap.put("id", id);
					childMap.put("pid", pid);
					childMap.put("name", serverName);
					childMap.put("status", stateflag);
					childMap.put("type", "server");
					childMap.put("serverIp", serverIp);
					childMap.put("serverRelease", serverRelease);
					childMap.put("serverProtocol", serverProtocol);
					childMap.put("serverType", serverType);
					childMap.put("serverMate", serverMate);
					childMap.put("mateServer", mateServer);
					
					resultList.add(childMap);
					System.err.println("resultList:========"+resultList);
					id++;
				}
			}
			
		}
		
		return resultList;
	}
	
	
	
	@RequestMapping(path = "/getServerInfo")
	public String getServerInfo(Model model){
		
		//2
		//Map<String,Set<ServerSort>> infos = serverInfoService.getServerInfo();
		/*JSONArray jsonTree = new JSONArray();
		for (String info : infos.keySet()) {
			JSONObject value = new JSONObject();
			value.put("text", info);
			Set<ServerSort> sets = infos.get(info);
			System.err.println(sets);
			JSONArray childTree = new JSONArray();
			for (ServerSort set : sets) {
				JSONObject child = new JSONObject();
				Map<String, JSONObject> maps = set.getMap();
				//System.err.println(maps);
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
		
		
		
		//System.err.println(infos);
		//{set1=[ServerSort [map={BJRMS21A={"lab":{"serverName":"BJRMS21A","serverIp":"135.242.17.206","serverRelease":"SP17.9","serverProtocol":"ITU","serverType":"Line","serverMate":"Standalone","mateServer":"N","setName":"set1","serverSPA":["AethosTest","CDRPP311","CDRPPGW311","DIAMCL179","DROUTER179","ECTRL179","ENWTPPS179","EPAY179","EPPSA179","EPPSM179","GATEWAY179","NWTCOM111","NWTGSM066"],"serverRTDB":["SCRRTDBV7","AECIDB179","SGLDB28H","TIDDB28C","GPRSSIM08","AIRTDB179","CTRTDB179","HTIDDB179","PMOUDB179","PROMDB179","SIMDB179","SYDB179","GCIPL312","VTXDB179","SHRTDB28F","CDBRTDB","RCNRDB173","HMRTDB173","SESSDB311","ACMDB104","SIMIDXDB","FSNDB173","UARTDB287","RERTDB279","SFFDB28C","GCURDB","SLTBLRTDB","ID2MDN01","GTMDB28A"]},"taskStatus":{"status":"Idle","runningCase":""},"taskResult":{"success":[],"fail":[]}}}], ServerSort [map={BJRMS21B={"lab":{"serverName":"BJRMS21B","serverIp":"135.242.17.206","serverRelease":"SP17.9","serverProtocol":"ITU","serverType":"Group","serverMate":"Standalone","mateServer":"N","setName":"set1","serverSPA":["AethosTest","CDRPP311","CDRPPGW311","DIAMCL179","DROUTER179","ECTRL179","ENWTPPS179","EPAY179","EPPSA179","EPPSM179","GATEWAY179","NWTCOM111","NWTGSM066"],"serverRTDB":["SCRRTDBV7","AECIDB179","SGLDB28H","TIDDB28C","GPRSSIM08","AIRTDB179","CTRTDB179","HTIDDB179","PMOUDB179","PROMDB179","SIMDB179","SYDB179","GCIPL312","VTXDB179","SHRTDB28F","CDBRTDB","RCNRDB173","HMRTDB173","SESSDB311","ACMDB104","SIMIDXDB","FSNDB173","UARTDB287","RERTDB279","SFFDB28C","GCURDB","SLTBLRTDB","ID2MDN01","GTMDB28A"]},"taskStatus":{"status":"Idle","runningCase":""},"taskResult":{"success":[],"fail":[]}}}], ServerSort [map={BJRMS21C={"lab":{"serverName":"BJRMS21C","serverIp":"135.242.17.206","serverRelease":"SP17.9","serverProtocol":"ITU","serverType":"Line","serverMate":"Primary","mateServer":"BJRMS21D","setName":"set1","serverSPA":["AethosTest","CDRPP311","CDRPPGW311","DIAMCL179","DROUTER179","ECTRL179","ENWTPPS179","EPAY179","EPPSA179","EPPSM179","GATEWAY179","NWTCOM111","NWTGSM066"],"serverRTDB":["SCRRTDBV7","AECIDB179","SGLDB28H","TIDDB28C","GPRSSIM08","AIRTDB179","CTRTDB179","HTIDDB179","PMOUDB179","PROMDB179","SIMDB179","SYDB179","GCIPL312","VTXDB179","SHRTDB28F","CDBRTDB","RCNRDB173","HMRTDB173","SESSDB311","ACMDB104","SIMIDXDB","FSNDB173","UARTDB287","RERTDB279","SFFDB28C","GCURDB","SLTBLRTDB","ID2MDN01","GTMDB28A"]},"taskStatus":{"status":"Idle","runningCase":""},"taskResult":{"success":[],"fail":[]}}, BJRMS21D={"lab":{"serverName":"BJRMS21D","serverIp":"135.242.17.206","serverRelease":"SP17.9","serverProtocol":"ITU","serverType":"Line","serverMate":"Secondary","mateServer":"BJRMS21C","setName":"set1","serverSPA":["AethosTest","CDRPP311","CDRPPGW311","DIAMCL179","DROUTER179","ECTRL179","ENWTPPS179","EPAY179","EPPSA179","EPPSM179","GATEWAY179","NWTCOM111","NWTGSM066"],"serverRTDB":["SCRRTDBV7","AECIDB179","SGLDB28H","TIDDB28C","GPRSSIM08","AIRTDB179","CTRTDB179","HTIDDB179","PMOUDB179","PROMDB179","SIMDB179","SYDB179","GCIPL312","VTXDB179","SHRTDB28F","CDBRTDB","RCNRDB173","HMRTDB173","SESSDB311","ACMDB104","SIMIDXDB","FSNDB173","UARTDB287","RERTDB279","SFFDB28C","GCURDB","SLTBLRTDB","ID2MDN01","GTMDB28A"]},"taskStatus":{"status":"Idle","runningCase":""},"taskResult":{"success":[],"fail":[]}}}], ServerSort [map={BJRMS21E={"lab":{"serverName":"BJRMS21E","serverIp":"135.242.17.206","serverRelease":"SP17.9","serverProtocol":"ITU","serverType":"Group","serverMate":"Primary","mateServer":"BJRMS21F","setName":"set1","serverSPA":["AethosTest","CDRPP311","CDRPPGW311","DIAMCL179","DROUTER179","ECTRL179","ENWTPPS179","EPAY179","EPPSA179","EPPSM179","GATEWAY179","NWTCOM111","NWTGSM066"],"serverRTDB":["SCRRTDBV7","AECIDB179","SGLDB28H","TIDDB28C","GPRSSIM08","AIRTDB179","CTRTDB179","HTIDDB179","PMOUDB179","PROMDB179","SIMDB179","SYDB179","GCIPL312","VTXDB179","SHRTDB28F","CDBRTDB","RCNRDB173","HMRTDB173","SESSDB311","ACMDB104","SIMIDXDB","FSNDB173","UARTDB287","RERTDB279","SFFDB28C","GCURDB","SLTBLRTDB","ID2MDN01","GTMDB28A"]},"taskStatus":{"status":"Idle","runningCase":""},"taskResult":{"success":[],"fail":[]}}, BJRMS21F={"lab":{"serverName":"BJRMS21F","serverIp":"135.242.17.206","serverRelease":"SP17.9","serverProtocol":"ITU","serverType":"Group","serverMate":"Secondary","mateServer":"BJRMS21E","setName":"set1","serverSPA":["AethosTest","CDRPP311","CDRPPGW311","DIAMCL179","DROUTER179","ECTRL179","ENWTPPS179","EPAY179","EPPSA179","EPPSM179","GATEWAY179","NWTCOM111","NWTGSM066"],"serverRTDB":["SCRRTDBV7","AECIDB179","SGLDB28H","TIDDB28C","GPRSSIM08","AIRTDB179","CTRTDB179","HTIDDB179","PMOUDB179","PROMDB179","SIMDB179","SYDB179","GCIPL312","VTXDB179","SHRTDB28F","CDBRTDB","RCNRDB173","HMRTDB173","SESSDB311","ACMDB104","SIMIDXDB","FSNDB173","UARTDB287","RERTDB279","SFFDB28C","GCURDB","SLTBLRTDB","ID2MDN01","GTMDB28A"]},"taskStatus":{"status":"Idle","runningCase":""},"taskResult":{"success":[],"fail":[]}}}]]}
		return "serverInfo3";
	}
	/*@RequestMapping(path = "/testGetServerInfo")
	public String test(Model model){
		Map<String,Set<ServerSort>> infos = serverInfoService.getServerInfo();
		System.out.println("-----------------------infos------------------------------");
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
	
	@RequestMapping(path = "/addServerInfo")
	public String addServerInfo(Model model) throws Exception{
		model.addAllAttributes(spaAndRtdbManService.getSpaAndRtdbInfo());
		return "addServerInfo";
	}
	
	@RequestMapping(path = "/addServerDetails")
	public void addServerDetails(HttpSession session,NServer server) throws Exception{
		String useName = session.getAttribute("login").toString();
		NUser nuser = new NUser();
		nuser.setUsername(useName);
		ArrayList<HashMap<String, Object>> queryNUser = loginService.queryNUser(nuser);
		int id = Integer.parseInt(queryNUser.get(0).get("id").toString());
		int deptId = Integer.parseInt(queryNUser.get(0).get("deptid").toString());
		server.setCreateUserId(id);
		server.setCreateDeptId(deptId);
		serverInfoService.addServerDetails(server);
	}

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
