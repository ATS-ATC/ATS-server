package com.alucn.weblab.controller;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alucn.casemanager.server.common.model.ServerSort;
import com.alucn.weblab.model.Server;
import com.alucn.weblab.service.ServerInfoService;
import com.alucn.weblab.service.SpaAndRtdbManService;

import net.sf.json.JSONObject;

/**
 * @author haiqiw
 * desc: serverinfo
 */
@Controller
//@RequestMapping("/serverInfo")
public class ServerInfoController {
	
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
	
	@RequestMapping(path = "/getServerInfo")
	public String getServerInfo(Model model){
		Map<String,Set<ServerSort>> infos = serverInfoService.getServerInfo();
		model.addAttribute("infos", infos);
		return "serverInfo";
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
	public void addServerDetails(Server server) throws Exception{
		serverInfoService.addServerDetails(server);
	}

	@RequestMapping(path = "/removeServerInfo")
	public void removeServerInfo(Model model, HttpSession session, String condition,  PrintWriter out) throws Exception{
		out.write(serverInfoService.removeServerInfo(condition));
	}
	
	@RequestMapping(path = "/removeServerDetails")
	public void removeServerDetails(Server server) throws Exception{
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
