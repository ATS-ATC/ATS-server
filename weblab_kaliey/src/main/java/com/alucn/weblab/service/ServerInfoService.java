package com.alucn.weblab.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alucn.casemanager.server.common.CaseConfigurationCache;
import com.alucn.casemanager.server.common.ConfigProperites;
import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.model.ServerMate;
import com.alucn.casemanager.server.common.model.ServerSort;
import com.alucn.casemanager.server.common.model.ServerType;
import com.alucn.casemanager.server.common.util.Fifowriter;
import com.alucn.casemanager.server.common.util.FileUtil;
import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.weblab.dao.impl.ServerInfoDaoImpl;
import com.alucn.weblab.dao.impl.UserDaoImpl;
import com.alucn.weblab.model.NServer;
import com.alucn.weblab.model.Server;
import com.alucn.weblab.socket.TcpClient;
import com.alucn.weblab.utils.KalieyMysqlUtil;
import com.alucn.weblab.utils.SocketClientConn;
import com.alucn.weblab.utils.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service("serverInfoService")
public class ServerInfoService {
	private static Logger logger = Logger.getLogger(ServerInfoService.class);
	
	@Autowired(required=true)
	private ServerInfoDaoImpl serverInfoDaoImpl;
	
	private KalieyMysqlUtil jdbc = KalieyMysqlUtil.getInstance();
	
	public Map<String,Set<Map<String,JSONObject>>> getServerInfoNosort(){
		/*JSONArray infos = new JSONArray();
//		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21H\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverTpye\": \"L\",\"serverMate\": \"P\",\"mateServer\": \"BJRMS21I\",\"setName\": \"set2\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
//		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21I\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverTpye\": \"L\",\"serverMate\": \"S\",\"mateServer\": \"BJRMS21H\",\"setName\": \"set2\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
//		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21G\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverTpye\": \"L\",\"serverMate\": \"N\",\"mateServer\": \"N\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21A\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverTpye\": \"L\",\"serverMate\": \"N\",\"mateServer\": \"N\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21B\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverTpye\": \"G\",\"serverMate\": \"N\",\"mateServer\": \"N\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21C\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverTpye\": \"L\",\"serverMate\": \"P\",\"mateServer\": \"BJRMS21D\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21D\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverTpye\": \"L\",\"serverMate\": \"S\",\"mateServer\": \"BJRMS21C\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21E\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverTpye\": \"G\",\"serverMate\": \"P\",\"mateServer\": \"BJRMS21F\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21F\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverTpye\": \"G\",\"serverMate\": \"S\",\"mateServer\": \"BJRMS21E\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");*/
		JSONArray infos = CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock, true, null);
		Map<String,Set<Map<String,JSONObject>>> setMap = new HashMap<String,Set<Map<String,JSONObject>>>();
		for(int i=0; i<infos.size(); i++){
			JSONObject info = infos.getJSONObject(i);
			JSONObject lib = info.getJSONObject(Constant.LAB);
			String setName = lib.getString(Constant.SETNAME);
			String serverName = lib.getString(Constant.SERVERNAME);
			String mateServer = lib.getString(Constant.MATESERVER);
			if(setMap.get(setName)==null){
				Set<Map<String,JSONObject>> serverSet = new HashSet<Map<String,JSONObject>>();
				if(mateServer.equals("N")){
					Map<String,JSONObject> serverMap = new TreeMap<String,JSONObject>();
					serverMap.put(serverName, info);
					serverSet.add(serverMap);
					setMap.put(setName, serverSet);
				}else{
					Map<String,JSONObject> mateMap = new TreeMap<String,JSONObject>();
					mateMap.put(mateServer, info);
					serverSet.add(mateMap);
					setMap.put(setName, serverSet);
				}
			}else{
				Set<Map<String,JSONObject>> serverSet = setMap.get(setName);
				Iterator<Map<String, JSONObject>> iterator = serverSet.iterator();
				boolean isMate = false;
				while(iterator.hasNext()){
					Map<String,JSONObject> mateOrServerMap = iterator.next();
					if(mateOrServerMap.get(serverName)!=null){
						JSONObject mateinfo = mateOrServerMap.get(serverName);
						mateOrServerMap.put(mateServer, mateinfo);
						mateOrServerMap.put(serverName, info);
						isMate = true;
					}
				}
				if(!isMate){
					if(mateServer.equals("N")){
						Map<String,JSONObject> serverMap = new TreeMap<String,JSONObject>();
						serverMap.put(serverName, info);
						serverSet.add(serverMap);
						setMap.put(setName, serverSet);
					}else{
						Map<String,JSONObject> mateMap = new TreeMap<String,JSONObject>();
						mateMap.put(mateServer, info);
						serverSet.add(mateMap);
						setMap.put(setName, serverSet);
					}
				}
			}
		}
		
		return setMap;
	}
	public Map<String,Set<Map<String,JSONObject>>> getNewServerInfoNosort(){
		JSONArray infos = null;
		try {
			infos = SocketClientConn.getLabStatus();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String,Set<Map<String,JSONObject>>> setMap = new HashMap<String,Set<Map<String,JSONObject>>>();
		for(int i=0; i<infos.size(); i++){
			JSONObject info = infos.getJSONObject(i).getJSONObject("body");;
			JSONObject lib = info.getJSONObject(Constant.LAB);
			String setName = lib.getString(Constant.SETNAME);
			String serverName = lib.getString(Constant.SERVERNAME);
			String mateServer = lib.getString(Constant.MATESERVER);
			if(setMap.get(setName)==null){
				Set<Map<String,JSONObject>> serverSet = new HashSet<Map<String,JSONObject>>();
				if(mateServer.equals("N")){
					Map<String,JSONObject> serverMap = new TreeMap<String,JSONObject>();
					serverMap.put(serverName, info);
					serverSet.add(serverMap);
					setMap.put(setName, serverSet);
				}else{
					Map<String,JSONObject> mateMap = new TreeMap<String,JSONObject>();
					mateMap.put(mateServer, info);
					serverSet.add(mateMap);
					setMap.put(setName, serverSet);
				}
			}else{
				Set<Map<String,JSONObject>> serverSet = setMap.get(setName);
				Iterator<Map<String, JSONObject>> iterator = serverSet.iterator();
				boolean isMate = false;
				while(iterator.hasNext()){
					Map<String,JSONObject> mateOrServerMap = iterator.next();
					if(mateOrServerMap.get(serverName)!=null){
						JSONObject mateinfo = mateOrServerMap.get(serverName);
						mateOrServerMap.put(mateServer, mateinfo);
						mateOrServerMap.put(serverName, info);
						isMate = true;
					}
				}
				if(!isMate){
					if(mateServer.equals("N")){
						Map<String,JSONObject> serverMap = new TreeMap<String,JSONObject>();
						serverMap.put(serverName, info);
						serverSet.add(serverMap);
						setMap.put(setName, serverSet);
					}else{
						Map<String,JSONObject> mateMap = new TreeMap<String,JSONObject>();
						mateMap.put(mateServer, info);
						serverSet.add(mateMap);
						setMap.put(setName, serverSet);
					}
				}
			}
		}
		
		return setMap;
	}
	
	public Map<String,Set<ServerSort>> getServerInfo(){
		/*JSONArray infos = new JSONArray();
//		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21H\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverTpye\": \"L\",\"serverMate\": \"P\",\"mateServer\": \"BJRMS21I\",\"setName\": \"set2\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
//		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21I\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverTpye\": \"L\",\"serverMate\": \"S\",\"mateServer\": \"BJRMS21H\",\"setName\": \"set2\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
//		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21G\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverTpye\": \"L\",\"serverMate\": \"N\",\"mateServer\": \"N\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21A\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverTpye\": \"L\",\"serverMate\": \"N\",\"mateServer\": \"N\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21B\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverTpye\": \"G\",\"serverMate\": \"N\",\"mateServer\": \"N\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21C\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverTpye\": \"L\",\"serverMate\": \"P\",\"mateServer\": \"BJRMS21D\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21D\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverTpye\": \"L\",\"serverMate\": \"S\",\"mateServer\": \"BJRMS21C\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21E\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverTpye\": \"G\",\"serverMate\": \"P\",\"mateServer\": \"BJRMS21F\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21F\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverTpye\": \"G\",\"serverMate\": \"S\",\"mateServer\": \"BJRMS21E\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");*/
		
		JSONArray infos = CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock, true, null);
		//remove server
		for(int i=0; i<infos.size();i++){
			JSONObject tmpJsonObject = (JSONObject) infos.get(i);
			if(CaseConfigurationCache.removeListServer.contains(tmpJsonObject.getJSONObject(Constant.LAB).getString(Constant.SERVERNAME))){
				infos.remove(i);
			}
		}
		Map<String,Set<ServerSort>> setMap = new HashMap<String,Set<ServerSort>>();
		/*JSONObject taskStatus = new JSONObject();
		for(int i=0; i<infos.size(); i++){
			JSONObject info = infos.getJSONObject(i);
			JSONObject lib = info.getJSONObject(Constant.LAB);
			String serverType = lib.getString(Constant.SERVERTYPE);
			String serverMate = lib.getString(Constant.SERVERMATE);
			if(serverType.equals(ServerType.LINE.getName()) && (serverMate.equals(ServerMate.PRIMARY.getName()) || serverMate.equals(ServerMate.N.getName()))){
				taskStatus = info.getJSONObject(Constant.TASKSTATUS);
			}
		}
		for(int i=0; i<infos.size(); i++){
			JSONObject info = infos.getJSONObject(i);
			JSONObject lib = info.getJSONObject(Constant.LAB);
			String serverType = lib.getString(Constant.SERVERTYPE);
			if(!serverType.equals(ServerType.STANDALONE.getName())){
				info.put(Constant.TASKSTATUS, taskStatus);
			}
			
		}*/
		for(int i=0; i<infos.size(); i++){
			JSONObject info = infos.getJSONObject(i);
			JSONObject lib = info.getJSONObject(Constant.LAB);
			String setName = lib.getString(Constant.SETNAME);
			String serverName = lib.getString(Constant.SERVERNAME);
			String mateServer = lib.getString(Constant.MATESERVER);
			if(setMap.get(setName)==null){
				Set<ServerSort> serverSet = new TreeSet<ServerSort>();
				if(mateServer.equals("N")){
					Map<String,JSONObject> serverMap = new TreeMap<String,JSONObject>();
					ServerSort s = new ServerSort();
					serverMap.put(serverName, info);
					s.setMap(serverMap);
					serverSet.add(s);
					setMap.put(setName, serverSet);
				}else{
					Map<String,JSONObject> mateMap = new TreeMap<String,JSONObject>();
					ServerSort s = new ServerSort();
					mateMap.put(mateServer, info);
					s.setMap(mateMap);
					serverSet.add(s);
					setMap.put(setName, serverSet);
				}
			}else{
				Set<ServerSort> serverSet = setMap.get(setName);
				Iterator<ServerSort> iterator = serverSet.iterator();
				boolean isMate = false;
				while(iterator.hasNext()){
					Map<String,JSONObject> mateOrServerMap = iterator.next().getMap();
					if(mateOrServerMap.get(serverName)!=null){
						JSONObject mateinfo = mateOrServerMap.get(serverName);
						mateOrServerMap.put(mateServer, mateinfo);
						mateOrServerMap.put(serverName, info);
						isMate = true;
					}
				}
				if(!isMate){
					if(mateServer.equals("N")){
						Map<String,JSONObject> serverMap = new TreeMap<String,JSONObject>();
						ServerSort s = new ServerSort();
						serverMap.put(serverName, info);
						s.setMap(serverMap);
						serverSet.add(s);
						setMap.put(setName, serverSet);
					}else{
						Map<String,JSONObject> mateMap = new TreeMap<String,JSONObject>();
						ServerSort s = new ServerSort();
						mateMap.put(mateServer, info);
						s.setMap(mateMap);
						serverSet.add(s);
						setMap.put(setName, serverSet);
					}
				}
			}
		}
		return setMap;
	}
	public Map<String,Set<ServerSort>> getNewServerInfo(){
		/*JSONArray infos = new JSONArray();
//		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21H\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverTpye\": \"L\",\"serverMate\": \"P\",\"mateServer\": \"BJRMS21I\",\"setName\": \"set2\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
//		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21I\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverTpye\": \"L\",\"serverMate\": \"S\",\"mateServer\": \"BJRMS21H\",\"setName\": \"set2\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
//		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21G\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverTpye\": \"L\",\"serverMate\": \"N\",\"mateServer\": \"N\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21A\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverTpye\": \"L\",\"serverMate\": \"N\",\"mateServer\": \"N\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21B\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverTpye\": \"G\",\"serverMate\": \"N\",\"mateServer\": \"N\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21C\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverTpye\": \"L\",\"serverMate\": \"P\",\"mateServer\": \"BJRMS21D\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21D\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverTpye\": \"L\",\"serverMate\": \"S\",\"mateServer\": \"BJRMS21C\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21E\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverTpye\": \"G\",\"serverMate\": \"P\",\"mateServer\": \"BJRMS21F\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"serverName\":\"BJRMS21F\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverTpye\": \"G\",\"serverMate\": \"S\",\"mateServer\": \"BJRMS21E\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");*/
		
		JSONArray infos = null;
		try {
			infos = SocketClientConn.getLabStatus();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//remove server
		/*for(int i=0; i<infos.size();i++){
			JSONObject tmpJsonObject = (JSONObject) infos.get(i);
			if(CaseConfigurationCache.removeListServer.contains(tmpJsonObject.getJSONObject(Constant.LAB).getString(Constant.SERVERNAME))){
				infos.remove(i);
			}
		}*/
		Map<String,Set<ServerSort>> setMap = new HashMap<String,Set<ServerSort>>();
		/*JSONObject taskStatus = new JSONObject();
		for(int i=0; i<infos.size(); i++){
			JSONObject info = infos.getJSONObject(i);
			JSONObject lib = info.getJSONObject(Constant.LAB);
			String serverType = lib.getString(Constant.SERVERTYPE);
			String serverMate = lib.getString(Constant.SERVERMATE);
			if(serverType.equals(ServerType.LINE.getName()) && (serverMate.equals(ServerMate.PRIMARY.getName()) || serverMate.equals(ServerMate.N.getName()))){
				taskStatus = info.getJSONObject(Constant.TASKSTATUS);
			}
		}
		for(int i=0; i<infos.size(); i++){
			JSONObject info = infos.getJSONObject(i);
			JSONObject lib = info.getJSONObject(Constant.LAB);
			String serverType = lib.getString(Constant.SERVERTYPE);
			if(!serverType.equals(ServerType.STANDALONE.getName())){
				info.put(Constant.TASKSTATUS, taskStatus);
			}
			
		}*/
		for(int i=0; i<infos.size(); i++){
			JSONObject info = infos.getJSONObject(i).getJSONObject("body");
			JSONObject lib = info.getJSONObject(Constant.LAB);
			String setName = lib.getString(Constant.SETNAME);
			String serverName = lib.getString(Constant.SERVERNAME);
			String mateServer = lib.getString(Constant.MATESERVER);
			if(setMap.get(setName)==null){
				Set<ServerSort> serverSet = new TreeSet<ServerSort>();
				if(mateServer.equals("N")){
					Map<String,JSONObject> serverMap = new TreeMap<String,JSONObject>();
					ServerSort s = new ServerSort();
					serverMap.put(serverName, info);
					s.setMap(serverMap);
					serverSet.add(s);
					setMap.put(setName, serverSet);
				}else{
					Map<String,JSONObject> mateMap = new TreeMap<String,JSONObject>();
					ServerSort s = new ServerSort();
					mateMap.put(mateServer, info);
					s.setMap(mateMap);
					serverSet.add(s);
					setMap.put(setName, serverSet);
				}
			}else{
				Set<ServerSort> serverSet = setMap.get(setName);
				Iterator<ServerSort> iterator = serverSet.iterator();
				boolean isMate = false;
				while(iterator.hasNext()){
					Map<String,JSONObject> mateOrServerMap = iterator.next().getMap();
					if(mateOrServerMap.get(serverName)!=null){
						JSONObject mateinfo = mateOrServerMap.get(serverName);
						mateOrServerMap.put(mateServer, mateinfo);
						mateOrServerMap.put(serverName, info);
						isMate = true;
					}
				}
				if(!isMate){
					if(mateServer.equals("N")){
						Map<String,JSONObject> serverMap = new TreeMap<String,JSONObject>();
						ServerSort s = new ServerSort();
						serverMap.put(serverName, info);
						s.setMap(serverMap);
						serverSet.add(s);
						setMap.put(setName, serverSet);
					}else{
						Map<String,JSONObject> mateMap = new TreeMap<String,JSONObject>();
						ServerSort s = new ServerSort();
						mateMap.put(mateServer, info);
						s.setMap(mateMap);
						serverSet.add(s);
						setMap.put(setName, serverSet);
					}
				}
			}
		}
		return setMap;
	}
	
	/*public static void main(String[] args) {
		System.out.println(new ServerInfoService().getServerInfo().size());
		System.out.println(new ServerInfoService().getServerInfo().get("set1").size());
		System.out.println(new ServerInfoService().getServerInfo().toString());
		Set<Map<String,JSONObject>> serverSet = new ServerInfoService().getServerInfo().get("set1");
		Iterator<Map<String, JSONObject>> iterator = serverSet.iterator();
		while(iterator.hasNext()){
			Map<String,JSONObject> mateOrServerMap = iterator.next();
			System.out.println(mateOrServerMap.size()+mateOrServerMap.values().toString());
		}
	}*/
	
	
	/*public void addServerDetails(Server server) throws Exception{
		String filePath = ConfigProperites.getInstance().getCaseClientSftpSourcePath();
		Fifowriter.writerFile(filePath, Constant.SPAANDRTDB, JSONObject.fromObject(server).toString());
		String sftpTargetPath = ConfigProperites.getInstance().getCaseClientSftpTargetPath();
		String shellName = ConfigProperites.getInstance().getCaseClientSftpSendShellName();
		String userName = ConfigProperites.getInstance().getCaseClientSftpUserName();
		String password = ConfigProperites.getInstance().getCaseClientSftpPassword();
		int port = Integer.parseInt(ConfigProperites.getInstance().getCaseClientSftpPort());
		FileUtil.upLoadFile(FileUtil.createSession(server.getServerIp(), userName, password, port), filePath, sftpTargetPath);
		String[] cmds = new String[] {"sh "+sftpTargetPath+File.separator+shellName};
		String[] result = FileUtil.execShellCmdBySSH(server.getServerIp(), port, userName, password, cmds);
		for(String str : result){
			System.out.println(str);
		}
	}*/
	
	public String removeServerInfo(String serverName){
		try {
		    JSONArray currKeyStatus = CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock,true,null);
		    for(int i=0; i<currKeyStatus.size();i++){
                JSONObject tmpJsonObject = currKeyStatus.getJSONObject(i);
                String server_name = tmpJsonObject.getJSONObject(Constant.LAB).getString(Constant.SERVERNAME);
                if(serverName.equals(server_name)){
                	String status = tmpJsonObject.getJSONObject(Constant.TASKSTATUS).getString(Constant.STATUS);
                	if(!status.equals(Constant.CASESTATUSDEAD)){
                		return "server is running,please stop it!";
                	}else{
                		CaseConfigurationCache.removeListServer.add(server_name);
                	}
                }
            }
			String cmd = "rm -rf /home/huanglei/*"+serverName+"*";
			String[] cmds = {"/bin/sh","-c",cmd};
			Process process = Runtime.getRuntime().exec(cmds);
			int result = process.waitFor();
			if (process != null) {
				process.destroy();
		    }
			if(0 != result){
				return "remove server failed!";
			}else{
				return "remove server success!";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public void removeServerDetails(){
	}
	
	public void updateServerInfo(){
	}
	
	public String cancel(String serverName){
		try {
			String cmd = "sh /home/huanglei/autoPreLab/stopClient.sh "+serverName;
			String[] cmds = {
					"/bin/sh",
					"-c",
					cmd
			};
			Process process = Runtime.getRuntime().exec(cmds);
			int result = process.waitFor();
			if (process != null) {
				process.destroy();
		    }
			if(0 != result){
				return "cancel failed!";
			}else{
				return "cancel success!";
			}
		} catch (Exception e) {}
		return "";
	}

	public void addServerDetails(NServer server) throws Exception {
		String filePath = ConfigProperites.getInstance().getCaseClientSftpSourcePath();//""
		logger.info("addServerDetails >> filePath:==========="+filePath);
		Fifowriter.writerFile(filePath, Constant.SPAANDRTDB, JSONObject.fromObject(server).toString());//spaANDrtdb.txt
		
		String sftpTargetPath = ConfigProperites.getInstance().getCaseClientSftpTargetPath();//""
		String shellName = ConfigProperites.getInstance().getCaseClientSftpSendShellName();//init.sh
		String userName = ConfigProperites.getInstance().getCaseClientSftpUserName();//root
		String password = ConfigProperites.getInstance().getCaseClientSftpPassword();//r00t
		int port = Integer.parseInt(ConfigProperites.getInstance().getCaseClientSftpPort());//22
		
		logger.info("addServerDetails >> upLoadFile:==========="+sftpTargetPath+"/"+shellName+"/"+userName+"/"+password+"/"+port);
		FileUtil.upLoadFile(FileUtil.createSession(server.getServerIp(), userName, password, port), filePath, sftpTargetPath);
		String[] cmds = new String[] {"sh "+sftpTargetPath+File.separator+shellName};
		logger.info("addServerDetails >> cmds:============="+cmds.toString());
		String[] result = FileUtil.execShellCmdBySSH(server.getServerIp(), port, userName, password, cmds);
		for(String str : result){
			System.out.println(str);
		}
	}

	public String addLabStatus(String status,String type, String log, String enwtpps, String ss7, String labname, String db, String free, String ips, String ptversion, String spa, String deptid, String createid, String createtime, String ainsflag) {
		String result ="success";
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = null;
		try {
			jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		} catch (Exception e1) {
			result ="fail";
			e1.printStackTrace();
		}*/
		//String fdb = db.replace("\"", "").replace("[", "").replace("]", "");
		//String fspa = spa.replace("\"", "").replace("[", "").replace("]", "");
		String sql = "insert into kaliey.n_add_lab_status(status,type,log,enwtpps,ss7,labname,db,free,ips,ptversion,spa,deptid,createid,createtime,modifytime,insflag) "
				+ "values('"+status+"','"+type+"','"+log+"','"+enwtpps+"','"+ss7+"','"+labname+"','"+db+"','"+free+"','"+ips+"','"+ptversion+"','"+spa+"','"+deptid+"','"+createid+"','"+createtime+"','','"+ainsflag+"')";
		try {
			System.err.println("addLabStatus >> "+sql);
			serverInfoDaoImpl.insert(jdbc, sql);
		} catch (Exception e) {
			result ="fail";
			e.printStackTrace();
		}
		return result; 
	}

	public void editLabStatus(String status, String log,String labname,String stateflag,String createtime) throws Exception {
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		String sql="";
		if(stateflag!=null&&!"".equals(stateflag)) {
			sql = "update kaliey.n_add_lab_status set status='"+status+"',log='"+log+"',stateflag='"+stateflag+"',modifytime=now() where stateflag=0 and labname ='"+labname+"' and createtime='"+createtime+"'";
		}else {
			sql = "update kaliey.n_add_lab_status set status='"+status+"',log='"+log+"',modifytime=now() where stateflag=0 and labname ='"+labname+"' and createtime='"+createtime+"'";
		}
		System.err.println("editLabStatus >> "+sql);
		serverInfoDaoImpl.insert(jdbc, sql);
		
	}

	public ArrayList<HashMap<String, Object>> getLabLogJson(String limit, String offset, String labname, List<String> deptids,boolean hasRole) throws Exception {
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		String dept_ids = StringUtil.formatSplitList(deptids);
		
		String sql = "select * from kaliey.n_add_lab_status "
				+ "where 1=1 ";
				if(!hasRole) {
					sql = sql + "and deptid in ("+dept_ids+") ";
				}
		if(labname!=null && !"".equals(labname)) {
			sql=sql+"and a.labname like '%"+labname+"%' ";
		}
		sql=sql+"order by createtime desc limit "+offset+","+limit;
		System.out.println("ServerInfoService >> getAllDeptInfoJson >> sql "+sql);
		ArrayList<HashMap<String, Object>> query = serverInfoDaoImpl.query(jdbc, sql);
		return query;
	}
	public int getLabLogJsonCount(String labname, List<String> deptids) throws Exception {
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		String dept_ids = StringUtil.formatSplitList(deptids);
		String sql = "select count(1) ccount from kaliey.n_add_lab_status "
				+ "where 1=1 "
				+ "and deptid in ("+dept_ids+") ";
		if(labname!=null && !"".equals(labname)) {
			sql=sql+"and a.labname like '%"+labname+"%' ";
		}
		System.out.println("ServerInfoService >> getAllDeptInfoJson >> sql "+sql);
		ArrayList<HashMap<String, Object>> query = serverInfoDaoImpl.query(jdbc, sql);
		if(query.size()>0) {
			return Integer.parseInt((String)query.get(0).get("ccount"));
		}else {
			return 0; 
		}
	}

	public ArrayList<HashMap<String, Object>> getServerStatusLogJson(String limit, String offset, String serverName, List<String> deptids, boolean hasRole) throws Exception {
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		String sql = "select * from kaliey.n_lab_status_time "
				+ "where 1=1 ";
		if(!hasRole) {
			String dept_ids = StringUtil.formatSplitList(deptids);
			sql=sql+"and groupid in ("+dept_ids+") ";
		}
		if(serverName!=null && !"".equals(serverName)) {
			sql=sql+"and labname='"+serverName+"' ";
		}
		sql=sql+"order by endtime desc limit "+offset+","+limit;
		System.out.println("ServerInfoService >> getServerStatusLogJson >> sql "+sql);
		ArrayList<HashMap<String, Object>> query = serverInfoDaoImpl.query(jdbc, sql);
		return query;
	}
	public int getServerStatusLogJsonCount(String serverName, List<String> deptids, boolean hasRole) throws Exception {
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		String sql = "select count(*) ccount from kaliey.n_lab_status_time "
				+ "where 1=1 ";
		if(!hasRole) {
			String dept_ids = StringUtil.formatSplitList(deptids);
			sql=sql+"and groupidin("+dept_ids+") ";
		}
		if(serverName!=null && !"".equals(serverName)) {
			sql=sql+"and labname='"+serverName+"' ";
		}
		//sql=sql+"order by endtime desc limit "+offset+","+limit;
		System.err.println("ServerInfoService >> getServerStatusLogJson >> sql "+sql);
		ArrayList<HashMap<String, Object>> query = serverInfoDaoImpl.query(jdbc, sql);
		if(query.size()>0) {
			return Integer.parseInt((String)query.get(0).get("ccount"));
		}else {
			return 0; 
		}
	}

}
