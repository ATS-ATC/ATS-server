package com.alucn.weblab.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.weblab.dao.impl.SpaAndRtdbDaoImpl;

/**
 * @author haiqiw
 * 2017年6月6日 下午7:19:07
 * desc:SpaAndRtdbManService
 */
@Service("spaAndRtdbManService")
public class SpaAndRtdbManService {
	
	@Autowired(required=true)
	private SpaAndRtdbDaoImpl spaAndRtdbDaoImpl;
	private List<String> SPA = new ArrayList<>();
	private List<String> RTDB = new ArrayList<>();
	private Map<String, List<String>> spaAndRt = new HashMap<String, List<String>>();

	public Map<String, List<String>> getSpaAndRtdbInfo() throws Exception{
		SPA.clear();
		RTDB.clear();
		spaAndRt.clear();
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String getSpa = "SELECT spa_name FROM spainfo";
		ArrayList<HashMap<String, Object>> result = spaAndRtdbDaoImpl.query(jdbc, getSpa);
		for(int i=0; i<result.size();i++){
			Map<String, Object> obj = result.get(i);
			for(String key: obj.keySet()){
				SPA.add(obj.get(key).toString());
			}
		}
		
		String getTrdb = "SELECT rtdb_name FROM rtdbinfo";
		result = spaAndRtdbDaoImpl.query(jdbc, getTrdb);
		for(int i=0; i<result.size();i++){
			Map<String, Object> obj = result.get(i);
			for(String key: obj.keySet()){
				RTDB.add(obj.get(key).toString());
			}
		}
		spaAndRt.put("SPA", SPA);
		spaAndRt.put("RTDB", RTDB);
		return spaAndRt;
	}
	
	public String addSpaAndRtdbInfo(String spa, String rtdb){
		try {
			String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
			JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
			String [] spaName = spa.split(",");
			String [] rtdbName = rtdb.split(",");
			for(int i=0; i<spaName.length; i++){
				String insertSpa = "INSERT INTO spainfo (spa_name) VALUES ('"+ spaName[i] +"');";
				spaAndRtdbDaoImpl.insert(jdbc, insertSpa);
			}
			for(int i=0; i<rtdbName.length; i++)
			{
				String insertRtdb = "INSERT INTO rtdbinfo (rtdb_name) VALUES ('"+ rtdbName[i] +"');";
				spaAndRtdbDaoImpl.insert(jdbc, insertRtdb);
			}
		} catch (Exception e) {
			return "Operations Failed!" ;
		}
		return "Operations Succeed!";
	}
	
	public String removeSpaAndRtdbInfo(String spa, String rtdb){
		try {
			String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
			JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
			String [] spaName = spa.split(",");
			String [] rtdbName = rtdb.split(",");
			for(int i=0; i<spaName.length; i++){
				String removeSpa = "DELETE FROM spainfo WHERE  spa_name='"+ spaName[i] +"';";
				spaAndRtdbDaoImpl.delete(jdbc, removeSpa);
			}
			for(int i=0; i<rtdbName.length; i++){
				String remoceRtdb = "DELETE FROM rtdbinfo WHERE rtdb_name='"+ rtdbName[i] +"';";
				spaAndRtdbDaoImpl.delete(jdbc, remoceRtdb);
			}
		} catch (Exception e) {
			return "Operations Failed!" ;
		}
		return "Operations Succeed!";
	}
	
	
	public SpaAndRtdbDaoImpl getSpaAndRtdbDaoImpl() {
		return spaAndRtdbDaoImpl;
	}

	public void setSpaAndRtdbDaoImpl(SpaAndRtdbDaoImpl spaAndRtdbDaoImpl) {
		this.spaAndRtdbDaoImpl = spaAndRtdbDaoImpl;
	}
}
