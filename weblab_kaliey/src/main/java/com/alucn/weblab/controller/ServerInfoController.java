package com.alucn.weblab.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

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
import com.alucn.weblab.socket.TcpClient;
import com.alucn.weblab.utils.LabStatusUtil;
import com.alucn.weblab.utils.StringUtil;
import com.alucn.weblab.utils.TimeUtil;

import mx4j.tools.config.DefaultConfigurationBuilder.New;
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
	@Autowired(required = true)
	private SpaAndRtdbManService spaAndRtdbManService;

	/*
	 * @RequestMapping(path = "/getServerInfo") public String getServerInfo(Model
	 * model){ Map<String,Set<Map<String,JSONObject>>> infos =
	 * serverInfoService.getServerInfo(); model.addAttribute("infos", infos); return
	 * "serverInfo"; }
	 */

	// lab变动日志记录表
	@RequestMapping(path = "/getServerStatusLog")
	public String getServerStatusLog(HttpServletRequest request, Model model) {
		String serverName = request.getParameter("serverName") == null ? "" : request.getParameter("serverName").toString().trim();
		model.addAttribute("serverName", serverName);
		return "serverStatusLog";
	}

	@RequestMapping(path = "/getServerStatusLogJson")
	@ResponseBody
	public Map<String, Object> getServerStatusLogJson(HttpServletRequest request, Model model, HttpSession session)
			throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		String limit = request.getParameter("limit") == null ? "" : request.getParameter("limit").toString().trim();
		String offset = request.getParameter("offset") == null ? "" : request.getParameter("offset").toString().trim();
		String serverName = request.getParameter("serverName") == null ? "" : request.getParameter("serverName").toString().trim();
		System.err.println(serverName);
		String username = (String) session.getAttribute("login");
		ArrayList<HashMap<String, Object>> deptByUserName = loginService.getDeptIdsByUserName(username);
		List<String> deptids = new ArrayList<String>();
		if (deptByUserName.size() > 0) {
			for (HashMap<String, Object> hashMap : deptByUserName) {
				String deptid = "" + hashMap.get("dept_id");
				if (deptid != null && !"".equals(deptid)) {
					deptids.add(deptid);
				}
			}
			// deptid = (String) deptByUserName.get(0).get("dept_id");
		}
		Subject subject = SecurityUtils.getSubject();
		boolean hasRole = subject.hasRole("admin");
		ArrayList<HashMap<String, Object>> labLogJson = serverInfoService.getServerStatusLogJson(limit, offset,
				serverName, deptids, hasRole);
		for (HashMap<String, Object> hashMap : labLogJson) {
			Long starttime = Long.parseLong((String) hashMap.get("starttime"));
			Long endtime = Long.parseLong((String) hashMap.get("endtime"));
			String timeDifference = TimeUtil.getTimeDifference(endtime, starttime);
			hashMap.put("hodingtime", timeDifference);
			hashMap.put("starttime", TimeUtil.stampToTime(starttime));
			hashMap.put("endtime", TimeUtil.stampToTime(endtime));
		}
		int labLogJsonCount = serverInfoService.getServerStatusLogJsonCount(serverName, deptids, hasRole);
		resultMap.put("rows", labLogJson);
		resultMap.put("total", labLogJsonCount);
		return resultMap;
	}
	
	@RequestMapping(path = "/change_auto_update_flag")
    @ResponseBody
    public Map<String, Object> change_auto_update_flag(HttpServletRequest request, Model model, HttpSession session)
            throws Exception {
	    Map<String,Object> resultMap = new HashMap<String,Object>();
        String serverName = request.getParameter("serverName") == null ? "" : request.getParameter("serverName").toString().trim();
        String flag = request.getParameter("flag") == null ? "" : request.getParameter("flag").toString().trim();
        serverInfoService.change_auto_update_flag(serverName, flag);
        resultMap.put("result", "SUCCESS");
        return resultMap;
    }

	@RequestMapping(value = "/getServerInfoJson")
	@ResponseBody
	public ArrayList<HashMap<String, Object>> getServerInfoJson(HttpSession session) throws Exception {

		ArrayList<HashMap<String, Object>> resultList = new ArrayList<HashMap<String, Object>>();
		Subject subject = SecurityUtils.getSubject();
		boolean hasRole = subject.hasRole("admin");
		// Map<String,Set<ServerSort>> infos = serverInfoService.getServerInfo();
		Map<String, Set<ServerSort>> infos = serverInfoService.getNewServerInfo();
		// logger.info("infos:==============="+infos);
		int id = 1;
		for (String info : infos.keySet()) {
			int pid = 0;
			// 父级目录
			HashMap<String, Object> superMap = new LinkedHashMap<String, Object>();
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
			superMap.put("auto_update_build", "");
			superMap.put("hodingtime", "");

			resultList.add(superMap);
			pid = id;
			id++;
			// 子级目录
			Set<ServerSort> sets = infos.get(info);
			for (ServerSort set : sets) {
				Map<String, JSONObject> maps = set.getMap();
				for (String map : maps.keySet()) {
					HashMap<String, Object> childMap = new LinkedHashMap<String, Object>();
					id++;
					JSONObject jsonObject = maps.get(map);
					// logger.info("===============");
					// logger.info(jsonObject);
					// {"lab":{"serverName":"BJRMS21C","serverIp":"135.242.17.206","serverRelease":"SP17.9","serverProtocol":"ITU","serverType":"Line","serverMate":"Primary","mateServer":"BJRMS21D","setName":"set2","serverSPA":["AethosTest","CDRPP311","CDRPPGW311","DIAMCL179","DROUTER179","ECTRL179","ENWTPPS179","EPAY179","EPPSA179","EPPSM179","GATEWAY179","NWTCOM111","NWTGSM066"],"serverRTDB":["SCRRTDBV7","AECIDB179","SGLDB28H","TIDDB28C","GPRSSIM08","AIRTDB179","CTRTDB179","HTIDDB179","PMOUDB179","PROMDB179","SIMDB179","SYDB179","GCIPL312","VTXDB179","SHRTDB28F","CDBRTDB","RCNRDB173","HMRTDB173","SESSDB311","ACMDB104","SIMIDXDB","FSNDB173","UARTDB287","RERTDB279","SFFDB28C","GCURDB","SLTBLRTDB","ID2MDN01","GTMDB28A"]},"taskStatus":{"status":"Idle","runningCase":""},"taskResult":{"success":[],"fail":[]}}
					// {"lab":{"serverName":"BJRMS21D","serverIp":"135.242.17.206","serverRelease":"SP17.9","serverProtocol":"ITU","serverType":"Line","serverMate":"Secondary","mateServer":"BJRMS21C","setName":"set2","serverSPA":["AethosTest","CDRPP311","CDRPPGW311","DIAMCL179","DROUTER179","ECTRL179","ENWTPPS179","EPAY179","EPPSA179","EPPSM179","GATEWAY179","NWTCOM111","NWTGSM066"],"serverRTDB":["SCRRTDBV7","AECIDB179","SGLDB28H","TIDDB28C","GPRSSIM08","AIRTDB179","CTRTDB179","HTIDDB179","PMOUDB179","PROMDB179","SIMDB179","SYDB179","GCIPL312","VTXDB179","SHRTDB28F","CDBRTDB","RCNRDB173","HMRTDB173","SESSDB311","ACMDB104","SIMIDXDB","FSNDB173","UARTDB287","RERTDB279","SFFDB28C","GCURDB","SLTBLRTDB","ID2MDN01","GTMDB28A"]},"taskStatus":{"status":"Idle","runningCase":""},"taskResult":{"success":[],"fail":[]}}
					// "taskStatus":{"status":"Idle","runningCase":""}
					JSONObject lab = jsonObject.getJSONObject("lab");
					// logger.info(lab);
					// logger.info("===============");
					String serverName = lab.getString("serverName");
					String serverIp = lab.getString("serverIp");
					String serverRelease = lab.getString("serverRelease");
					String serverProtocol = lab.getString("serverProtocol");
					String serverType = lab.getString("serverType");
					String serverMate = lab.getString("serverMate");
					String mateServer = lab.getString("mateServer");
					String sdeptid = lab.getString("deptid");
					String auto_update_flag = "Y";
					try {
					    auto_update_flag = lab.getString("auto_update_build");
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
					
					String timeDifference = "0";
					try {
						long lasttime = lab.getLong("last_time");
						long nowtime = new Date().getTime();
						timeDifference = TimeUtil.getTimeDifference(nowtime, lasttime);
						if(timeDifference.contains("-"))
						{
						    timeDifference = "0";
						}
					} catch (Exception e) {
						System.out.println(lab);
						e.printStackTrace();
					}
					ArrayList<HashMap<String, Object>> deptById = loginService.getDeptById(sdeptid);
					String deptname = "";
					if (deptById.size() > 0) {
						deptname = (String) deptById.get(0).get("dept_name");
					}

					String labdeptid = "";
					try {
						labdeptid = lab.getString("deptid");
					} catch (Exception e) {
						e.printStackTrace();
					}
					String status = jsonObject.getJSONObject("taskStatus").getString("status");
					/*
					 * int stateflag = 1; if("Idle".equals(status)) { stateflag=0; }
					 * if("Dead".equals(status)) { stateflag=1; } if("Running".equals(status)) {
					 * stateflag=2; } if("Ready".equals(status)) { stateflag=3; }
					 * if("Finished".equals(status)) { stateflag=4; }
					 */
					childMap.put("id", id);
					childMap.put("pid", pid);
					childMap.put("name", serverName);
					// childMap.put("status", stateflag);
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
					childMap.put("auto_update_build", auto_update_flag);
					// childMap.put("hodingtime", 0);
					String username = (String) session.getAttribute("login");
					ArrayList<HashMap<String, Object>> deptByUserName = loginService.getDeptIdsByUserName(username);
					List<String> deptids = new ArrayList<>();
					if (deptByUserName.size() > 0) {
						for (HashMap<String, Object> hashMap : deptByUserName) {
							String deptid = "" + hashMap.get("dept_id");
							if (deptid != null && !"".equals(deptid)) {
								deptids.add(deptid);
							}
						}
						// deptid = (String) deptByUserName.get(0).get("deptid");
					}
					if (deptids.contains(labdeptid) || hasRole) {
						resultList.add(childMap);
					}
					// logger.info("resultList:========"+resultList);
					id++;
				}
			}

		}
		Set<Integer> setList = new HashSet();
		List<Integer> idList = new ArrayList<>();
		for (int i = 0; i < resultList.size(); i++) {
			int sid = (int) resultList.get(i).get("id");
			int spid = (int) resultList.get(i).get("pid");
			String stype = (String) resultList.get(i).get("type");
			if ("server".equals(stype)) {
				setList.add(spid);
			} else if ("set".equals(stype)) {
				idList.add(sid);
			}
		}
		idList.removeAll(setList);
		// logger.info("idList >> "+idList);
		ArrayList<HashMap<String, Object>> midResultList = new ArrayList<HashMap<String, Object>>();

		for (HashMap<String, Object> hashMap : resultList) {
			int sid = (int) hashMap.get("id");
			if (!idList.contains(sid)) {
				midResultList.add(hashMap);
			}
		}

		return midResultList;
	}

	@RequestMapping(path = "/getServerInfo")
	public String getServerInfo(Model model, HttpSession session) throws Exception {

		
		String username = (String) session.getAttribute("login");
		Subject subject = SecurityUtils.getSubject();
		boolean hasRole = subject.hasRole("admin");

		ArrayList<HashMap<String, Object>> deptByUserName = new ArrayList<HashMap<String, Object>>();
		if (hasRole) {
			deptByUserName = loginService.getDeptsByAdmin();
		} else {
			deptByUserName = loginService.getDeptsByUserName(username);
		}

		Map<Object, Object> deptmap = new HashMap<Object, Object>();
		List<String> deptids = new ArrayList<>();
		if (deptByUserName.size() > 0) {
			for (HashMap<String, Object> hashMap : deptByUserName) {
				String deptid = "" + hashMap.get("dept_id");
				String deptName = "" + hashMap.get("dept_name");
				if (deptid != null && !"".equals(deptid) && !"default".equals(deptName)) {
					deptmap.put(deptName, deptid);
					deptids.add(deptid);
				}
			}
			
		}
		
		model.addAttribute("deptmap", deptmap);
		model.addAllAttributes(spaAndRtdbManService.getSpaAndRtdbList());
		// JSONArray Servers =
		// CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock,
		// true, null);
		JSONArray Servers = LabStatusUtil.getLabStatus();
		// result.put("Servers", Servers);
		// List setList = new ArrayList<>();
		Set<String> sets = new HashSet<String>();
		if (Servers.size() > 0) {
			for (int i = 0; i < Servers.size(); i++) {
				JSONObject lab = Servers.getJSONObject(i).getJSONObject("body").getJSONObject(Constant.LAB);
				String sdeptid = "";
				try {
					sdeptid = lab.getString("deptid");
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (hasRole) {
					String setName = lab.getString("setName");
					sets.add(setName);
				} else if (deptids.contains(sdeptid)) {
					String setName = lab.getString("setName");
					sets.add(setName);
				}
			}
		}
		sets.add("default");
		model.addAttribute("sets", sets);
		
		String[] spaDefault = {"'EPAY'", "'EPPSA'", "'ENWTPPS'", "'EPPSM'", "'NWTCOM'", "'NWTGSM'", "'GATEWAY'", "'AETHOSTEST'", "'DIAMCL'", "'DROUTER'", "'ECTRL'"};
		String[] dbDefault = {"'ACMDB'","'SIMDB'","'AIRTDB'","'CTRTDB'","'GPRSSIM'"};
		
		model.addAttribute("spaDefault", Arrays.asList(spaDefault));
		model.addAttribute("dbDefault", Arrays.asList(dbDefault));
		return "serverInfo3";
	}

	/*
	 * @RequestMapping(path = "/testGetServerInfo") public String test(Model model){
	 * Map<String,Set<ServerSort>> infos = serverInfoService.getServerInfo();
	 * logger.info("-----------------------infos------------------------------");
	 * return null; }
	 */
	@RequestMapping(path = "/getServerDetails")
	public String getServerDetails(String serverName, Model model) {
		Map<String, Set<Map<String, JSONObject>>> infos = serverInfoService.getNewServerInfoNosort();
		for (String key : infos.keySet()) {
			Set<Map<String, JSONObject>> set = infos.get(key);
			Iterator<Map<String, JSONObject>> iterator = set.iterator();
			while (iterator.hasNext()) {
				Map<String, JSONObject> serverOrMate = iterator.next();
				if (serverOrMate.get(serverName) != null) {
					model.addAttribute("info", JSONObject.fromObject(serverOrMate.get(serverName)));
				}
			}
		}
		return "serverDetails";
	}

	@RequestMapping(path = "/addlablog")
	public String addlablog(Model model) throws Exception {
		// model.addAllAttributes(spaAndRtdbManService.getSpaAndRtdbInfo());
		return "addlablog";
	}

	@RequestMapping(path = "/addlablogJson")
	@ResponseBody
	public Map<String, Object> addlablogJson(HttpServletRequest request, Model model, HttpSession session)
			throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		String limit = request.getParameter("limit") == null ? "" : request.getParameter("limit").toString().trim();
		String offset = request.getParameter("offset") == null ? "" : request.getParameter("offset").toString().trim();
		String labname = request.getParameter("labname") == null ? "" : request.getParameter("labname").toString().trim();
		String username = (String) session.getAttribute("login");
		ArrayList<HashMap<String, Object>> deptByUserName = loginService.getDeptIdsByUserName(username);

		// System.out.println("addlablogJson : "+deptByUserName);

		List<String> deptids = new ArrayList<>();
		if (deptByUserName.size() > 0) {
			for (HashMap<String, Object> hashMap : deptByUserName) {
				String deptid = "" + hashMap.get("dept_id");
				// System.out.println(deptid);
				if (deptid != null && !"".equals(deptid)) {
					deptids.add(deptid);
				}
			}
		}
		Subject subject = SecurityUtils.getSubject();
		boolean hasRole = subject.hasRole("admin");
		// System.out.println(deptids);
		ArrayList<HashMap<String, Object>> labLogJson = serverInfoService.getLabLogJson(limit, offset, labname, deptids,
				hasRole);
		if (labLogJson.size() > 0) {
			for (HashMap<String, Object> hashMap : labLogJson) {
				String createtime = (String) hashMap.get("createtime");
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
				long lt = new Long(createtime);
				Date date = new Date(lt);
				String format = simpleDateFormat.format(date);
				hashMap.put("createtime", format);
			}
		}
		int labLogJsonCount = serverInfoService.getLabLogJsonCount(labname, deptids);

		resultMap.put("rows", labLogJson);
		resultMap.put("total", labLogJsonCount);

		return resultMap;

	}

	@RequestMapping(path = "/addServerInfo")
	public String addServerInfo(Model model) throws Exception {
		model.addAllAttributes(spaAndRtdbManService.getSpaAndRtdbInfo());
		return "addServerInfo";
	}

	@RequestMapping(path = "/getLabInfo")
	@ResponseBody
	public Map<String, Object> getLabInfo(HttpSession session, Model model, HttpServletRequest request)
			throws Exception {
		String aeservername = request.getParameter("aeservername") == null ? ""
				: request.getParameter("aeservername").toString().trim();
		Map<String, Object> result = new HashMap<String, Object>();
		// 通过lab的名称获取genClient所需要的信息返回
		JSONObject reqUrl = HttpReq
				.reqUrl("http://135.251.249.124:9333/spadm/default/labapi/dailylab/" + aeservername + ".json");
		if (!reqUrl.isEmpty()) {
			JSONArray jsonArray = reqUrl.getJSONArray("content");
			if (jsonArray.size() > 0) {

				String labname = jsonArray.getJSONObject(0).getString("labname");
				String status = jsonArray.getJSONObject(0).getString("status");
				String ips = jsonArray.getJSONObject(0).getString("ips");
				String ip = "";
				List<String> ipList = new ArrayList<>();
				if (ips.contains(",")) {
					String[] ipss = ips.split(",");
					for (String string : ipss) {
						ipList.add(string);
					}
					ip = ipss[0];
				}
				String ss7 = jsonArray.getJSONObject(0).getString("ss7");
				String enwtpps = jsonArray.getJSONObject(0).getString("enwtpps");
				String log = jsonArray.getJSONObject(0).getString("log");
				String free = jsonArray.getJSONObject(0).getString("free");
				String ptversion = jsonArray.getJSONObject(0).getString("log");
				if ("Succeed".equals(status)) {
					result.put("ipList", ipList);
					result.put("ss7", ss7);
					result.put("enwtpps", enwtpps);
				} else if ("Installing".equals(status)) {
					result.put("result", "fail");
					result.put("msg", "lab is installing");
					return result;
				} else if ("Failed".equals(status)) {
					result.put("result", "fail");
					result.put("msg", "lab status failed");
					return result;
				}
			} else {
				result.put("result", "fail");
				result.put("msg", "cannot find the lab");
				return result;
			}
		}

		String username = session.getAttribute("login").toString();
		System.err.println("=======>" + username);
		NUser user = new NUser();
		user.setUsername(username);
		ArrayList<HashMap<String, Object>> queryNUser = new ArrayList<HashMap<String, Object>>();
		ArrayList<HashMap<String, Object>> deptIdsByUserName = new ArrayList<HashMap<String, Object>>();
		try {
			queryNUser = loginService.queryNUser(user);
			deptIdsByUserName = loginService.getDeptIdsByUserName(username);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<String> deptids = new ArrayList<String>();
		if (deptIdsByUserName.size() > 0) {
			for (HashMap<String, Object> hashMap : deptIdsByUserName) {
				String deptid = "" + hashMap.get("dept_id");
				if (deptid != null && !"".equals(deptid)) {
					deptids.add(deptid);
				}
			}
			// deptid = (String) deptByUserName.get(0).get("dept_id");
		}
		String createid = "";
		if (queryNUser.size() > 0) {
			createid = (String) queryNUser.get(0).get("id");
		}
		// JSONArray Servers =
		// CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock,
		// true, null);
		JSONArray Servers = LabStatusUtil.getLabStatus();
		// result.put("Servers", Servers);
		// List setList = new ArrayList<>();
		Set sets = new HashSet<>();
		if (Servers.size() > 0) {
			for (int i = 0; i < Servers.size(); i++) {
				JSONObject lab = Servers.getJSONObject(i).getJSONObject("body").getJSONObject(Constant.LAB);
				String sdeptid = "";
				try {
					sdeptid = lab.getString("deptid");
				} catch (Exception e) {
					e.printStackTrace();
				}
				Subject subject = SecurityUtils.getSubject();
				boolean hasRole = subject.hasRole("admin");
				if (hasRole) {
					String setName = lab.getString("setName");
					sets.add(setName);
				} else if (deptids.contains(sdeptid)) {
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

	// http://localhost:8080/weblab/genClient.do?labname=CHSP12B&ip=135.2.213.211&enwtpps=SP18.9&ss7=ITU&setname=set1
	@RequestMapping(path = "/genClient")
	@ResponseBody
	public Map<String, Object> genClient(HttpSession session, HttpServletRequest request) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();

		String labname = request.getParameter("labname") == null ? ""
				: request.getParameter("labname").toString().trim();
		String ip = request.getParameter("ip") == null ? "" : request.getParameter("ip").toString().trim();
		String enwtpps = request.getParameter("enwtpps") == null ? ""
				: request.getParameter("enwtpps").toString().trim();
		String ss7 = request.getParameter("ss7") == null ? "" : request.getParameter("ss7").toString().trim();
		String setname = request.getParameter("setname") == null ? ""
				: request.getParameter("setname").toString().trim();
		String ehdept = request.getParameter("ehdept") == null ? "" : request.getParameter("ehdept").toString().trim();
		String username = session.getAttribute("login").toString();

		System.out.println("ehdept:" + ehdept);

		NUser user = new NUser();
		user.setUsername(username);
		ArrayList<HashMap<String, Object>> queryNUser = new ArrayList<HashMap<String, Object>>();
		// String deptid= "";
		String createid = "";
		try {
			queryNUser = loginService.queryNUser(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (queryNUser.size() > 0) {
			// deptid = (String) queryNUser.get(0).get("deptid");
			createid = (String) queryNUser.get(0).get("id");
		}
		String createtime = new Date().getTime() + "";
		serverInfoService.addLabStatus("Installing", "exist", "", enwtpps, ss7, labname, "", "", ip, "", "", ehdept,
				createid, createtime, "");
		try {
			logger.info("cd /home/huanglei && ./genClient.sh " + labname + " " + ip + " " + enwtpps + " " + ss7 + " "
					+ setname + " " + ehdept);
			// 为了测试注释掉执行部分
			Exec("cd /home/huanglei && ./genClient.sh " + labname + " " + ip + " " + enwtpps + " " + ss7 + " " + setname
					+ " " + ehdept);
			serverInfoService.editLabStatus("Succeed", "", labname, new Date().getTime() + "", createtime);
			result.put("result", "success");
		} catch (Exception e) {
			e.printStackTrace();
			serverInfoService.editLabStatus("Failed", "", labname, new Date().getTime() + "", createtime);
			result.put("result", "fail");
		}
		return result;
	}
	@RequestMapping(path = "/genClientNew")
	@ResponseBody
	public Map<String, Object> genClientNew(HttpSession session, HttpServletRequest request) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		String labname = request.getParameter("labname") == null ? "" : request.getParameter("labname").toString().trim();
		//String ip = request.getParameter("ip") == null ? "" : request.getParameter("ip").toString().trim();
		//String enwtpps = request.getParameter("enwtpps") == null ? "" : request.getParameter("enwtpps").toString().trim();
		//String ss7 = request.getParameter("ss7") == null ? "" : request.getParameter("ss7").toString().trim();
		String setname = request.getParameter("setname") == null ? "" : request.getParameter("setname").toString().trim();
		String ehdept = request.getParameter("ehdept") == null ? "" : request.getParameter("ehdept").toString().trim();
		String update_flag = request.getParameter("eupdate") == null ? "" : request.getParameter("eupdate").toString().trim();
		String username = session.getAttribute("login").toString();
		
		System.out.println("ehdept:" + ehdept);
		
		NUser user = new NUser();
		user.setUsername(username);
		ArrayList<HashMap<String, Object>> queryNUser = new ArrayList<HashMap<String, Object>>();
		// String deptid= "";
		String createid = "";
		try {
			queryNUser = loginService.queryNUser(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (queryNUser.size() > 0) {
			// deptid = (String) queryNUser.get(0).get("deptid");
			createid = (String) queryNUser.get(0).get("id");
		}
		String createtime = new Date().getTime() + "";
		//serverInfoService.addLabStatus("Installing", "exist", "", enwtpps, ss7, labname, "", "", ip, "", "", ehdept,createid, createtime, "");
		try {
			//logger.info("cd /home/huanglei && ./genClient.sh " + labname + " " + ip + " " + enwtpps + " " + ss7 + " " + setname + " " + ehdept);
			// 为了测试注释掉执行部分
			/*
			 * String reqData = "{\"ip\": \"" + ip + "\", " + "\"labname\": \"" + labname +
			 * "\", " + "\"release\": \"" + enwtpps + "\", " + "\"protocol\": \"" + ss7 +
			 * "\", " + "\"deptid\": \"" + ehdept + "\", " + "\"setname\": \""+ setname +
			 * "\"}";
			 */
			String reqData =  "{\"labname\": \"" + labname + "\",  \"deptid\": \"" + ehdept + "\",  \"setname\": \""+ setname +  "\", " + "\"update_flag\": \"" + update_flag +"\"}";
			logger.info(reqData);
			
			JSONObject req_json = new JSONObject();
			req_json.put("labname", labname);
			req_json.put("deptid", ehdept);
			req_json.put("setname", setname);
			req_json.put("update_flag", update_flag);
			logger.info("req_json: " + req_json.toString());
			//Exec("cd /home/huanglei && ./genClient.sh " + labname + " " + ip + " " + enwtpps + " " + ss7 + " " + setname+ " " + ehdept);
			//serverInfoService.editLabStatus("Succeed", "", labname, new Date().getTime() + "", createtime);
			//result.put("result", "success");
			
			String resResult = HttpReq.reqUrl("http://135.242.16.160:8000/auto-test/api/add_exist_lab", reqData); 
			result.put("msg", resResult);
			 
			result.put("result", "success");
		} catch (Exception e) {
			e.printStackTrace();
			//serverInfoService.editLabStatus("Failed", "", labname, new Date().getTime() + "", createtime);
			result.put("result", "fail");
		}
		return result;
	}

	public Map<String, Object>  getlabGroupFlag(String labname, String deptid, String username) throws Exception {
		Map<String, Object> result = new HashMap<>();
		if (deptid.equals("1")) {
		    result.put("result", false);
            result.put("msg", "Default user have no permission.");
            return result;
		}
		JSONObject reqUrl = null;
		try {
		    reqUrl = HttpReq
	                .reqUrl("http://135.251.249.124:9191/spadm/default/certapi/kvmlabusage/" + labname + ".json");
        } catch (Exception e) {
            // TODO: handle exception
        }
		
		if (!reqUrl.isEmpty()) 
		{
			JSONArray jsonArray = reqUrl.getJSONArray("content");
			if (jsonArray.size() > 0) {
				String labuser = jsonArray.getJSONObject(0).getString("labuser");
				NUser user = new NUser();
				String msg = "User in not in the group";
				if("Dailyrun".equals(labuser))
				{		    
	                user.setUsername(username.trim());
				}
				else
				{
				    String email = jsonArray.getJSONObject(0).getString("email");
				    ArrayList<HashMap<String, Object>> users = loginService.queryUserFromEmail(email);
				    if(users.size() > 0)
				    {
				        user.setUsername(users.get(0).get("user_name").toString());
				    }
				    else{
				        msg = "email: " + email + " is not in the DB.";
				        result.put("result", false);
		                result.put("msg", msg);
		                return result;
				    }
				    
				}
			    ArrayList<HashMap<String, Object>> queryNUser = loginService.queryNUser(user);
			    boolean group_match = false;
			    
			    for(int i = 0; i < queryNUser.size(); i++)
			    {
			        logger.info(queryNUser.get(i).toString());
			        logger.info(queryNUser.get(i).get("deptid").toString());
			        if(queryNUser.get(i).get("deptid").toString().equals(deptid))
			        {
			            group_match = true;
			            msg = "";
			            break;
			        }
			    }
			    result.put("result", group_match);
                result.put("msg", msg);
                return result;
			}
			else {
				result.put("result", false);
				result.put("msg", "lab  is not registered in the system.");
				return result;
			}
		}
        else 
        {
			result.put("result", false);
			result.put("msg", labname + " information cannot be obtained.");
			return result;
		}

	}

	// 通过labname获取lab的运行状态（内存中的状态）
	@RequestMapping(path = "/testStatus")
	@ResponseBody
	public Map<String, Object> getLabStatus(String labname) {
		Map<String, Object> result = new HashMap<>();
		JSONArray Servers = CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock, true,
				null);
		// result.put("Servers", Servers);
		// List setList = new ArrayList<>();
		Set sets = new HashSet<>();
		if (Servers.size() > 0) {
			for (int i = 0; i < Servers.size(); i++) {
				JSONObject lab = Servers.getJSONObject(i).getJSONObject(Constant.LAB);
				String status = "";
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
	public Map<String, Object> installLab(HttpSession session, HttpServletRequest request) throws Exception {

		Map<String, Object> result = new HashMap<String, Object>();

		String aservername = request.getParameter("aservername") == null ? "" : request.getParameter("aservername").toString().trim();
		String arelease = request.getParameter("arelease") == null ? "" : request.getParameter("arelease").toString().trim();
		String aprotocol = request.getParameter("aprotocol") == null ? "" : request.getParameter("aprotocol").toString().trim();
		/*
		 * String aservertype =
		 * request.getParameter("aservertype")==null?"":request.getParameter(
		 * "aservertype").toString().trim(); String amatetype =
		 * request.getParameter("amatetype")==null?"":request.getParameter("amatetype").
		 * toString().trim();
		 */
		String hdept = request.getParameter("hdept") == null ? "" : request.getParameter("hdept").toString().trim();
		String ainsflag = request.getParameter("ainsflag") == null ? "" : request.getParameter("ainsflag").toString().trim();
		String sspa = request.getParameter("sspa") == null ? "" : request.getParameter("sspa").toString().trim();
		String sdb = request.getParameter("sdb") == null ? "" : request.getParameter("sdb").toString().trim();
		String sset = request.getParameter("sset") == null ? "" : request.getParameter("sset").toString().trim();
		

		/*
		 * 部门添加权限验证：
		 * 
		 * super用户 通过 labname 查 用户 1、用户在系统内，查询部门，与传入的部门id比对 2、用户不在系统内，返回失败
		 * 
		 * admin用户 不限制
		 * 
		 */
		String username = (String) session.getAttribute("login");
		Subject subject = SecurityUtils.getSubject();
		boolean isPermitted = subject.isPermitted("lab:create");
		boolean hasAdminRole = subject.hasRole("admin");
		// 如果是超级管理员，不做校验
		if (!hasAdminRole) {
			if (!isPermitted) {
				result.put("result", "fail");
				result.put("msg",
						"Sorry ,You have no authority to operate the business, please contact the super administrator.");
				return result;
			} else {
				Map<String, Object> getlabGroupFlag = getlabGroupFlag(aservername, hdept, username);
				if (!(boolean) getlabGroupFlag.get("result")) {
					result.put("result", "fail");
					result.put("msg", getlabGroupFlag.get("msg"));
					return result;
				}
			}
		}

		/*
		 * 安装前进行check 如果lab正在运行（running）则不进行安装，返回失败
		 */
		Map<String, Object> labStatus = getLabStatus(aservername);

		String db = StringUtil.formatJsonString(sdb);
		String spa = StringUtil.formatJsonString(sspa);

		String reqData = "{\"protocol\": \"" + aprotocol + "\", " + "\"labname\": [\"" + aservername + "\"], "
				+ "\"DB\": " + db + ", " + "\"mate\": \"N\", " + "\"release\": \"" + arelease + "\", " + "\"SPA\": "
				+ spa + ", " + "\"ins_flag\": \"" + ainsflag + "\"}";

		// logger.info(reqData);
		/*
		 * { "protocol": "ITU", "labname": ["CHSP12B"], "DB": ["SIMDB","ACMDB"], "mate":
		 * "N", "release": "SP18.9", "SPA":
		 * ["DROUTER","ENWTPPS","EPAY","EPPSA","EPPSM","NWTCOM","NWTGSM","DIAMCL"],
		 * "ins_flag": "0" }
		 */

		String resResult = HttpReq.reqUrl("http://135.251.249.124:9333/spadm/default/certapi/certtask.json", reqData);
		// String resResult = "OK";
		logger.info("installLab  >> resResult  >>  " + resResult);
		if ("OK".equals(resResult)) {
			// logger.info("1");
			result.put("result", "success");
			result.put("msg", "Congratulations, Installation is underway, please check the log after 10 seconds.");
			// 开启线程，检测状态，等到lab安装完成返回success时genclient
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					// logger.info("2");
					String addLabFlag = "false";// 用于添加lab动作记录到数据库
					int i = 0;
					int count = 0;
					long createtime = new Date().getTime();
					String createid = "";
					String deptid = "";
					String username = session.getAttribute("login").toString();
					NUser user = new NUser();
					user.setUsername(username);
					ArrayList<HashMap<String, Object>> queryNUser = new ArrayList<HashMap<String, Object>>();
					try {
						queryNUser = loginService.queryNUser(user);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (queryNUser.size() > 0) {
						createid = (String) queryNUser.get(0).get("id");
						deptid = (String) queryNUser.get(0).get("deptid");
					}
					while (true) {
						try {
							// 等待10s
							Thread.sleep(1000 * 10);

							// Thread.sleep(1000*10);
							Date date = new Date();
							SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
							logger.info(df.format(date) + " >>  installLab >> " + aservername + " thread " + count);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						if (i > 50) {
							// genClient.sh异常10次跳出循环
							break;
						}
						JSONObject reqUrl = HttpReq.reqUrl(
								"http://135.251.249.124:9333/spadm/default/labapi/dailylab/" + aservername + ".json");
						if (!reqUrl.isEmpty()) {
							JSONArray jsonArray = reqUrl.getJSONArray("content");
							if (jsonArray.size() > 0) {

								String labname = jsonArray.getJSONObject(0).getString("labname");
								String status = jsonArray.getJSONObject(0).getString("status");
								// String status = "Succeed";
								String ips = jsonArray.getJSONObject(0).getString("ips");
								String ip = "";
								if (ips.contains(",")) {
									String[] ipss = ips.split(",");
									ip = ipss[0];
								}
								// String ss7 = jsonArray.getJSONObject(0).getString("ss7");
								// String enwtpps = jsonArray.getJSONObject(0).getString("enwtpps");
								String ss7 = aprotocol;
								String enwtpps = arelease;
								String log = jsonArray.getJSONObject(0).getString("log");
								String free = jsonArray.getJSONObject(0).getString("free");
								String ptversion = jsonArray.getJSONObject(0).getString("log");

								if (addLabFlag == "false") {
									String labStatus = serverInfoService.addLabStatus(status, "new", "", enwtpps, ss7,
											labname, db, free, ips, ptversion, spa, deptid, createid, createtime + "",
											ainsflag);
									// labStatus 返回success表示记录到数据库内成功，返回fail则表示添加到数据库失败<因为在线程中，所以暂时没啥用>
									// 只添加一次
									addLabFlag = "true";
								}
								if ("Installing".equals(status)) {
									SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
									logger.info(df.format(new Date()) + "  >>  Installing  >>" + i);
									/*
									 * JSONArray infos = CaseConfigurationCache.readOrWriteSingletonCaseProperties(
									 * CaseConfigurationCache.lock, true, null); infos.
									 * add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21F\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverTpye\": \"G\",\"serverMate\": \"S\",\"mateServer\": \"BJRMS21E\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}"
									 * ); for(int i=0; i<infos.size(); i++){
									 * CaseConfigurationCache.readOrWriteSingletonCaseProperties(
									 * CaseConfigurationCache.lock,false,infos.getJSONObject(i).getJSONObject(
									 * Constant.BODY)); }
									 */
								} else if ("Succeed".equals(status)) {
									try {
										serverInfoService.editLabStatus(status, log, labname, new Date().getTime() + "",
												createtime + "");
										// logger.info("cd /home/huanglei && ./genClient.sh "+labname+" "+ip+"
										// "+enwtpps+" "+ss7+" "+sset+" "+deptid);
										// Exec("cd /home/huanglei && ./genClient.sh "+labname+" "+ip+" "+enwtpps+"
										// "+ss7+" "+sset+" "+deptid);
										break;
									} catch (Exception e) {
										e.printStackTrace();
										i++;
										continue;
									}
								} else if ("Failed".equals(status)) {
									// 如果失败，则直接跟新记录表，跳出循环
									try {
										serverInfoService.editLabStatus(status, log, labname, new Date().getTime() + "",
												createtime + "");
										break;
									} catch (Exception e) {
										e.printStackTrace();
										i++;
										continue;
									}
								}
							}

						} else {
							continue;
						}
						count++;
						try {
							// 等待10分钟
							Thread.sleep(1000 * 60 * 10);
							logger.info("10m >> installLab >> " + aservername + " thread " + count);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}

				}
			});
			thread.start();
		}
		if ("BAD".equals(resResult)) {
			result.put("result", "fail");
			result.put("msg", "Do not accept installation requests, please contact the super administrator.");
		}
		return result;
	}

	@RequestMapping(path = "/installLabNew")
	@ResponseBody
	public Map<String, Object> installLabNew(HttpSession session, HttpServletRequest request) throws Exception {

		Map<String, Object> result = new HashMap<String, Object>();

		String aservername = request.getParameter("aservername") == null ? "" : request.getParameter("aservername").toString().trim();
		String arelease = request.getParameter("arelease") == null ? "" : request.getParameter("arelease").toString().trim();
		String aprotocol = request.getParameter("aprotocol") == null ? "" : request.getParameter("aprotocol").toString().trim();
		String hdept = request.getParameter("hdept") == null ? "" : request.getParameter("hdept").toString().trim();
		String ainsflag = request.getParameter("ainsflag") == null ? "" : request.getParameter("ainsflag").toString().trim();
		String sspa = request.getParameter("sspa") == null ? "" : request.getParameter("sspa").toString().trim();
		String sdb = request.getParameter("sdb") == null ? "" : request.getParameter("sdb").toString().trim();
		String sset = request.getParameter("sset") == null ? "" : request.getParameter("sset").toString().trim();
		String update_flag = request.getParameter("aupdate") == null ? "" : request.getParameter("aupdate").toString().trim();
		Subject subject = SecurityUtils.getSubject();
		boolean isPermitted = subject.isPermitted("lab:create");
		String username = (String) session.getAttribute("login");
		
		boolean hasAdminRole = subject.hasRole("admin");
		// 如果是超级管理员，不做校验
		if (!hasAdminRole) {
			if (!isPermitted) {
				result.put("result", "fail");
				result.put("msg", "Sorry ,You have no authority to operate the business, please contact the super administrator.");
				return result;
			} else {
			    Map<String, Object> getlabGroupFlag = getlabGroupFlag(aservername, hdept, username);
                if (!(boolean) getlabGroupFlag.get("result")) {
                    result.put("result", "fail");
                    result.put("msg", getlabGroupFlag.get("msg"));
                    return result;
                }
			}
		}
		String db = StringUtil.formatJsonString(sdb);
		String spa = StringUtil.formatJsonString(sspa);

		String reqData = "{\"protocol\": \"" + aprotocol + "\", " + "\"labname\": \"" + aservername + "\", "
				+ "\"DB\": " + db + ", " + "\"mate\": \"N\", "+ "\"setname\": \""+sset+"\", "+ "\"deptid\": \""+hdept+"\", " + "\"release\": \"" + arelease + "\", " + "\"SPA\": "
				+ spa + ", " + "\"ins_flag\": \"" + ainsflag + "\", " + "\"update_flag\": \"" + update_flag + "\" }";
		
		JSONObject req_json = new JSONObject();
		req_json.put("protocol", aprotocol);
		req_json.put("labname", aservername);
		req_json.put("DB", new JSONArray().fromObject(db));
		req_json.put("mate", "N");
		req_json.put("setname", sset);
		req_json.put("deptid", hdept);
		req_json.put("release", arelease);
		req_json.put("SPA", new JSONArray().fromObject(spa));
		req_json.put("ins_flag", ainsflag);
		req_json.put("update_flag", update_flag);
		System.out.println(reqData);
		System.out.println("req_json:" + req_json.toString());
		String resResult = HttpReq.reqUrl("http://135.242.16.160:8000/auto-test/api/add_new_lab",reqData); 
		logger.info("installLab  >> resResult  >>  " + resResult);
		result.put("msg",resResult);
		result.put("result", "success");
		
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
	public void addServerDetails(HttpSession session, NServer server) throws Exception {

		// 检测角色 >> /addServerInfo roles[spuer,admin]
		// insert into kaliey.n_permission_url(permission_name,url,type,remark)
		// values('authc,roles["admin","super"]','/addServerInfo.do','menu','新增lab超级用户以上权限');

		String useName = session.getAttribute("login").toString();
		NUser nuser = new NUser();
		nuser.setUsername(useName);
		ArrayList<HashMap<String, Object>> queryNUser = loginService.queryNUser(nuser);
		int id = Integer.parseInt(queryNUser.get(0).get("id").toString());
		int deptId = Integer.parseInt(queryNUser.get(0).get("deptid").toString());
		server.setCreateUserId(id);
		server.setCreateDeptId(deptId);
		// serverInfoService.addServerDetails(server);
		// 添加server
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

		// {"ins_flag":"0","protocol":"ANSI","labname":["BJRMS21H"],"DB":["CTRTDB","XBRTDB","GNRTDB","SGLDB","SGLRTDB","SIMDB"],"mate":"N","release":"SP17.9","SPA":["DROUTER","ENWTPPS","EPAY","EPPSA","EPPSM","NWTCOM","NWTGSM","ECGS"]}
		logger.info("addServerDetails >> reqdate >> " + reqdate);
		String reqData = "{\"protocol\": \"" + server.getServerProtocol() + "\", " + "\"labname\": [\""
				+ server.getServerName() + "\"], " + "\"DB\": " + DB + ", " + "\"mate\": \"N\", " + "\"release\": \""
				+ server.getServerRelease() + "\", " + "\"SPA\": " + SPA + ", " + "\"ins_flag\": \"0\"}";

		logger.info("addServerDetails >> reqData >> " + reqData);
		String resResult = HttpReq.reqUrl("http://135.251.249.124:9333/spadm/default/certapi/certtask.json", reqData);
		// String resResult =
		// HttpReq.reqUrl("http://135.251.249.124:9333/spadm/default/certapi/certtask.json",
		// reqdate.toString());
		logger.info("addServerInfo >> resResult >> " + resResult);

	}

	// 定义一个方法：传入lab名字和用户的deptid，取判断是否可以使用该lab-->已经有方法完成此功能
	/*
	 * public Map<String, Object> isCanUseLab(String labname,String deptid) {
	 * Map<String, Object> result = new HashMap<String, Object>();
	 * if(labname==null||"".equals(labname)) { result.put("result", "false");
	 * result.put("msg", "labname is require."); return result; }
	 * if(deptid==null||"".equals(deptid)) { result.put("result", "false");
	 * result.put("msg", "deptid is require."); return result; } JSONObject reqUrl =
	 * HttpReq.reqUrl(
	 * "http://135.251.249.124:9333/spadm/default/labapi/kvmlabusage/"+labname+
	 * ".json");
	 * //http://135.251.249.124:9333/spadm/default/labapi/kvmlabusage/BJVM12B.json
	 * //"labuser": "Yang PAN", if(!reqUrl.isEmpty()) { JSONArray jsonArray =
	 * reqUrl.getJSONArray("content"); if(jsonArray.size()>0) { String labuser =
	 * jsonArray.getJSONObject(0).getString("labuser");
	 * 
	 * }else { result.put("result", "false"); result.put("msg",
	 * "No lab was found."); return result; } } return result; }
	 */

	@RequestMapping(path = "/removeServerInfo")
	public void removeServerInfo(Model model, HttpSession session, String condition, PrintWriter out) throws Exception {
		// System.out.println("1111111111111111111"+condition);
		out.write(serverInfoService.removeServerInfoNew(condition));
	}
	
	@RequestMapping(path = "/init_lab")
    public void init_lab(Model model, HttpSession session, String condition, PrintWriter out) throws Exception {
        out.write(serverInfoService.init_lab(condition));
    }
	
	@RequestMapping(path = "/restart_client")
    public void restart_client(Model model, HttpSession session, String condition, PrintWriter out) throws Exception {
        out.write(serverInfoService.restart_client(condition));
    }
	
	@RequestMapping(path = "/restart_plat")
    public void restart_plat(Model model, HttpSession session, String condition, PrintWriter out) throws Exception {
        out.write(serverInfoService.restart_plat(condition));
    }
	
	@RequestMapping(path = "/reinstall_lab")
    public void reinstall_lab(Model model, HttpSession session, String condition, PrintWriter out) throws Exception {
        out.write(serverInfoService.reinstall_lab(condition));
    }

	@RequestMapping(path = "/removeServerDetails")
	public void removeServerDetails(NServer server) throws Exception {
		serverInfoService.addServerDetails(server);
	}

	@RequestMapping(path = "/updateServerInfo")
	public String updateServerInfo(String model) throws Exception {
		return "addServerInfo";
	}

	@RequestMapping(path = "/cancel")
	public void cancel(Model model, HttpSession session, String condition, PrintWriter out) throws Exception {
		out.write(serverInfoService.cancelNew(condition));
	}

	public ServerInfoService getServerInfoService() {
		return serverInfoService;
	}

	public void setServerInfoService(ServerInfoService serverInfoService) {
		this.serverInfoService = serverInfoService;
	}
}
