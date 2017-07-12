package com.alucn.weblab.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alucn.casemanager.server.common.LdapAuthentication;
import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.weblab.dao.impl.UserDaoImpl;
import com.alucn.weblab.model.User;

/**
 * @author haiqiw
 * 2017��6��5�� ����5:27:40
 * desc:LoginService
 */
@Service("loginService")
public class LoginService {
	
	@Autowired(required=true)
	private UserDaoImpl userDaoImpl;
	
	public boolean getUser(User user) throws Exception{
    	String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
		String sql = "SELECT userName FROM AdminList where userName='"+user.getUserName()+"'";
		ArrayList<HashMap<String, Object>> result = userDaoImpl.query(jdbc, sql);
        if(result!=null && result.size()!=0){
            return true;
        }
        return false;
	}
	
	public boolean authUser(User user) throws Exception{
	    LdapAuthentication ldapAuth = new LdapAuthentication(user.getUserName(),user.getPassWord());
        String authResult = ldapAuth.getAuth();
        if(authResult.equals(Constant.AUTHSUCCESS)){
            return true;
        }
        return false;
	}
	
	public UserDaoImpl getUserDaoImpl() {
		return userDaoImpl;
	}
	
	public void setUserDaoImpl(UserDaoImpl userDaoImpl) {
		this.userDaoImpl = userDaoImpl;
	}
	
	
}
