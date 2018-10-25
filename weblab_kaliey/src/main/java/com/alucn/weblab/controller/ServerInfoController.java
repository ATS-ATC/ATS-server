package com.alucn.weblab.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.tools.ant.taskdefs.Exec;
import org.apache.tools.ant.taskdefs.Get;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite.EmptyWithAstNode;
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
import com.alucn.weblab.model.Server;
import com.alucn.weblab.service.LoginService;
import com.alucn.weblab.service.ServerInfoService;
import com.alucn.weblab.service.SpaAndRtdbManService;
import com.alucn.weblab.utils.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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
					
					resultList.add(childMap);
					System.err.println("resultList:========"+resultList);
					id++;
				}
			}
			
		}
		
		return resultList;
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
		
//		System.err.println("aservername:"+aservername);
//		System.err.println("arelease:"+arelease);
//		System.err.println("aprotocol:"+aprotocol);
//		System.err.println("aservertype:"+aservertype);
//		System.err.println("amatetype:"+amatetype);
//		System.err.println("hdept:"+hdept);
//		System.err.println("ainsflag:"+ainsflag);
//		System.err.println("sspa:"+sspa);
//		System.err.println("sdb:"+sdb);
		String db = StringUtil.formatJsonString(sdb);
		String spa = StringUtil.formatJsonString(sspa);
		
		String reqData = "{\"protocol\": \""+aprotocol+"\", "
        		+ "\"labname\": [\""+aservername+"\"], "
        		+ "\"DB\": "+db+", "
        		+ "\"mate\": \"N\", "
        		+ "\"release\": \""+arelease+"\", "
        		+ "\"SPA\": "+spa+", "
        		+ "\"ins_flag\": \""+ainsflag+"\"}";
		
		//System.err.println(reqData);
		/*
		{
			"protocol": "ANSI",
			"labname": ["CHSP12B"],
			"DB": ["SGLRTDB","SCRRTDB"],
			"mate": "N",
			"release": "SP18.9",
			"SPA": ["EPAY","EPPSA"],
			"ins_flag": "0"
		}
		*/
		
		String resResult = HttpReq.reqUrl("http://135.251.249.124:9333/spadm/default/certapi/certtask.json", reqData);
		System.err.println("installLab  >> resResult  >>  "+resResult);
		if("OK".equals(resResult)) {
			//System.err.println("1");
			result.put("result", "success");
			result.put("msg", "Congratulations, Installation is underway, please check the log after 10 seconds.");
			//开启线程，检测状态，等到lab安装完成返回success时genclient
			new Thread(new Runnable() {
				@Override
				public void run() {
					//System.err.println("2");
					String addLabFlag="false";//用于添加lab动作记录到数据库
					int i = 0;
					int count = 0;
					while(true) {
						try {
							//等待10秒钟
							Thread.sleep(1000*10);
							System.err.println("installLab >> "+aservername+" thread "+count);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						if(i>10) {
							//genClient.sh异常10次跳出循环
							break;
						}
						JSONObject reqUrl = HttpReq.reqUrl("http://135.251.249.124:9333/spadm/default/labapi/dailylab/"+aservername+".json");
						if(!reqUrl.isEmpty()) {
							JSONArray jsonArray = reqUrl.getJSONArray("content");
							if(jsonArray.size()>0) {
								
								String labname = jsonArray.getJSONObject(0).getString("labname");
								String status = jsonArray.getJSONObject(0).getString("status");
								String ips = jsonArray.getJSONObject(0).getString("ips");
								String ip ="";
								if(ips.contains(",")) {
									String[] ipss = ips.split(",");
									ip=ipss[0];
								}
								String ss7 = jsonArray.getJSONObject(0).getString("ss7");
								String enwtpps = jsonArray.getJSONObject(0).getString("enwtpps");
								String log = jsonArray.getJSONObject(0).getString("log");
								String free = jsonArray.getJSONObject(0).getString("free");
								String ptversion = jsonArray.getJSONObject(0).getString("log");
								
								if(addLabFlag=="false") {
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
									String labStatus = serverInfoService.addLabStatus(status,log,enwtpps,ss7,labname,db,free,ips,ptversion,spa,deptid,createid);
									//labStatus 返回success表示记录到数据库内成功，返回fail则表示添加到数据库失败<因为在线程中，所以暂时没啥用>
									//只添加一次
									addLabFlag="true";
								}
								
								/*if(status=="Installing" && addPoolFlag=="false") {
									JSONArray infos = CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock, true, null);
									infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21F\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverTpye\": \"G\",\"serverMate\": \"S\",\"mateServer\": \"BJRMS21E\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
									for(int i=0; i<infos.size(); i++){
										CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock,false,infos.getJSONObject(i).getJSONObject(Constant.BODY));
									}
								}else */
								if (status=="Succeed") {
									try {
										Exec("cd /home/huanglei && ./genClient.sh "+labname+" "+ip+" "+enwtpps+" "+ss7);
										break;
									} catch (Exception e) {
										e.printStackTrace();
										i++;
										continue;
									}
								}/*else if (status=="Failed") {
									
								}*/
							}
							
						}
						count++;
					}
					
				}
			}).start();
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
		System.out.println("addServerDetails >> reqdate >> "+reqdate);
        String reqData = "{\"protocol\": \""+server.getServerProtocol()+"\", "
		        		+ "\"labname\": [\""+server.getServerName()+"\"], "
		        		+ "\"DB\": "+DB+", "
		        		+ "\"mate\": \"N\", "
		        		+ "\"release\": \""+server.getServerRelease()+"\", "
		        		+ "\"SPA\": "+SPA+", "
		        		+ "\"ins_flag\": \"0\"}";

        System.out.println("addServerDetails >> reqData >> "+reqData);
        String resResult = HttpReq.reqUrl("http://135.251.249.124:9333/spadm/default/certapi/certtask.json", reqData);
        //String resResult = HttpReq.reqUrl("http://135.251.249.124:9333/spadm/default/certapi/certtask.json", reqdate.toString());
        System.out.println("addServerInfo >> resResult >> "+resResult);
        
        
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
