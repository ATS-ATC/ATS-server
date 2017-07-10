package com.alucn.weblab.dao;

import java.util.ArrayList;
import java.util.HashMap;

import com.alucn.casemanager.server.common.util.JdbcUtil;

/**
 * @author haiqiw
 * 2017年6月5日 下午5:30:56
 * desc:DataAccessInterface
 */
public interface DataAccessInterface {

	public void insert(JdbcUtil jdbc, String sql) throws Exception;
	public void update(JdbcUtil jdbc, String sql) throws Exception;
	public void delete(JdbcUtil jdbc, String sql) throws Exception;
	public ArrayList<HashMap<String,Object>> query(JdbcUtil jdbc, String sql) throws Exception;
}
