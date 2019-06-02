package com.alucn.weblab.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alucn.weblab.dao.impl.SpaAndRtdbDaoImpl;
import com.alucn.weblab.utils.JDBCHelper;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


@Service("spaAndRtdbManService")
public class SpaAndRtdbManService {
	
	@Autowired(required=true)
	private SpaAndRtdbDaoImpl spaAndRtdbDaoImpl;
	private List<String> SPA = new ArrayList<>();
	private List<String> RTDB = new ArrayList<>();
	private Map<String, List<String>> spaAndRt = new HashMap<String, List<String>>();

	public JSONObject getSpaAndRtdbInfo() throws Exception{
		
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		String getSpa = "SELECT spa_name FROM kaliey.spainfo order by spa_name";
		JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
		ArrayList<HashMap<String, Object>> result = spaAndRtdbDaoImpl.query(jdbc, getSpa);
		JSONArray spas = new JSONArray().fromObject(result);
		JSONArray new_spas = new JSONArray();
		JSONObject spa = new JSONObject();
		for(int i = 0; i < spas.size(); i++)
		{
		    String key = "spa_name#" + String.valueOf((i%4));
		    if( i != 0 && i%4 == 0)
            {
		        new_spas.add(spa);
		        spa = new JSONObject();
            }
		    spa.put(key, spas.getJSONObject(i).getString("spa_name"));    
		}
		new_spas.add(spa);
		
		for(int i=0; i<result.size();i++){
			Map<String, Object> obj = result.get(i);
			for(String key: obj.keySet()){
				SPA.add(obj.get(key).toString());
			}
		}
		
		String getTrdb = "SELECT rtdb_name FROM kaliey.rtdbinfo order by rtdb_name";
		result = spaAndRtdbDaoImpl.query(jdbc, getTrdb);
		JSONArray rtdbs = new JSONArray().fromObject(result);
        JSONArray new_rtdbs = new JSONArray();
        JSONObject rtdb = new JSONObject();
        for(int i = 0; i < rtdbs.size(); i++)
        {
            String key = "rtdb_name#" + String.valueOf((i%4));
            if( i != 0 && i%4 == 0)
            {
                new_rtdbs.add(rtdb);
                rtdb = new JSONObject();
            }
            rtdb.put(key, rtdbs.getJSONObject(i).getString("rtdb_name")); 
        }
        new_rtdbs.add(rtdb);
        JSONObject spa_db = new JSONObject();
        spa_db.put("SPA", new_spas);
        spa_db.put("RTDB", new_rtdbs);
		return spa_db;
	}
	
	public Map<String, List<String>> getSpaAndRtdbList() throws Exception{
        SPA.clear();
        RTDB.clear();
        spaAndRt.clear();
        /*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
        JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
        String getSpa = "SELECT spa_name FROM kaliey.spainfo order by spa_name";
        JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
        ArrayList<HashMap<String, Object>> result = spaAndRtdbDaoImpl.query(jdbc, getSpa);
        for(int i=0; i<result.size();i++){
            Map<String, Object> obj = result.get(i);
            for(String key: obj.keySet()){
                SPA.add(obj.get(key).toString());
            }
        }
        
        String getTrdb = "SELECT rtdb_name FROM kaliey.rtdbinfo order by rtdb_name";
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
			/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
			JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		    JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
		    if(!"".equals(spa.trim()))
		    {
    			String [] spaName = spa.split(",");
    			for(int i=0; i<spaName.length; i++){
    				String insertSpa = "INSERT INTO kaliey.spainfo (spa_name) VALUES ('"+ spaName[i] +"');";
    				spaAndRtdbDaoImpl.insert(jdbc, insertSpa);
    			}
    			
		    }
		    if(!"".equals(rtdb.trim()))
		    {
    			String [] rtdbName = rtdb.split(",");
    			for(int i=0; i<rtdbName.length; i++)
    			{
    				String insertRtdb = "INSERT INTO kaliey.rtdbinfo (rtdb_name) VALUES ('"+ rtdbName[i] +"');";
    				spaAndRtdbDaoImpl.insert(jdbc, insertRtdb);
    			}
		    }
		} catch (Exception e) {
			return "Operations Failed!" ;
		}
		return "Operations Succeed!";
	}
	
	public String removeSpaAndRtdbInfo(String spa, String rtdb){
		try {
			/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
			JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		    JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
			String [] spaName = spa.split(",");
			String [] rtdbName = rtdb.split(",");
			for(int i=0; i<spaName.length; i++){
				String removeSpa = "DELETE FROM kaliey.spainfo WHERE  spa_name='"+ spaName[i] +"';";
				spaAndRtdbDaoImpl.delete(jdbc, removeSpa);
			}
			for(int i=0; i<rtdbName.length; i++){
				String remoceRtdb = "DELETE FROM kaliey.rtdbinfo WHERE rtdb_name='"+ rtdbName[i] +"';";
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
