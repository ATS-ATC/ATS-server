package com.alucn.weblab.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alucn.casemanager.server.common.ConfigProperites;
import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.weblab.dao.impl.ConfigOptDaoImpl;

/**
 * @author haiqiw
 * 2017年6月23日 下午1:53:25
 * desc:ConfigOptService
 */
@Service("configOptService")
public class ConfigOptService {
	
	@Autowired(required=true)
	private ConfigOptDaoImpl configOptDaoImpl;
	
	public ArrayList<HashMap<String, Object>> getConfig() throws Exception{
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String sql = "SELECT * FROM config;";
		ArrayList<HashMap<String, Object>> result = configOptDaoImpl.query(jdbc, sql);
		return result;
	}
	
	public String updateConfig(String userName, String configKey, String configValue) throws Exception{
		try {
			String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
			JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
			Map<String, String> mapConfigs = new HashMap<String, String>();
			String [] configKeys = configKey.split(",");
			String [] configValues = configValue.split(",");
			for(int i=0; i<configKeys.length; i++){
				String updateCon = "update config set con_value='"+configValues[i]+"' where con_key='"+configKeys[i]+"'";
				configOptDaoImpl.update(jdbc, updateCon);
				mapConfigs.put(configKeys[i], configValues[i]);
			}
			ConfigProperites.getInstance().refreshConfiguration(mapConfigs);
		} catch (Exception e) {
			return "Operations Failed!" ;
		}
		return "Operations Succeed!";
	}

}
