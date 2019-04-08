package com.alucn.weblab.dao;

import java.util.ArrayList;
import java.util.HashMap;

import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.weblab.utils.KalieyMysqlUtil;

public interface KalieyDataAccessInterface {

	public void insert(KalieyMysqlUtil jdbc, String sql) throws Exception;
	public void update(KalieyMysqlUtil jdbc, String sql) throws Exception;
	public void delete(KalieyMysqlUtil jdbc, String sql) throws Exception;
	public ArrayList<HashMap<String,Object>> query(KalieyMysqlUtil jdbc, String sql) throws Exception;
}
