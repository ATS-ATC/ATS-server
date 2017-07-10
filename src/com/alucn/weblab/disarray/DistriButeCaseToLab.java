package com.alucn.weblab.disarray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import org.apache.log4j.Logger;
import org.omg.CORBA.PRIVATE_MEMBER;

import com.alucn.casemanager.server.common.CaseConfigurationCache;
import com.alucn.casemanager.server.common.ConfigProperites;
import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.util.ParamUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DistriButeCaseToLab {
    public static Logger logger = Logger.getLogger(DistriButeCaseToLab.class);
    private JSONArray GetServerInfoFromDB()
    {
        
        Connection connection=null;
        Statement state=null;

        JSONArray ServerInfos=new JSONArray();
        try
        {
             Class.forName("org.sqlite.JDBC");
             String CaseInfoDB=ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
             connection=DriverManager.getConnection("jdbc:sqlite:"+CaseInfoDB);
             state = connection.createStatement();
             String query_sql = "select * from serverList;";
             ResultSet result=state.executeQuery(query_sql);
             ResultSetMetaData metaData = result.getMetaData();
             int ColumnCount = metaData.getColumnCount();
             while(result.next())
             {
                 JSONObject ServerInfo = new JSONObject();
                 for(int i=1; i<= ColumnCount; i++)
                 {
                     ServerInfo.put(metaData.getColumnName(i), result.getString(i));
                 }
                 ServerInfos.add(ServerInfo);
             }
         
        }
        catch(SQLException e1)
        {
            logger.error(e1);
        }
        catch(Exception e2)
        {
            logger.error(e2);
            
        }
        finally
        {
            try
            {
                if(state!=null)
                {
                    state.close();
                }
                if(connection!=null)
                {
                    connection.close();
                }
            }
            catch(SQLException e3)
            {
                
                logger.error(e3);
            }
         
        }   
        return ServerInfos;
    
    }
    
    private boolean IsInJSONArray(Object value, JSONArray list)
    {
        for(int i = 0; i < list.size(); i++)
        {
            if(String.valueOf(value).equals(String.valueOf(list.get(i))))
            {
                return true;
            }
        }
        return false;
    }
    
    private boolean isSameJSONArrayWithJSONArray(JSONArray value, JSONArray list)
    {
        if(value.size() != list.size())
        {
            return false;
        }
        for (int i = 0; i < value.size(); i++)
        {
            if(!IsInJSONArray(value.getString(i),list))
            {
                return false;
            }
        }
        return true;
    }
    
    private boolean IsInJSONArrayWithoutCase(Object value, JSONArray list)
    {
        for(int i = 0; i < list.size(); i++)
        {
            String A_value = String.valueOf(list.get(i)).toUpperCase();
            if(A_value.startsWith(String.valueOf(value)))
            {
                return true;
            }
        }
        return false;
    }
    
    private boolean isLabListContainsCaseList(JSONArray A, JSONArray B)
    {
        for(int i = 0; i < B.size(); i ++)
        {
            if(!IsInJSONArrayWithoutCase(B.getString(i).toUpperCase(), A))
            {
                return false;
            }
        }
        return true;
    }
    
    private int postionInJSONArray(Object value, JSONArray list)
    {
        for(int i = 0; i < list.size(); i++)
        {
            String A_value = String.valueOf(list.get(i)).toUpperCase();
            if(A_value.startsWith(String.valueOf(value)))
            {
                return i;
            }
        }
        
        return -1;
    }
    
    
    private JSONArray updateKvmDB()
    {
        JSONArray changedList = new JSONArray();
        JSONArray changedKvmList = new JSONArray();
        JSONArray needDeleteKvmList = new JSONArray();
        JSONArray Servers = CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock, true, null);
        JSONArray ServersInDB = GetServerInfoFromDB();
        JSONObject ServerDB;
        JSONObject ServerMem;
        boolean IsExist;
        for(int i = 0; i < ServersInDB.size(); i++)
        {
            ServerDB = ServersInDB.getJSONObject(i);
            IsExist = false;
            for(int j = 0; j < Servers.size(); j++)
            {
                ServerMem = Servers.getJSONObject(j).getJSONObject(Constant.LAB);
                if(ServerDB.getString("serverName").equals(ServerMem.getString(Constant.SERVERNAME)))
                {
                    IsExist = true;
                    if(! ServerDB.getString("protocol").equals(ServerMem.getString(Constant.SERVERPROTOCOL)))
                    {
                        changedKvmList.add(ServerMem);
                        changedList.add(ServerMem.getString(Constant.SERVERNAME));
                        break;
                    }
                    if(! isSameJSONArrayWithJSONArray(JSONArray.fromObject(ServerDB.getString("SPA")), ServerMem.getJSONArray(Constant.SERVERSPA)))
                    {
                        changedKvmList.add(ServerMem);
                        changedList.add(ServerMem.getString(Constant.SERVERNAME));
                        break;
                    }
                    if(! isSameJSONArrayWithJSONArray(JSONArray.fromObject(ServerDB.getString("RTDB")), ServerMem.getJSONArray(Constant.SERVERRTDB)))
                    {
                        changedKvmList.add(ServerMem);
                        changedList.add(ServerMem.getString(Constant.SERVERNAME));
                        break;
                    }
                }
            }
            if(!IsExist)
            {
                needDeleteKvmList.add(ServerDB.getString("serverName"));
                changedList.add(ServerDB.getString("serverName"));
            }
            
        }
        
        for(int i = 0; i < Servers.size(); i++)
        {
            ServerMem = Servers.getJSONObject(i).getJSONObject(Constant.LAB);
            IsExist = false;
            for(int j =0; j <ServersInDB.size(); j++)
            {
                if(ServersInDB.getJSONObject(j).getString("serverName").equals(ServerMem.getString(Constant.SERVERNAME)))
                {
                    IsExist = true;
                    break;
                }
            }
            if(!IsExist)
            {
                changedKvmList.add(ServerMem);
                changedList.add(ServerMem.getString(Constant.SERVERNAME));
            }
        }
        
        
        
        if(needDeleteKvmList.size() > 0 || changedKvmList.size() > 0)
        {
            Connection connection=null;
            Statement state=null;

            try
            {
                 Class.forName("org.sqlite.JDBC");
                 String CaseInfoDB=ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
                 connection=DriverManager.getConnection("jdbc:sqlite:"+CaseInfoDB);
                 
                 if(needDeleteKvmList.size() > 0)
                 {
                     PreparedStatement prep = connection.prepareStatement(
                             "delete from serverList where serverName = ?;");
                     
                     for(int i =0; i<needDeleteKvmList.size(); i++)
                     {
                         prep.setString(1, needDeleteKvmList.getString(i));
                         prep.addBatch();
                     }
                     
                     connection.setAutoCommit(false);
                     prep.executeBatch();
                     connection.setAutoCommit(true);
                 }
                 
                 if(changedKvmList.size() > 0)
                 {
                     PreparedStatement prep = connection.prepareStatement(
                             "replace into serverList values( ?, ?, ?, ?);");
                     
                     for(int i =0; i<changedKvmList.size(); i++)
                     {
                         ServerMem = changedKvmList.getJSONObject(i);
                         
                         prep.setString(1, ServerMem.getString(Constant.SERVERNAME));
                         prep.setString(2, ServerMem.getString(Constant.SERVERPROTOCOL));
                         prep.setString(3, ServerMem.getJSONArray(Constant.SERVERSPA).toString());
                         prep.setString(4, ServerMem.getJSONArray(Constant.SERVERRTDB).toString());
//                         prep.setString(5, ServerMem.getStatus());
                         //prep.setString(5, "");
                         prep.addBatch();
                     }
                     
                     connection.setAutoCommit(false);
                     prep.executeBatch();
                     connection.setAutoCommit(true);
                 }   
             
            }
            catch(SQLException e1)
            {
                logger.error(e1);
            }
            catch(Exception e2)
            {
                logger.error(e2);
                
            }
            finally
            {
                try
                {
                    if(state!=null)
                    {
                        state.close();
                    }
                    if(connection!=null)
                    {
                        connection.close();
                    }
                }
                catch(SQLException e3)
                {
                    
                    logger.error(e3);
                }
             
            }   
        }
        
        return changedList;
    }
    
   /* private  boolean isNumeric(String str){  
        Pattern pattern = Pattern.compile("[0-9]*");  
        return pattern.matcher(str).matches();     
    }  */
    
    
    private JSONArray GetReleaseList()
    {
        JSONArray releaseArray = new JSONArray();
        URL url;
        try {
            url = new URL ("http://135.251.249.250/hg/SurepayDraft/rawfile/tip/.info/TagConfig.json");
            InputStream inputStream = null;
            InputStreamReader inputStreamReader = null;
            BufferedReader reader = null;
            String tempLine, response = "";
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                reader = new BufferedReader(inputStreamReader);
                
                while ((tempLine = reader.readLine()) != null) {
                    response += tempLine;
                
                }
                
                JSONObject tagInfos = JSONObject.fromObject(response);
                JSONArray SingleList = tagInfos.getJSONArray("single");
                for(int i = 0; i < SingleList.size(); i++)
                {
                    if(SingleList.getJSONObject(i).getString("name").equals("release"))
                    {
                        releaseArray = SingleList.getJSONObject(i).getJSONArray("value");
                    }
                }
                
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
        return releaseArray;
    }
    
    private void UpdateCaseStatusDB(JSONArray caseList)
    {
        Connection connection=null;
        Statement state=null;
        String QuerySql = null;
        String CaseInfoDB =ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
        String caseListStr = caseList.toString().replace("\"", "'").replace("[", "(").replace("]", ")");
        int GroupId = 0;
        try
        {
         Class.forName("org.sqlite.JDBC");
         
         connection=DriverManager.getConnection("jdbc:sqlite:"+CaseInfoDB);
         state = connection.createStatement();
         
         QuerySql = "select max(group_id) from toDistributeCases where case_name not in " + caseListStr;
         try
         {
             ResultSet result=state.executeQuery(QuerySql);
             while(result.next())
             {
                 try
                 {
                     GroupId = result.getInt("max(group_id)");
                 }catch(Exception e)
                 {
                     GroupId = 0;
                     logger.error(e);
                 }
                 
             }
         }catch(Exception e)
         {
             logger.error(e);
             GroupId = 0;
             
         }
             
         
         QuerySql = "select D.case_name, D.base_data, D.special_data, D.lab_number, D.mate, D.customer, D.release, D.porting_release, C.SPA, C.DB as RTDB, C.SecData as second_data "
                 + "from DailyCase as D, CaseDepends as C where D.case_name = C.case_name and D.case_name in " +  caseListStr   
                 + " order by D.lab_number, D.mate, D.special_data,  D.base_data, C.SecData, D.case_name;";
         //int change_num = state.executeUpdate(UpdateSql);
         //logger.error(QuerySql);
         String caseName;
         String old_lab_number = "INIT", new_lab_number;
         String old_mate = "INIT", new_mate;
         String old_special_data = "INIT", new_special_data;
         String old_base_data = "INIT", new_base_data;
         String old_second_data = "INIT", new_second_data;
         String SPA, RTDB;
         String customer, release, porting_release;
         JSONArray spaArray, rtdbArray; 
         boolean IsSame = true;
         
         
         JSONArray Servers =  CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock, true, null);
         JSONObject ServerMem;
         ResultSet result2=state.executeQuery(QuerySql);
         PreparedStatement prep = connection.prepareStatement(
                 "replace into toDistributeCases values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
      
         boolean isExcute = false;
         
         while(result2.next())
         {
             caseName = result2.getString("case_name");
             new_lab_number = result2.getString("lab_number");
             new_mate = result2.getString("mate");
             new_special_data = result2.getString("special_data");
             new_base_data = result2.getString("base_data");
             new_second_data = result2.getString("second_data");
             customer = result2.getString("customer");
             release = result2.getString("release");
             porting_release = result2.getString("porting_release");
             SPA = result2.getString("SPA");
             RTDB = result2.getString("RTDB");
             spaArray = JSONArray.fromObject(SPA.split(","));
             rtdbArray = JSONArray.fromObject(RTDB.split(","));
             JSONArray kvmList = new JSONArray();
             for (int i = 0; i < Servers.size(); i ++)
             {
                 ServerMem = Servers.getJSONObject(i).getJSONObject(Constant.LAB);
                 String serverName = ServerMem.getString(Constant.SERVERNAME);
                 JSONArray spaList = ServerMem.getJSONArray(Constant.SERVERSPA);
                 JSONArray rtdbList = ServerMem.getJSONArray(Constant.SERVERRTDB);
                 String serverProtocol = ServerMem.getString(Constant.SERVERPROTOCOL);
                 String serverRelease = ServerMem.getString(Constant.SERVERRELEASE);
                 //logger.error("Server: " + spaList.toString() + " ----  " + rtdbList.toString());
                 if(!isLabListContainsCaseList(spaList, spaArray))
                 {
                     //logger.error("Server: " + spaList.toString() + " case: " + spaArray.toString());
                     continue;
                 }
                 if(!isLabListContainsCaseList(rtdbList, rtdbArray))
                 {
                     //logger.error("Server: " + rtdbList.toString() + " case: " + rtdbArray.toString());
                     continue;
                 }
                 if((serverProtocol.equals("ANSI") && !customer.equals("VZW")) || (serverProtocol.equals("ITU") && customer.equals("VZW")))
                 {
                     continue;
                 }
                 boolean isReleaseMath = false;
                 if(serverRelease.equals(release))
                 {
                     isReleaseMath = true;
                 }
                 else
                 {
                     JSONArray portingReleaseList = JSONArray.fromObject("[\"" + porting_release.replace("+", "").replace(",", "\",\"") + "\"]");
                     if(IsInJSONArray(serverRelease,portingReleaseList))
                     {
                         isReleaseMath = true;
                     }
                     else
                     {
                         if(porting_release.endsWith("+"))
                         {
                             int serverReleasePostion = postionInJSONArray(serverRelease,portingReleaseList );
                             if(serverReleasePostion!= -1)
                             {
                                 int LastReleasePostion = postionInJSONArray(porting_release.substring(porting_release.lastIndexOf(",")+1, porting_release.length() -1),portingReleaseList );
                                 if(LastReleasePostion != -1 && serverReleasePostion >= LastReleasePostion)
                                 {
                                     isReleaseMath = true;
                                 }
                             }
                         }
                     }
                     
                 }
                 
                 if(!isReleaseMath)
                 {
                     continue;
                 }
                 kvmList.add(serverName);
             }
             if(!new_lab_number.equals(old_lab_number))
             {
                 IsSame = false;
                 //logger.error(old_lab_number + " --- " + new_lab_number);
             }
             if(!new_mate.equals(old_mate))
             {
                 IsSame = false;
                 //logger.error(old_mate + " --- " + new_mate);
             }
             if(!new_special_data.equals(old_special_data))
             {
                 IsSame = false;
                 //logger.error(old_special_data + " --- " + new_special_data);
             }
             if(!new_base_data.equals(old_base_data))
             {
                 IsSame = false;
                 //logger.error(old_base_data + " --- " + new_base_data);
             }
             if(!new_second_data.equals(old_second_data))
             {
                 IsSame = false;
                 //logger.error(old_second_data + " --- " + new_second_data);
             }
             
             if(!IsSame)
             {
                 old_lab_number = new_lab_number;
                 old_mate = new_mate;
                 old_special_data = new_special_data;
                 old_base_data = new_base_data;
                 old_second_data = new_second_data;
                 GroupId ++;
                 IsSame = true;
             }
             
             prep.setString(1, caseName);
             prep.setString(2, new_lab_number);
             prep.setString(3, new_mate);
             prep.setString(4, new_special_data);
             prep.setString(5, new_base_data);
             prep.setString(6, new_second_data);
             prep.setString(7, release);
             prep.setString(8, porting_release);
             prep.setString(9, spaArray.toString());
             prep.setString(10, rtdbArray.toString());
             prep.setString(11, kvmList.toString());
             prep.setString(12, customer);
             prep.setInt(13, GroupId);
             prep.addBatch();
             isExcute = true;
         }
         if(isExcute)
         {
             connection.setAutoCommit(false);
             prep.executeBatch();
             connection.setAutoCommit(true);
         }
        }
        catch(SQLException e1)
        {
            logger.error(e1);
        }
        catch(Exception e2)
        {
            logger.error(e2);
            
        }
        finally
        {
            try
            {
                if(state!=null)
                {
                    state.close();
                }
                if(connection!=null)
                {
                    connection.close();
                }
            }
            catch(SQLException e3)
            {
                
                e3.printStackTrace();
            }
         
        }   
    }
    
    private void UpdatedistributeDB(JSONArray KVMList)
    {
        JSONArray caseList = new JSONArray();
        
        Connection connection=null;
        Statement state=null;
        try
        {
             Class.forName("org.sqlite.JDBC");
             String CaseInfoDB=ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
             connection=DriverManager.getConnection("jdbc:sqlite:"+CaseInfoDB);
             state = connection.createStatement();
             String query_sql;
             if(KVMList.size() > 0)
             {
                 query_sql = "select case_name from toDistributeCases where ";
                 for (int i = 0; i < KVMList.size(); i ++)
                 {
                     query_sql += "server like '%" + KVMList.getString(i) +"%' or ";
                 }
                 query_sql = query_sql.substring(0, query_sql.length()-4) + ";";
                 logger.debug(query_sql);
                 ResultSet result=state.executeQuery(query_sql);
                 while(result.next())
                 {
                     caseList.add(result.getString("case_name"));
                 }
             }
             
             query_sql = "select case_name from DailyCase where case_status = 'I';";
             //query_sql = "select case_name from DailyCase where case_name not in (select case_name from toDistributeCases);";
             ResultSet result2=state.executeQuery(query_sql);
             while(result2.next())
             {
                 caseList.add(result2.getString("case_name"));
             }  
             
             query_sql = "delete from toDistributeCases where case_name in (select case_name from toDistributeCases where case_name not in (select case_name from DailyCase where case_status = 'I'));";
             state.executeUpdate(query_sql);
             if(caseList.size() > 0)
             {
                 UpdateCaseStatusDB(caseList);
             }
         
        }
        catch(SQLException e1)
        {
            logger.error(e1);
        }
        catch(Exception e2)
        {
            
            logger.error(e2);
        }
        finally
        {
            try
            {
                if(state!=null)
                {
                    state.close();
                }
                if(connection!=null)
                {
                    connection.close();
                }
            }
            catch(SQLException e3)
            {
                
                e3.printStackTrace();
            }
         
        }
    }
    
    private JSONArray genCaseListToLab(String ServerName)
    {
        JSONArray caseList = new JSONArray();

        Connection connection=null;
        Statement state=null;
        int old_group = -1, new_group;
        try
        {
             Class.forName("org.sqlite.JDBC");
             String CaseInfoDB=ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
             connection=DriverManager.getConnection("jdbc:sqlite:"+CaseInfoDB);
             state = connection.createStatement();
             String query_case_sql = "select case_name from DistributedCaseTbl;";
             ResultSet result2=state.executeQuery(query_case_sql);
             JSONArray case_name_list = new JSONArray();
             while(result2.next())
             {
                 String Case_name=result2.getString("case_name");
                 case_name_list.add(Case_name);
             }
             
             
             String query_sql = "select case_name, group_id from toDistributeCases where server like '%" + ServerName +"%' and case_name not in "
                     + case_name_list.toString().replace("\"", "'").replace("[", "(").replace("]", ")")     + " order by group_id";
             logger.info(query_sql);
             ResultSet result=state.executeQuery(query_sql);
             int CaseCount = 0;
             while(result.next())
             {
                 new_group =  result.getInt("group_id");
                 if(new_group != old_group)
                 {
                     if(old_group != -1)
                     {
                         break;
                     }
                     old_group = new_group;
                 }
                 caseList.add(result.getString("case_name"));
                 CaseCount ++;
                 if(CaseCount >= Integer.valueOf(ConfigProperites.getInstance().getMaxCaseSizeForOneLab()))
                 {
                     break;
                 }
                 
             }
             logger.info(ServerName + " case: " + caseList.toString());

        }
        catch(SQLException e1)
        {
            logger.error(e1);
        }
        catch(Exception e2)
        {
            logger.error(e2);
            
        }
        finally
        {
            try
            {
                if(state!=null)
                {
                    state.close();
                }
                if(connection!=null)
                {
                    connection.close();
                }
            }
            catch(SQLException e3)
            {
                
                e3.printStackTrace();
            }
         
        }
        return caseList;
    }
    
    public JSONObject GetDistributeCases()
    {
        JSONObject AvailableCases = new JSONObject();
        JSONObject Cases = new JSONObject();
        JSONArray changedKvmList = updateKvmDB();
        UpdatedistributeDB(changedKvmList);
        
        JSONArray Servers =  CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock, true, null);
        JSONObject ServerMem;
        for (int i = 0; i < Servers.size(); i ++)
        {
            if(Servers.getJSONObject(i).getJSONObject(Constant.TASKSTATUS).getString(Constant.STATUS).equals(Constant.CASESTATUSIDLE))
            {
                
                ServerMem = Servers.getJSONObject(i).getJSONObject(Constant.LAB);
                String serverName = ServerMem.getString(Constant.SERVERNAME);
                
                logger.debug("idle server: " + serverName);
                JSONArray caseList = genCaseListToLab(serverName);
                JSONObject labInfo = new JSONObject();
                labInfo.put("uuid", UUID.randomUUID().toString());
                labInfo.put("case_list", caseList);
                DbOperation.UpdateDistributedCase(caseList, serverName);
                Cases.put(serverName, labInfo);
            }
        }
        AvailableCases.put("availableCase", Cases);
        return AvailableCases;
    }
}
