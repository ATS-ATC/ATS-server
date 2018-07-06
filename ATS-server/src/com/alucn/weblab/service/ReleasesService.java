package com.alucn.weblab.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.weblab.dao.impl.ReleasesDaoImpl;

@Service("releasesService")
public class ReleasesService {
	
	@Autowired(required=true)
	private ReleasesDaoImpl releasesDaoImpl;
	
	public ArrayList<HashMap<String, Object>> getReleases() throws Exception{
		String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String sql = "select * from n_version_info where stateflag=0 order by version_date desc";
		ArrayList<HashMap<String, Object>> result = releasesDaoImpl.query(jdbc, sql);
		return result;
	}
}
