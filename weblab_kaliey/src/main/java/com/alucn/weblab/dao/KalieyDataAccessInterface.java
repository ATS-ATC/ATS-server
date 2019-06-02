package com.alucn.weblab.dao;

import java.util.ArrayList;
import java.util.HashMap;

import com.alucn.weblab.utils.JDBCHelper;

public interface KalieyDataAccessInterface {

	public void insert(JDBCHelper jdbc, String sql) throws Exception;
	public void update(JDBCHelper jdbc, String sql) throws Exception;
	public void delete(JDBCHelper jdbc, String sql) throws Exception;
	public ArrayList<HashMap<String,Object>> query(JDBCHelper jdbc, String sql) throws Exception;
  
}
