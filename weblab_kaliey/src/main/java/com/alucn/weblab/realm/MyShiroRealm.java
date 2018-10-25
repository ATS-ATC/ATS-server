package com.alucn.weblab.realm;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import com.alucn.weblab.model.NUser;
import com.alucn.weblab.service.LoginService;

public class MyShiroRealm extends  AuthorizingRealm{
	public static Logger logger = Logger.getLogger(MyShiroRealm.class);
	@Autowired
	private LoginService loginService;
	
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		System.err.println("PrincipalCollection >> doGetAuthorizationInfo");
		String username = (String)principals.fromRealm(getName()).iterator().next();
		if(username!=null){
			ArrayList<HashMap<String, Object>> pers = new ArrayList<HashMap<String, Object>>();
			ArrayList<String> roles = new ArrayList<String>();
			try {
				pers = loginService.getPermissionsByUserName(username);
				ArrayList<HashMap<String, Object>> rolesByName = loginService.getRolesByName(username);
				SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
				if(rolesByName.size()>0) {
					for (HashMap<String, Object> hashMap : rolesByName) {
						String role_name = (String)hashMap.get("role_name");
						roles.add(role_name);
					}
					System.err.println("doGetAuthorizationInfo   role_name:======="+roles);
					//logger.info("doGetAuthorizationInfo   role_name:======="+roles);
					info.addRoles(roles);
				}
		        if(pers.size()>0){
		             for(HashMap<String, Object> each:pers){
		                 //将权限资源添加到用户信息中  
		            	 String permission_name = (String)each.get("permission_name");
		            	 System.err.println("doGetAuthorizationInfo   permission_name:======="+permission_name);
		            	 //logger.info("doGetAuthorizationInfo   permission_name:======="+permission_name);
		            	 if(permission_name!=null&&!"".equals(permission_name)) {
		            		 info.addStringPermission(permission_name);
		            	 }
		             }
		         }
		        return info;
			} catch (Exception e) {
				e.printStackTrace();
			} 
	     }
		return null;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken at) {
		System.err.println("AuthenticationToken >> doGetAuthenticationInfo");
		 UsernamePasswordToken token = (UsernamePasswordToken) at;
		 // 通过表单接收的用户名 
		 String username  = token.getUsername();
		 
		 if(username!=null && !"".equals(username)){
			NUser nUser = new NUser();
			nUser.setUsername(username);
			ArrayList<HashMap<String, Object>> queryNUser = new ArrayList<HashMap<String, Object>>();
				try {
					queryNUser = loginService.queryNUser(nUser);
				} catch (Exception e) {
					e.printStackTrace();
				}
			 	if (queryNUser.size()>0) {  
			 		logger.info("doGetAuthenticationInfo:========="+queryNUser.get(0).get("password"));
			 		return new SimpleAuthenticationInfo(queryNUser.get(0).get("username"), queryNUser.get(0).get("password"), getName());  
			 	} 
	 	}
		return null;
	}
	public LoginService getLoginService() {
		return loginService;
	}

	public void setLoginService(LoginService loginService) {
		this.loginService = loginService;
	}
}
