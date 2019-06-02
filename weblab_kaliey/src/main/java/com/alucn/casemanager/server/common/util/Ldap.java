package com.alucn.casemanager.server.common.util;

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
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


import com.alucn.weblab.utils.JDBCHelper;;

public class Ldap {  
	private static Log log = LogFactory.getLog(Ldap.class);
	
   private static DirContext ctx = null;  
   
   private static String	AuthSuccess = "SLLAUTHSUCCESS";
   private static String	AuthFail = "SLLAUTHFAIL";
   private static String	Ad4ServerFail = "SLLAD4FAIL";
   
   public Ldap() {
   }
   
   private static JSONArray search_users(JSONArray user_list, String to_reporter, boolean is_check_manager, boolean is_check_next)  throws NamingException {
       
       SearchControls searchCtls = new SearchControls();
       searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
       String returnedAtts[] = {"mail","displayName","directReports","sAMAccountName", "manager"};
       searchCtls.setReturningAttributes(returnedAtts);
       String searchBase = "dc=nsn-intra,dc=net";
       
       String searchFilter;
       String user, mail, show_name, user_name, reporter, manager;
       JSONArray report_list = new JSONArray();
       JSONArray sub_users = new JSONArray();
       JSONObject user_info = new JSONObject();
       JSONArray user_info_list = new JSONArray();
       //= "(cn=Li James 69106775)";
       for(int i =0; i < user_list.size(); i++)
       {
           user = user_list.getString(i);
           if (user.contains("="))
           {
               searchFilter = "(" + user + ")";
           }
           else
           {
               searchFilter = "(sAMAccountName=" + user + ")";
           }
           
           NamingEnumeration<SearchResult> answer = ctx.search(searchBase, searchFilter,searchCtls);
           while (answer.hasMoreElements()) {
               SearchResult sr = (SearchResult) answer.next();
               //System.out.println("getname=" + sr.getName());
               Attributes Attrs = sr.getAttributes();
               
               if (Attrs != null) 
               {
                   try {
                       try {
                           mail = Attrs.get("mail").toString().split(":")[1].trim();
                        } catch (Exception e) {
                            mail = "";
                        }
                       
                       show_name = Attrs.get("displayName").toString().split(":")[1].trim();;
                       user_name = Attrs.get("sAMAccountName").toString().split(":")[1].trim();;
                       report_list = new JSONArray();
                       sub_users = new JSONArray();
                       if(!is_check_manager)
                       {
                           
                           try{
                               Attribute Attr = Attrs.get("directReports");
                               if(Attr != null)
                               {
                                   for (NamingEnumeration e = Attr.getAll(); e.hasMore();) {
                                       reporter = e.next().toString();                           
                                       if(reporter.startsWith("CN="))
                                       {   reporter = reporter.split(",")[0];
                                           report_list.add(reporter);
                                       }
                                       else{
                                           log.error("Unknow reporter: " + reporter);
                                       }
                                   }
                               }
                           }catch (Exception e) {
                               log.error("Attribute: "+ user + " -- " + e );
                           }
                           
                           if(report_list.size() > 0)
                           {
                               sub_users = search_users(report_list, user_name, false, false);
                           }
                       }
                       else 
                       {
                           if(is_check_next)
                           {
                               try {
                                   manager = Attrs.get("manager").toString().split(":")[1].trim();;
                                   log.debug(manager);
                                   if(manager.startsWith("CN="))
                                   {   manager = manager.split(",")[0];
                                       JSONArray managers = new JSONArray();
                                       managers.add(manager);
                                       log.debug(manager);
                                       JSONArray manager_list = search_users(managers, "", true, false);
                                       if(manager_list.size() > 0)
                                       {
                                           log.debug(manager_list);
                                           to_reporter = manager_list.getJSONObject(0).getString("user_name");
                                       }
                                   }
                                   else{
                                       log.error("Unknow manager: " + manager);
                                   }
                                   
                                } catch (Exception e) {
                                    // TODO: handle exception
                                }
                               
                           }
                       }
                       user_info = new JSONObject();
                       user_info.put("user_name", user_name);
                       user_info.put("mail", mail);
                       user_info.put("show_name", show_name);
                       user_info.put("to_reporter", to_reporter);
                       user_info_list.add(user_info);
                       

                       for(int j = 0; j < sub_users.size(); j++)
                       {
                           user_info_list.add(sub_users.getJSONObject(j));
                       }
                       
                   }catch (Exception e) {
                       log.error("Attrs: " + user + " -- " + e);
                   } 
               }
           }
       }
       
       return user_info_list;
   }
   
   public void getUserDN(String uid) throws NamingException {
       
       List<String> ignore_ids=new ArrayList<String>();
       ignore_ids.add("objectGUID");
       ignore_ids.add("msExchMailboxSecurityDescriptor");
       ignore_ids.add("objectSid");
       ignore_ids.add("msExchMailboxGuid");
       ignore_ids.add("mS-DS-ConsistencyGuid");
       ignore_ids.add("protocolSettings");
       ignore_ids.add("msExchSafeSendersHash");
       

       SearchControls searchCtls = new SearchControls();
       searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
       String searchFilter = "(sAMAccountName=q1tan)";
       String searchBase = "dc=nsn-intra,dc=net";
       String returnedAtts[] = {"mail","manager"};
       //searchCtls.setReturningAttributes(returnedAtts);
       NamingEnumeration<SearchResult> answer = ctx.search(searchBase, searchFilter,searchCtls);
       while (answer.hasMoreElements()) {
           SearchResult sr = (SearchResult) answer.next();
           System.out.println("getname=" + sr.getName());
           Attributes Attrs = sr.getAttributes();
           //System.out.println("Attrs=" + Attrs.get("dSCorePropagationData").toString());
           if (Attrs != null) {
           try {
            
               for (NamingEnumeration ne = Attrs.getAll(); ne.hasMore();) {
                   Attribute Attr = (Attribute) ne.next();
                   
                   // 读取属性值
                   System.out.println("AttributeID="
                           + Attr.getID().toString());
                   
                   if(ignore_ids.contains(Attr.getID()))
                   {
                      continue;
                   }
                   for (NamingEnumeration e = Attr.getAll(); e.hasMore();) {
                       String user = e.next().toString(); // 接受循环遍历读取的userPrincipalName用户属性
                       System.out.println(user);
                   }
               }
           }catch (Exception e) {
               System.out.println(e);
           } 
        }

       }
   }
   
   public void init_users() {
       
       JSONArray manager_list = new JSONArray();
       manager_list.add("wyan1");
       manager_list.add("yphu");
       JSONArray users;
       if(! AuthSuccess.equals(init_ldap()))
       {
           log.error("init ldap failed.");
           return;
       }
        try {
            users = search_users(manager_list, "", false, false);
            if(users.size() > 0)
            {
                log.debug(users.toString());
                String replace_sql = "replace into user_info (user_name, mail, show_name, to_reporter) values (?, ?, ?, ?);";
                List<Object[]> params = new ArrayList<Object[]>();
                for(int j=0; j<users.size(); j++){
                    Object [] paramsArray = new Object[3];
                    paramsArray[0] = users.getJSONObject(j).get("user_name").toString();
                    paramsArray[1] = users.getJSONObject(j).get("mail").toString();
                    paramsArray[2] = users.getJSONObject(j).get("show_name").toString();
                    paramsArray[3] = users.getJSONObject(j).get("to_reporter").toString();
                    params.add(paramsArray);
                    
                }
                JDBCHelper mysql = JDBCHelper.getInstance("mysql-1");
                int []changes = mysql.executeBatch(replace_sql, params);
                
                if(changes.length != users.size())
                {
                    log.error("Update user_info DB not successful: need change nums: " + users.size() + " -- changed nums: " + changes.length );
                }
            }
            
        } catch (NamingException e) {
            // TODO Auto-generated catch block
            log.error(e.getStackTrace());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error(e.getStackTrace());
            
        }
        
        close_ldap();
   }
   
   public static JSONArray search_users(JSONArray user)
   {
       try {
           init_ldap();
            JSONArray users =  search_users(user, "", true, true);
            if(users.size() > 0)
            {
                String replace_sql = "replace into user_info (user_name, mail, show_name, to_reporter) values (?, ?, ?, ?);";
                
                List<Object[]> params = new ArrayList<Object[]>();
                for(int j=0; j<users.size(); j++){
                    Object [] paramsArray = new Object[3];
                    paramsArray[0] = users.getJSONObject(j).get("user_name").toString();
                    paramsArray[1] = users.getJSONObject(j).get("mail").toString();
                    paramsArray[2] = users.getJSONObject(j).get("show_name").toString();
                    paramsArray[3] = users.getJSONObject(j).get("to_reporter").toString();
                    params.add(paramsArray);
                    
                }
                JDBCHelper mysql = JDBCHelper.getInstance("mysql-1");
                int []changes = mysql.executeBatch(replace_sql, params);
                
                if(changes.length != users.size())
                {
                    log.error("Update user_info DB not successful: need change nums: " + users.size() + " -- changed nums: " + changes.length );
                }
            }
            close_ldap();
            return users;
        } catch (NamingException e) {
            // TODO Auto-generated catch block
            log.error(e.getStackTrace());
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error(e.getStackTrace());
        }
        return new JSONArray();
   }
   
   
   @SuppressWarnings("unchecked")
   public static String init_ldap() {
       String ad4_server = "nsn-intra.net";
       @SuppressWarnings("rawtypes")
       Hashtable env = new Hashtable();
       env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
       env.put("com.sun.jndi.ldap.connect.timeout", "2000");//连接超时设置为3秒
       env.put(Context.PROVIDER_URL, "ldap://" + ad4_server + ":389/");
       env.put(Context.SECURITY_AUTHENTICATION, "simple");
       env.put(Context.SECURITY_PRINCIPAL, "SUNIT_SVC_CN"  );
       env.put(Context.SECURITY_CREDENTIALS, "W@EDpaqer010");
        try {
            ctx = new InitialDirContext(env);
            log.debug("LdapAuthentication sucess");
            return AuthSuccess;
        } catch (javax.naming.AuthenticationException e) {
            log.error("SUnit LdapAuthentication fail 1: " + e.toString());
            return AuthFail;
        } catch (javax.naming.CommunicationException e) {
            log.error("SUnit Ldap server connection fail 2: " + e.toString());
        } 
        catch (Exception e) {
            log.error("SUnit LdapAuthentication server fail 3: " + e.toString());
        }
    
       return Ad4ServerFail;
        
   }  
   
   public static void  close_ldap()
   {
       if(ctx != null)
       {
           try {
               ctx.close();
           } catch (Exception e) {
               log.error(e);
           }
       }
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