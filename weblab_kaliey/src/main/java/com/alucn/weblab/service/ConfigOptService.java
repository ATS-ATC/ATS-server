package com.alucn.weblab.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.experimental.theories.FromDataPoints;
import org.openqa.jetty.html.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alucn.casemanager.server.common.ConfigProperites;
import com.alucn.weblab.dao.impl.ConfigOptDaoImpl;
import com.alucn.weblab.utils.JDBCHelper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author haiqiw
 * 2017年6月23日 下午1:53:25
 * desc:ConfigOptService
 */
@Service("configOptService")
public class ConfigOptService {
	
	@Autowired(required=true)
	private ConfigOptDaoImpl configOptDaoImpl;
	
	public JSONArray getConfig() throws Exception{
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		String sql = "SELECT * FROM cases_info_db.certify_server_config;";
		JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
		ArrayList<HashMap<String, Object>> result = configOptDaoImpl.query(jdbc, sql);
		JSONArray test = new JSONArray().fromObject(result);
		return test;
	}
	
	public String updateConfig(String userName, String configKey, String configValue) throws Exception{
		try {
			/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
			JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
			Map<String, String> mapConfigs = new HashMap<String, String>();
			String updateCon = "update cases_info_db.certify_server_config set con_value='"+configValue+"' where con_key='"+configKey+"'";
            JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
            configOptDaoImpl.update(jdbc, updateCon);
            String query_config = "select * from cases_info_db.certify_server_config";
            ArrayList<HashMap<String, Object>> configs = configOptDaoImpl.query(jdbc, query_config);
			for(int i=0; i<configs.size(); i++){
				
				mapConfigs.put(configs.get(i).get("con_key").toString(), configs.get(i).get("con_value").toString());
			}
			
			
			ConfigProperites.getInstance().refreshConfiguration(mapConfigs);
		} catch (Exception e) {
		    return "FAIL"; 
		}
		return "SUCCESS";
	}

}
