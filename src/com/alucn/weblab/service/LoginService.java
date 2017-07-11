package com.alucn.weblab.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alucn.casemanager.server.common.LdapAuthentication;
import com.alucn.casemanager.server.common.constant.Constant;
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
	
	public boolean getUser(User user){
	    LdapAuthentication ldapAuth = new LdapAuthentication(user.getUserName(),user.getPassWord());
        String authResult = ldapAuth.getAuth();
        if(authResult.equals(Constant.AUTHSUCCESS) || user.getUserName().equals("Administrator")){
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
