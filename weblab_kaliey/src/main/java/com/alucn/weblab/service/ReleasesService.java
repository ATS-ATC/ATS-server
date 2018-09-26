package com.alucn.weblab.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.weblab.dao.impl.ReleasesDaoImpl;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service("releasesService")
public class ReleasesService {
	
	@Autowired(required=true)
	private ReleasesDaoImpl releasesDaoImpl;
	
	public ArrayList<HashMap<String, Object>> getReleases() throws Exception {
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String sql = "select * from n_version_info where stateflag=0 order by version_date desc";
		ArrayList<HashMap<String, Object>> result = releasesDaoImpl.query(jdbc, sql);
		for (HashMap<String, Object> hashMap : result) {
			HashMap<String, Object> urlList = new HashMap<String, Object>();
			List<Map<String,String>> source_zip_urls = new ArrayList<>();
			String source_zip_url = (String) hashMap.get("source_zip_url");
			//System.out.println("source_zip_url:=========="+source_zip_url);
			if(!"".equals(source_zip_url)&&source_zip_url!=null) {
				Map<String,String> map = new LinkedHashMap<>();
				JSONObject json = JSONObject.fromObject(source_zip_url);
				/*JSONArray jsonArray = JSONArray.fromObject(source_zip_url);
				for (Object object : jsonArray) {
					
				}*/
				//System.out.println(json.toString());
				try {
					Set<String> keySet = json.keySet();
					for (String key : keySet) {
						if(json.get(key)!=null) {
							//System.out.println(key+":"+json.get(key).toString());
							map.put(key, json.get(key).toString());
						}
					}
					/*if(json.get("Windows")!=null) {
						//System.out.println(json.get("Windows").toString());
						map.put("Windows", json.get("Windows").toString());
					}
					if(json.get("Linux")!=null) {
						map.put("Linux", json.get("Linux").toString());
					}
					if(json.get("Command")!=null) {
						map.put("Command", json.get("Command").toString());
					}*/
					source_zip_urls.add(map);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			hashMap.put("source_zip_urls", source_zip_urls);
			//hashMap.add(urlList);
		}
		return result;
	}
}
