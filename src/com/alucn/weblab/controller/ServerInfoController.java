package com.alucn.weblab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.weblab.model.Server;
import com.alucn.weblab.service.ServerInfoService;
import com.alucn.weblab.service.SpaAndRtdbManService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author haiqiw
 * 2017年6月2日 下午5:39:08
 * desc: serverinfo
 */
@Controller
//@RequestMapping("/serverInfo")
public class ServerInfoController {
	
	@Autowired
	private ServerInfoService serverInfoService;
	@Autowired(required=true)
	private SpaAndRtdbManService spaAndRtdbManService;
	
	@RequestMapping(path = "/getServerInfo")
	public String getServerInfo(Model model){
		JSONArray infos = serverInfoService.getServerInfo();
		model.addAttribute("infos", infos);
		return "serverInfo";
	}

	@RequestMapping(path = "/getServerDetails")
	public String getServerDetails(String serverName, Model model){
		JSONArray infos = serverInfoService.getServerInfo();
		for(int i=0; i<infos.size(); i++){
			JSONObject info = infos.getJSONObject(i);
			if(serverName.equals(info.getJSONObject(Constant.LAB).getString(Constant.SERVERNAME))){
				model.addAttribute("info", info);
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
	public String removeServerInfo(String model) throws Exception{
		return "addServerInfo";
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
	public String cancel(String model) throws Exception{
		return "addServerInfo";
	}
	
	public ServerInfoService getServerInfoService() {
		return serverInfoService;
	}

	public void setServerInfoService(ServerInfoService serverInfoService) {
		this.serverInfoService = serverInfoService;
	}

}
