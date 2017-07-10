package com.alucn.weblab.service;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
  
public class LdapAuthentication {  
	private static Log log = LogFactory.getLog(LdapAuthentication.class);
	
   private static DirContext ctx = null;  
   private static String User;
   private static String Passwd;
   
   private static String	AuthSuccess = "SLLAUTHSUCCESS";
   private static String	AuthFail = "SLLAUTHFAIL";
   private static String	Ad4ServerFail = "SLLAD4FAIL";
   
   public LdapAuthentication(String user, String passwd) {
	   this.User = user;
	   this.Passwd = passwd;
   }
   @SuppressWarnings("unchecked")
   public String getAuth() {
	   	

	   	 if(User.trim().equals("") || Passwd.trim().equals(""))
	   	 {
	   		return AuthFail;
	   	 }
         String root = "ou=Users,ou=cnluc,ou=cn,dc=ad4,dc=ad,dc=alcatel,dc=com"; // root
         Hashtable env = new Hashtable();
         env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
         env.put(Context.PROVIDER_URL, "ldap://cnbeisidc01.ad4.ad.alcatel.com:389/" + root);
         env.put(Context.SECURITY_AUTHENTICATION, "simple");
         env.put(Context.SECURITY_PRINCIPAL, "cn="+User+','+ root );
         env.put(Context.SECURITY_CREDENTIALS, Passwd);
         try {
             // 链接ldap
             ctx = new InitialDirContext(env);
             log.debug("LdapAuthentication sucess");
             return AuthSuccess;
         } catch (javax.naming.AuthenticationException e) {
        	 log.error(User + "LdapAuthentication fail");
        	 log.debug("LdapAuthentication fail");
        	 return AuthFail;
         } catch (Exception e) {
        	 log.debug("LdapAuthentication server fail");
        	 log.error(User + "LdapAuthentication server fail");
        	 return Ad4ServerFail;
             //e.printStackTrace();
             //return false;
         }
         finally
         {
        	 
        	 if(ctx != null)
        	 {
				try {
					ctx.close();
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					log.warn(e);
				}
        	 }
         }
         
    }  
     
}   