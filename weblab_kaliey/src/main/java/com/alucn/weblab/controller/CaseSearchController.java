package com.alucn.weblab.controller;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alucn.casemanager.server.common.CaseConfigurationCache;
import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.weblab.service.CaseSearchService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author haiqiw
 * 2017年8月7日 上午10:10:05
 * desc:CaseSearchController
 */
@Controller
public class CaseSearchController {

	@Autowired(required=true)
	private CaseSearchService caseSearchService;
	
	
	@RequestMapping(path = "/openFakeServer")
	@ResponseBody
	public String openFakeServer(Model model) throws Exception{
		JSONArray infos = new JSONArray();
		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21E\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverType\": \"Group\",\"serverMate\": \"Primary\",\"mateServer\": \"BJRMS21F\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Dead\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21C\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverType\": \"Line\",\"serverMate\": \"Primary\",\"mateServer\": \"BJRMS21D\",\"setName\": \"set2\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Idle\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21B\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverType\": \"Group\",\"serverMate\": \"Standalone\",\"mateServer\": \"N\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21D\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverType\": \"Line\",\"serverMate\": \"Secondary\",\"mateServer\": \"BJRMS21C\",\"setName\": \"set2\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Finished\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21A\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverType\": \"Line\",\"serverMate\": \"Standalone\",\"mateServer\": \"N\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Idle\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21F\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverType\": \"Group\",\"serverMate\": \"Secondary\",\"mateServer\": \"BJRMS21E\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Idle\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
		for(int i=0; i<infos.size(); i++){
			CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock,false,infos.getJSONObject(i).getJSONObject(Constant.BODY));
		}
		return "success";
	}
	@RequestMapping(path = "/searchInfo")
	public String searchInfo(Model model) throws Exception{
		model.addAllAttributes(caseSearchService.getCaseSearch());
		return "caseSearch";
	}
	
	@RequestMapping(path = "/searchCaseInfo")
	@ResponseBody
	public Map<String,Object> searchCaseInfo(HttpSession session, String condition,HttpServletRequest request ) throws Exception{
		System.err.println(condition);
		String limit = request.getParameter("limit")==null?"":request.getParameter("limit").toString().trim();
		String offset = request.getParameter("offset")==null?"":request.getParameter("offset").toString().trim();
		String caseName = request.getParameter("caseName")==null?"":request.getParameter("caseName").toString().trim();
		
		Map<String,Object> returnMap = new HashMap<String,Object>();
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("limit", limit);
		paramMap.put("offset", offset);
		paramMap.put("caseName", caseName);
		
		returnMap.put("total",(String)caseSearchService.searchCaseInfo(paramMap,condition, session.getAttribute("auth").toString(),"total"));
		returnMap.put("rows", (ArrayList<HashMap<String, Object>>)caseSearchService.searchCaseInfo(paramMap,condition, session.getAttribute("auth").toString(),"rows"));
		return returnMap;
	}
	@RequestMapping(path = "/searchCaseRunLog")
	@ResponseBody
	public Map<String,Object> searchCaseRunLog(HttpSession session,HttpServletRequest request ) throws Exception{
		//System.err.println(condition);
		String limit = request.getParameter("limit")==null?"":request.getParameter("limit").toString().trim();
		String offset = request.getParameter("offset")==null?"":request.getParameter("offset").toString().trim();
		
		Map<String,Object> returnMap = new HashMap<String,Object>();
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("limit", limit);
		paramMap.put("offset", offset);
		
		returnMap.put("total",(String)caseSearchService.searchCaseRunLogInfo(paramMap, session.getAttribute("auth").toString(),"total"));
		returnMap.put("rows", (ArrayList<HashMap<String, Object>>)caseSearchService.searchCaseRunLogInfo(paramMap, session.getAttribute("auth").toString(),"rows"));
		return returnMap;
	}
	@RequestMapping(path = "/searchCaseRunLogInfo")
	public String searchCaseRunLogInfo(Model model,HttpServletRequest request ) throws Exception{
		String int_id = request.getParameter("int_id")==null?"":request.getParameter("int_id").toString().trim();
		//System.err.println(int_id);
		if(int_id==null &&"".equals(int_id)) {
			return "caseSearch";
		}
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("int_id", int_id);
		ArrayList<HashMap<String, Object>> searchCaseRunLogInfoById = caseSearchService.searchCaseRunLogInfoById(paramMap);
		HashMap<String,Object> cMap = new LinkedHashMap();
		
		for (HashMap<String, Object> hashMap : searchCaseRunLogInfoById) {
			String condition = (String) hashMap.get("condition");
			String[] split = condition.split(";");
			cMap.put("dataSource", split[0]);
			cMap.put("release", split[1]);
			cMap.put("customer", split[2]);
			cMap.put("base_data", split[3]);
			cMap.put("mate", split[4]);
			cMap.put("lab_number", split[5]);
			cMap.put("special_data", split[6]);
			cMap.put("porting_release", split[7]);
			cMap.put("case_status", split[8]);
			cMap.put("feature_number", split[9]);
			cMap.put("author", split[10]);
			cMap.put("server", split[11]);
			//ccMap.put("condition_info", cMap);
			hashMap.put("condition", cMap);
			
			paramMap.put("case_status", true);
			ArrayList<HashMap<String, Object>> case_bak = caseSearchService.searchCaseRunLogCaseInfoById(paramMap);
			model.addAttribute("scase", case_bak);
			model.addAttribute("scase_count", case_bak.size());
			
			paramMap.put("case_status", false);
			ArrayList<HashMap<String, Object>> case_bak_f = caseSearchService.searchCaseRunLogCaseInfoById(paramMap);
			model.addAttribute("fcase", case_bak_f);
			model.addAttribute("fcase_count", case_bak_f.size());
			
		  	/*String s_case = (String)hashMap.get("s_case");
		    if(s_case!="[]") {
				String[] split2 = s_case.replace("[", "").replace("]", "").replace("{", "").split("},");
				List<String> asList = Arrays.asList(split2);
				List<Map<String, Object>> sList = new ArrayList<Map<String, Object>>();
				for (String string : asList) {
					//System.out.println("string:============"+string);
					HashMap<String,Object> ccMap = new HashMap<>();
					String[] split3 = string.replace("}", "").split(",");
					
					for (String string2 : split3) {
						if(!"".equals(string2)) {
							String[] split4 = string2.split("=");
							if(split4.length==1) {
								ccMap.put(split4[0].trim(), "");
							}
							if(split4.length==2) {
								ccMap.put(split4[0].trim(), split4[1].trim());
							}
						}
					}
					System.err.println("cc:===="+ccMap);
					if(!ccMap.isEmpty()) {
						sList.add(ccMap);
					}
				}
				System.err.println("sList:========"+sList);
				model.addAttribute("scase", sList);
				model.addAttribute("scase_count", sList.size());
			}else {
				model.addAttribute("scase", "");
				model.addAttribute("scase_count", 0);
			}*/
		    
		    /*String server_info_str = (String)hashMap.get("server_info");
		    JSONObject server_info = JSONObject.fromObject(server_info_str.trim());
		    System.err.println("server_info:========="+server_info);
		    model.addAttribute("server_info",server_info);*/
			
		    ArrayList<HashMap<String, Object>> serverById = caseSearchService.searchCaseRunLogCaseServerById(paramMap);
		    model.addAttribute("server_info",serverById);
		    
		}
		
		
		//searchCaseRunLogInfoById.add(ccMap);
		System.err.println("searchCaseRunLogInfoById:============"+searchCaseRunLogInfoById);
		model.addAttribute("searchCaseRunLogInfoById",searchCaseRunLogInfoById);
		return "caseSearchInfo";
	}
	@RequestMapping(path = "/rerunning")
	@ResponseBody
	public Map<String,Object> rerunning(HttpSession session,HttpServletRequest request ){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		
		String ids = request.getParameter("ids")==null?"":request.getParameter("ids").toString().trim();
		String condition = request.getParameter("condition")==null?"":request.getParameter("condition").toString().trim();
		String selectAllflag = request.getParameter("flag")==null?"":request.getParameter("flag").toString().trim();
		System.out.println("ids============="+ids);
		System.out.println("condition============="+condition);
		System.out.println("selectAllflag============="+selectAllflag);
		System.out.println("\"false\".endsWith(selectAllflag)============="+"false".endsWith(selectAllflag));
		if("".equals(ids)&&"false".endsWith(selectAllflag)) {
			returnMap.put("msg", "please checked some case");
			return returnMap;
		}
		if("".equals(condition)) {
			returnMap.put("msg", "please checked some condition");
			return returnMap;
		}
		
		String login = (String) session.getAttribute("login");
		System.out.println("login:============"+login);
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("ids", ids);
		paramMap.put("condition", condition);
		paramMap.put("login", login);
		paramMap.put("selectAllflag", selectAllflag);
		paramMap.put("limit", "");
		paramMap.put("offset", "");
		paramMap.put("caseName", "");
		try {
			if("true".endsWith(selectAllflag)) {
				Map<String,Object> queryMap = new HashMap<String,Object>();
				ArrayList<HashMap<String, Object>> caseInfo = (ArrayList<HashMap<String, Object>>)caseSearchService.searchCaseInfo(queryMap,condition, session.getAttribute("auth").toString(),"condition");
				int i=0;
				ids="";
				for (HashMap<String, Object> hashMap : caseInfo) {
					String case_name = (String) hashMap.get("case_name");
					if(i==0){
						ids+=case_name;
					}else{
						ids+=","+case_name;
					}
					i++;
				}
				paramMap.put("ids", ids);
			}
			/*ExecutorService executor = Executors.newCachedThreadPool();
			Callable task = new Callable<Map<String, Object>>() {
				@Override
				public Map<String, Object> call() throws Exception {
					return caseSearchService.insertToDistributeCasesTbl(paramMap);
				}
			};
			Map<String, Object> casesTbl = new HashMap<String, Object>();
			try {
				Future<Map<String, Object>> future = executor.submit(task);
				if(future.get().size()>0) {
					casesTbl=future.get();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				executor.shutdown();
			}*/
			Map<String, Object> casesTbl = caseSearchService.insertToDistributeCasesTbl(paramMap);
			if((boolean) casesTbl.get("result")) {
				returnMap.put("s_case", casesTbl.get("s_case"));
				returnMap.put("f_case", casesTbl.get("f_case"));
			}else {
				returnMap.put("msg", casesTbl.get("msg"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnMap;
	}
}
