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
		//System.err.println(infos);
		//{set1=[ServerSort [map={BJRMS21A={"lab":{"serverName":"BJRMS21A","serverIp":"135.242.17.206","serverRelease":"SP17.9","serverProtocol":"ITU","serverType":"Line","serverMate":"Standalone","mateServer":"N","setName":"set1","serverSPA":["AethosTest","CDRPP311","CDRPPGW311","DIAMCL179","DROUTER179","ECTRL179","ENWTPPS179","EPAY179","EPPSA179","EPPSM179","GATEWAY179","NWTCOM111","NWTGSM066"],"serverRTDB":["SCRRTDBV7","AECIDB179","SGLDB28H","TIDDB28C","GPRSSIM08","AIRTDB179","CTRTDB179","HTIDDB179","PMOUDB179","PROMDB179","SIMDB179","SYDB179","GCIPL312","VTXDB179","SHRTDB28F","CDBRTDB","RCNRDB173","HMRTDB173","SESSDB311","ACMDB104","SIMIDXDB","FSNDB173","UARTDB287","RERTDB279","SFFDB28C","GCURDB","SLTBLRTDB","ID2MDN01","GTMDB28A"]},"taskStatus":{"status":"Idle","runningCase":""},"taskResult":{"success":[],"fail":[]}}}], ServerSort [map={BJRMS21B={"lab":{"serverName":"BJRMS21B","serverIp":"135.242.17.206","serverRelease":"SP17.9","serverProtocol":"ITU","serverType":"Group","serverMate":"Standalone","mateServer":"N","setName":"set1","serverSPA":["AethosTest","CDRPP311","CDRPPGW311","DIAMCL179","DROUTER179","ECTRL179","ENWTPPS179","EPAY179","EPPSA179","EPPSM179","GATEWAY179","NWTCOM111","NWTGSM066"],"serverRTDB":["SCRRTDBV7","AECIDB179","SGLDB28H","TIDDB28C","GPRSSIM08","AIRTDB179","CTRTDB179","HTIDDB179","PMOUDB179","PROMDB179","SIMDB179","SYDB179","GCIPL312","VTXDB179","SHRTDB28F","CDBRTDB","RCNRDB173","HMRTDB173","SESSDB311","ACMDB104","SIMIDXDB","FSNDB173","UARTDB287","RERTDB279","SFFDB28C","GCURDB","SLTBLRTDB","ID2MDN01","GTMDB28A"]},"taskStatus":{"status":"Idle","runningCase":""},"taskResult":{"success":[],"fail":[]}}}], ServerSort [map={BJRMS21C={"lab":{"serverName":"BJRMS21C","serverIp":"135.242.17.206","serverRelease":"SP17.9","serverProtocol":"ITU","serverType":"Line","serverMate":"Primary","mateServer":"BJRMS21D","setName":"set1","serverSPA":["AethosTest","CDRPP311","CDRPPGW311","DIAMCL179","DROUTER179","ECTRL179","ENWTPPS179","EPAY179","EPPSA179","EPPSM179","GATEWAY179","NWTCOM111","NWTGSM066"],"serverRTDB":["SCRRTDBV7","AECIDB179","SGLDB28H","TIDDB28C","GPRSSIM08","AIRTDB179","CTRTDB179","HTIDDB179","PMOUDB179","PROMDB179","SIMDB179","SYDB179","GCIPL312","VTXDB179","SHRTDB28F","CDBRTDB","RCNRDB173","HMRTDB173","SESSDB311","ACMDB104","SIMIDXDB","FSNDB173","UARTDB287","RERTDB279","SFFDB28C","GCURDB","SLTBLRTDB","ID2MDN01","GTMDB28A"]},"taskStatus":{"status":"Idle","runningCase":""},"taskResult":{"success":[],"fail":[]}}, BJRMS21D={"lab":{"serverName":"BJRMS21D","serverIp":"135.242.17.206","serverRelease":"SP17.9","serverProtocol":"ITU","serverType":"Line","serverMate":"Secondary","mateServer":"BJRMS21C","setName":"set1","serverSPA":["AethosTest","CDRPP311","CDRPPGW311","DIAMCL179","DROUTER179","ECTRL179","ENWTPPS179","EPAY179","EPPSA179","EPPSM179","GATEWAY179","NWTCOM111","NWTGSM066"],"serverRTDB":["SCRRTDBV7","AECIDB179","SGLDB28H","TIDDB28C","GPRSSIM08","AIRTDB179","CTRTDB179","HTIDDB179","PMOUDB179","PROMDB179","SIMDB179","SYDB179","GCIPL312","VTXDB179","SHRTDB28F","CDBRTDB","RCNRDB173","HMRTDB173","SESSDB311","ACMDB104","SIMIDXDB","FSNDB173","UARTDB287","RERTDB279","SFFDB28C","GCURDB","SLTBLRTDB","ID2MDN01","GTMDB28A"]},"taskStatus":{"status":"Idle","runningCase":""},"taskResult":{"success":[],"fail":[]}}}], ServerSort [map={BJRMS21E={"lab":{"serverName":"BJRMS21E","serverIp":"135.242.17.206","serverRelease":"SP17.9","serverProtocol":"ITU","serverType":"Group","serverMate":"Primary","mateServer":"BJRMS21F","setName":"set1","serverSPA":["AethosTest","CDRPP311","CDRPPGW311","DIAMCL179","DROUTER179","ECTRL179","ENWTPPS179","EPAY179","EPPSA179","EPPSM179","GATEWAY179","NWTCOM111","NWTGSM066"],"serverRTDB":["SCRRTDBV7","AECIDB179","SGLDB28H","TIDDB28C","GPRSSIM08","AIRTDB179","CTRTDB179","HTIDDB179","PMOUDB179","PROMDB179","SIMDB179","SYDB179","GCIPL312","VTXDB179","SHRTDB28F","CDBRTDB","RCNRDB173","HMRTDB173","SESSDB311","ACMDB104","SIMIDXDB","FSNDB173","UARTDB287","RERTDB279","SFFDB28C","GCURDB","SLTBLRTDB","ID2MDN01","GTMDB28A"]},"taskStatus":{"status":"Idle","runningCase":""},"taskResult":{"success":[],"fail":[]}}, BJRMS21F={"lab":{"serverName":"BJRMS21F","serverIp":"135.242.17.206","serverRelease":"SP17.9","serverProtocol":"ITU","serverType":"Group","serverMate":"Secondary","mateServer":"BJRMS21E","setName":"set1","serverSPA":["AethosTest","CDRPP311","CDRPPGW311","DIAMCL179","DROUTER179","ECTRL179","ENWTPPS179","EPAY179","EPPSA179","EPPSM179","GATEWAY179","NWTCOM111","NWTGSM066"],"serverRTDB":["SCRRTDBV7","AECIDB179","SGLDB28H","TIDDB28C","GPRSSIM08","AIRTDB179","CTRTDB179","HTIDDB179","PMOUDB179","PROMDB179","SIMDB179","SYDB179","GCIPL312","VTXDB179","SHRTDB28F","CDBRTDB","RCNRDB173","HMRTDB173","SESSDB311","ACMDB104","SIMIDXDB","FSNDB173","UARTDB287","RERTDB279","SFFDB28C","GCURDB","SLTBLRTDB","ID2MDN01","GTMDB28A"]},"taskStatus":{"status":"Idle","runningCase":""},"taskResult":{"success":[],"fail":[]}}}]]}
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
