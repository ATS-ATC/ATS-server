package com.alucn.weblab.disarray;

import java.sql.*;
import org.apache.log4j.Logger;
import com.alucn.casemanager.server.common.util.ParamUtil;
import net.sf.json.JSONArray;

public class DbOperation {

    public static Logger logger = Logger.getLogger(DbOperation.class);
    

    public static void DeleteDistributedCase(JSONArray UnNeedServers)
    {
        String CaseInfoDB=ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
        Connection connection=null;
        Statement stat = null;
        try
        {
             Class.forName("org.sqlite.JDBC");
             //connection=DriverManager.getConnection("jdbc:sqlite:"+Dbpath);
             connection=DriverManager.getConnection("jdbc:sqlite:"+CaseInfoDB);
             stat = connection.createStatement();
             String UpdateSql = "Delete from DistributedCaseTbl where server_name in (";
             for(int i = 0; i < UnNeedServers.size(); i++)
             {
                 UpdateSql += "'" + UnNeedServers.getString(i) + "', ";
             }
             UpdateSql = UpdateSql.substring(0, UpdateSql.length()-2) + ");";
             
             stat.executeUpdate( UpdateSql);
         
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
                if(stat!=null)
                {
                    stat.close();
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
    
    public static void UpdateDistributedCase(JSONArray CaseList, String server_name)
    {
        String CaseInfoDB=ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
        Connection connection=null;
        Statement stat = null;
        try
        {
             Class.forName("org.sqlite.JDBC");
             connection=DriverManager.getConnection("jdbc:sqlite:"+CaseInfoDB);
             PreparedStatement prep = connection.prepareStatement(
                     "replace into DistributedCaseTbl(case_name, server_name) values (?, ?);");
             
             for(int k = 0; k < CaseList.size(); k++)
             {
                prep.setString(1, CaseList.getString(k));
                prep.setString(2, server_name);
                prep.addBatch();
             }
             
             connection.setAutoCommit(false);
             prep.executeBatch();
             connection.setAutoCommit(true);
         
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
                if(stat!=null)
                {
                    stat.close();
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

}
