package com.alucn.weblab.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.stereotype.Repository;

import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.weblab.dao.DataAccessInterface;
import com.alucn.weblab.dao.KalieyDataAccessInterface;
import com.alucn.weblab.utils.JDBCHelper;

@Repository("userDaoImpl")
public class UserDaoImpl implements KalieyDataAccessInterface{

	@Override
	public void insert(JDBCHelper jdbc, String sql) throws Exception {
		// TODO Auto-generated method stub
		jdbc.executeSql(sql);
	}

	@Override
	public void update(JDBCHelper jdbc, String sql) throws Exception {
		// TODO Auto-generated method stub
		jdbc.executeSql(sql);
	}

	@Override
	public void delete(JDBCHelper jdbc, String sql) throws Exception {
		// TODO Auto-generated method stub
		jdbc.executeSql(sql);
	}

	@Override
	public ArrayList<HashMap<String, Object>> query(JDBCHelper jdbc, String sql) throws Exception {
		// TODO Auto-generated method stub
		return jdbc.query(sql);
	}
	
	
}
