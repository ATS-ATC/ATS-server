package com.alucn.casemanager.server.common;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

public class Ldap {  
	private static Log log = LogFactory.getLog(Ldap.class);
	
   private static DirContext ctx = null;  
   
   private static String	AuthSuccess = "SLLAUTHSUCCESS";
   private static String	AuthFail = "SLLAUTHFAIL";
   private static String	Ad4ServerFail = "SLLAD4FAIL";
   
   public Ldap() {
   }
   
    @SuppressWarnings("unchecked")
    public String getAuth(String User, String password) {
	   	
        if(User.trim().equals("") || password.trim().equals(""))
        {
            return AuthFail;
        }
	   	
	   	String ad4_server = "nsn-intra.net";
	   	@SuppressWarnings("rawtypes")
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put("com.sun.jndi.ldap.connect.timeout", "2000");//连接超时设置为3秒
        env.put(Context.PROVIDER_URL, "ldap://" + ad4_server + ":389/");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "nsn-intra\\"+User);
        env.put(Context.SECURITY_CREDENTIALS, password);
         try {
              // 链接ldap
             ctx = new InitialDirContext(env);
             log.debug("LdapAuthentication sucess");
             return AuthSuccess;
         } catch (javax.naming.AuthenticationException e) {
             log.error(User + " LdapAuthentication fail 1: " + e.toString());
             return AuthFail;
         } catch (javax.naming.CommunicationException e) {
             log.error(User + " Ldap server connection fail 2: " + e.toString());
         } 
         catch (Exception e) {
             log.error(User + " LdapAuthentication server fail 3: " + e.toString());
         }
        
         finally
         {
              
             if(ctx != null)
             {
                 try {
                     ctx.close();
                 } catch (Exception e) {
                     // TODO Auto-generated catch block
                     log.error(e);
                 }
             }
         }
             
        
        return Ad4ServerFail;
         
    }  
     
}   