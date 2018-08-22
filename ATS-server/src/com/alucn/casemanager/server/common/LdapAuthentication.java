package com.alucn.casemanager.server.common;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

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

	private static String AuthSuccess = "SLLAUTHSUCCESS";
	private static String AuthFail = "SLLAUTHFAIL";
	private static String Ad4ServerFail = "SLLAD4FAIL";

	@SuppressWarnings("static-access")
	public LdapAuthentication(String user, String passwd) {
		this.User = user;
		this.Passwd = passwd;
	}

	public String getAuth() {

		if (User.trim().equals("") || Passwd.trim().equals("")) {
			return AuthFail;
		}
		
		/*
		String root = "ou=Users,ou=cnluc,ou=cn,dc=ad4,dc=ad,dc=alcatel,dc=com"; // root
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://cnbeisidc01.ad4.ad.alcatel.com:389/" + root);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, "cn=" + User + ',' + root);
		env.put(Context.SECURITY_CREDENTIALS, Passwd);
		try {
			// 链接ldap
			ctx = new InitialDirContext(env);
			log.debug("LdapAuthentication sucess");
			return AuthSuccess;
		} catch (javax.naming.AuthenticationException e) {
			e.printStackTrace();
			log.error(User + "LdapAuthentication fail");
			log.debug("LdapAuthentication fail");
			return AuthFail;
		} catch (Exception e) {
			log.debug("LdapAuthentication server fail");
			log.error(User + "LdapAuthentication server fail");
			return Ad4ServerFail;
			// e.printStackTrace();
			// return false;
		} finally {

			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					log.warn(e);
				}
			}
		}
		 */
		int try_times = 0;
        String ad4_server = "ad.alcatel.com";
        List<String> ad4_server_list =new ArrayList<>();
        ad4_server_list.add("135.251.44.183");
        ad4_server_list.add("135.251.33.43");
        ad4_server_list.add("135.252.44.225");
        ad4_server_list.add("135.251.33.52");
        while(try_times < 4)
        {
            
            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put("com.sun.jndi.ldap.connect.timeout", "2000");//连接超时设置为3秒
            env.put(Context.PROVIDER_URL, "ldap://" + ad4_server + ":389/");
            //env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, "ad4\\" +User  );
            env.put(Context.SECURITY_CREDENTIALS, Passwd);
             try {
                  // 链接ldap
                 ctx = new InitialDirContext(env);
                 log.debug("LdapAuthentication sucess");
                 return AuthSuccess;
             } catch (javax.naming.AuthenticationException e) {
                 log.error(User + " LdapAuthentication fail 1: " + e.toString());
                 log.debug("LdapAuthentication fail");
                 return AuthFail;
             } catch (javax.naming.CommunicationException e) {
                 log.error(User + " Ldap server connection fail 2: " + e.toString());
                 ad4_server_list.get(try_times);//ad4_server = "135.251.33.43";
                 try_times += 1;
                 // TODO: handle exception
             } 
             catch (Exception e) {
                 log.debug("LdapAuthentication server fail");
                 log.error(User + " LdapAuthentication server fail 3: " + e.toString());
                 
                 //e.printStackTrace();
                 return Ad4ServerFail;
             }
            
             finally
             {
                  
                 if(ctx != null)
                 {
                     try {
                         ctx.close();
                     } catch (NamingException e) {
                         // TODO Auto-generated catch block
                         System.out.println(e);
                     }
                 }
             }
             
        }
        return Ad4ServerFail;
	}

	public static void main(String[] args) {
		LdapAuthentication test = new LdapAuthentication("haiqiw", "#EDC3edc");
		System.out.println(test.getAuth());
	}
}