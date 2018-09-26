package com.alucn.weblab.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.shiro.config.Ini;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.web.config.IniFilterChainResolverFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alucn.weblab.service.LoginService;

/**
 * 
 * ClassName: ShiroPermissionFactory 
 * @Description: 动态url访问控制
 * @author guoyouworld@gmail.com
 * @date 2017年12月16日
 */
public class ShiroPermissionFactory  extends ShiroFilterFactoryBean{
	
	public static Logger logger = Logger.getLogger(ShiroPermissionFactory.class);
	
	/**配置中的过滤链*/  
    public static String definitions;
      
    /**权限service*/  
    @Autowired
	private LoginService loginService;
  
    /** 
     * 从数据库动态读取权限 
     */  
    @Override  
    public void setFilterChainDefinitions(String definitions) {  
    	if(definitions!=null&&!"".endsWith(definitions)) {
    		ShiroPermissionFactory.definitions = definitions;  
            
            //数据库动态权限  
            //List<KPermissions> permissions = permissionsService.findAllPermissions();
            ArrayList<HashMap<String, Object>> permissions =new ArrayList<HashMap<String, Object>>();
    		try {
    			permissions = loginService.findUrlPermissions();
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
            Map<String, String> otherChains = new HashMap<String,String>();
            for(HashMap<String, Object> po : permissions){  
                //字符串拼接权限  
                //definitions = definitions+po.getKey() + " = "+ po.getValue();
            	String url =  (String)po.get("url");
            	String permission_name = (String)po.get("permission_name");
            	if(!"".equals(url)&&!"".equals(permission_name)) {
            		otherChains.put(url, permission_name);
            	}
            }  
            //System.out.println("==============="+definitions);  
            //从配置文件加载权限配置  
            Ini ini = new Ini();  
            ini.load(definitions);  
            Ini.Section section = ini.getSection(IniFilterChainResolverFactory.URLS);  
            if (CollectionUtils.isEmpty(section)) {  
                section = ini.getSection(Ini.DEFAULT_SECTION_NAME); 
                
            }  
            logger.info("================="+definitions+"================="+otherChains);
            //加入权限集合  
            section.putAll(otherChains);
            setFilterChainDefinitionMap(section);  
    	}
    }  
    public LoginService getLoginService() {
		return loginService;
	}

	public void setLoginService(LoginService loginService) {
		this.loginService = loginService;
	}
}
