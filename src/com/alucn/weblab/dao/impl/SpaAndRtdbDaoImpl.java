package com.alucn.weblab.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Repository;
import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.weblab.dao.DataAccessInterface;

/**
 * @author haiqiw
 * 2017年6月6日 下午7:17:08
 * desc:SpaAndRtdbDaoImpl
 */
@Repository("spaAndRtdbDaoImpl")
public class SpaAndRtdbDaoImpl implements DataAccessInterface{

	@Override
	public void insert(JdbcUtil jdbc, String sql) throws Exception {
		jdbc.executeSql(sql);
	}

	@Override
	public void update(JdbcUtil jdbc, String sql) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(JdbcUtil jdbc, String sql) throws Exception {
		jdbc.executeSql(sql);
	}

	@Override
	public ArrayList<HashMap<String, Object>> query(JdbcUtil jdbc, String sql) throws Exception {
		return jdbc.query(sql);
	}

}
