package com.alucn.weblab.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.stereotype.Repository;

import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.weblab.dao.DataAccessInterface;
import com.alucn.weblab.dao.KalieyDataAccessInterface;
import com.alucn.weblab.utils.JDBCHelper;

@Repository("mainDaoImpl")
public class MainDaoImpl implements KalieyDataAccessInterface{

	@Override
	public void insert(JDBCHelper jdbc, String sql) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int update(JDBCHelper jdbc, String sql) throws Exception {
		// TODO Auto-generated method stub
	    return jdbc.executeSql(sql);
	}

	@Override
	public int  delete(JDBCHelper jdbc, String sql) throws Exception {
		// TODO Auto-generated method stub
	    return jdbc.executeSql(sql);
	}

	@Override
	public ArrayList<HashMap<String, Object>> query(JDBCHelper jdbc, String sql) throws Exception {
		// TODO Auto-generated method stub
		return jdbc.query(sql);
	}

}
