package com.alucn.weblab.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alucn.casemanager.server.common.util.Fiforeader;
import com.alucn.weblab.dao.impl.SubmitPermissionDaoImpl;
import com.alucn.weblab.utils.JDBCHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * @author haiqiw
 * 2017年6月23日 下午2:02:28
 * desc:ErrorCaseInfoService
 */
@Service("submitPermissionService")
public class SubmitPermissionService {
    private static Logger logger = Logger.getLogger(SubmitPermissionService.class);
	@Autowired(required=true)
	private SubmitPermissionDaoImpl submitPermissionDaoImpl;
	private Map<String, Object> tagItemMap = new HashMap<String, Object>();
	@Autowired
    private ServletContext servletContext;
	
	@SuppressWarnings({ "deprecation", "unchecked" })
    public Map<String, Object> getSubmitPermission() throws NumberFormatException, InterruptedException, IOException{
        
        String configPath = System.getenv("WEBLAB_CONF");
        String [] args1 = {servletContext.getRealPath("conf")};       //localhost server
        String [] args2 = { configPath };

        String tagConfig = (args2[0]!=null?args2[0]:args1[0])+File.separator+"TagConfig.json";

  
        JSONObject do_edit = new JSONObject();
        JSONArray do_checklist = new JSONArray();
        JSONArray do_select = new JSONArray();
        JSONArray do_text = new JSONArray();
        JSONArray do_date = new JSONArray();
        
        JSONArray dynamic_columns = new JSONArray();
        JSONObject dynamic_item = new JSONObject();
        dynamic_item.put("title", "name");
        dynamic_item.put("field", "name");
        dynamic_columns.add(dynamic_item);
        
        dynamic_item = new JSONObject();
        dynamic_item.put("title", "value");
        dynamic_item.put("field", "value");
        dynamic_columns.add(dynamic_item);
        
        JSONObject caseSearchItems = JSONObject.fromObject(Fiforeader.readCaseInfoFromChannel(tagConfig));
        
        for (Object str:caseSearchItems.keySet()) {
            String key = (String)str;
            JSONArray mypara_list = caseSearchItems.getJSONArray(key);
            JSONObject dyamic_obj = new JSONObject();
            dyamic_obj.put("columns", dynamic_columns);
            JSONArray dyamic_datas = new JSONArray();
            for(int i = 0; i < mypara_list.size(); i++)
            {
                JSONObject para = mypara_list.getJSONObject(i);
                String type = para.getString("type");
                String name = para.getString("name");
                String value = "";
                String element_id = key + "-" + name;
                JSONObject dyamic_data = new JSONObject();
                dyamic_data.put("name", name);
                String classes = "editable editable-click editable-empty";
                String display_value = "Empty";
                String data_value = "";
                if("checklist".equals(type))
                {
                    JSONObject obj = new JSONObject();
                    obj.put("id", element_id);
                    obj.put("source", para.getJSONArray("source"));
                    if(para.has("value"))
                    {
                        obj.put("value", para.getJSONArray("value"));
                        display_value = "";
                        for(int j = 0; j < para.getJSONArray("value").size(); j ++)
                        {
                            for(int k = 0; k < para.getJSONArray("source").size(); k ++)
                            {
                                if(para.getJSONArray("value").getString(j).equals(para.getJSONArray("source").getJSONObject(k).getString("value")))
                                {
                                    display_value += "<br>" + para.getJSONArray("source").getJSONObject(k).getString("text");
                                    data_value += "," + para.getJSONArray("source").getJSONObject(k).getString("value");
                                }
                            }
                            
                        }
                        if(!"".equals(display_value))
                        {
                            display_value = display_value.substring(4);
                            data_value = data_value.substring(1);
                        }
                        else
                        {
                            display_value = "Empty";
                        }
                        
                        classes = "editable editable-click";
                    }
                    else
                    {
                        obj.put("value", new JSONArray());
                    }
                    do_checklist.add(obj);
                    
                }
                else if("text".equals(type))
                {
                    JSONObject obj = new JSONObject();
                    obj.put("id", element_id);                   
                    do_text.add(obj);
                  
                }
                else if("date".equals(type))
                {
                    JSONObject obj = new JSONObject();
                    obj.put("id", element_id);
                    do_date.add(obj);
                }
                else if("select".equals(type))
                {
                    JSONObject obj = new JSONObject();
                    obj.put("id", element_id);
                    obj.put("source", para.getJSONArray("source"));
                    if(para.has("value"))
                    {
                        obj.put("value", para.getString("value"));
                        
                        for(int k = 0; k < para.getJSONArray("source").size(); k ++)
                        {
                            if(para.getString("value").equals(para.getJSONArray("source").getJSONObject(k).getString("value")))
                            {
                                display_value = para.getJSONArray("source").getJSONObject(k).getString("text");
                                break;
                            }
                        }
                                          
                        data_value = para.getString("value");
                        classes = "editable editable-click";
                    }
                    else
                    {
                        obj.put("value", "");
                    }
                    do_select.add(obj);
                    
                }
                
                value = "<a href=\"javascript:void(0)\" id=\"" + element_id + "\" data-name=\"" 
                        + element_id + "\" data-type=\"" + type + "\" data-pk=\"undefined\"  data-title=\"" 
                        + name + "\" data-value=\""+ data_value +"\" class=\"editable editable-click editable-empty\">" + display_value + "</a>";
                dyamic_data.put("value", value);
                dyamic_datas.add(dyamic_data);
            }
            dyamic_obj.put("datas", dyamic_datas);
            logger.info("key: ------------------------------" + key);
            logger.info("dyamic_obj: " + dyamic_obj.toString());

            tagItemMap.put(key, dyamic_obj);
            
        }

        
        do_edit.put("select", do_select);
        do_edit.put("text", do_text);
        do_edit.put("date", do_date);
        do_edit.put("checklist", do_checklist);
        logger.info("do_edit: " + do_edit.toString());
        tagItemMap.put("editable_obj", do_edit);
        
        return tagItemMap;
    }
	
	public JSONObject condition_to_jsonobject(String condition)
    {
        JSONObject con = new JSONObject();
        String [] conds = condition.split("&");
        for (int i = 0; i < conds.length; i++)
        {
            String [] paras = conds[i].split("=");
            if(paras.length == 2)
            {
                con.put(paras[0], paras[1]);
            }
            
        }
        return con;
    }
	
	public ArrayList<HashMap<String, Object>> getSubmitPermissionInfo(String limit, String offset,String condition, String login_name) throws Exception{

	    JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
	    String feature_number = "";
	    String user_name = "";
	    JSONObject con = condition_to_jsonobject(condition);
        if(con.has("feature_number"))
        {
            feature_number = con.getString("feature_number");
        }
        if(con.has("user_name"))
        {
            user_name = con.getString("user_name");
        }
        
		String query_sql = "SELECT * FROM cases_info_db.feature_permission WHERE 1=1";
		if(!"".equals(feature_number)){
		    query_sql = query_sql+" and feature_number='"+feature_number+"'";
		}
		if(!"".equals(user_name)){
            query_sql = query_sql+" and user_name='"+user_name+"'";
        } 
		
		query_sql=query_sql+" order by feature_number, user_name ";
		query_sql=query_sql+" limit "+offset+","+limit;
		
		logger.debug(query_sql);
		ArrayList<HashMap<String, Object>> result = submitPermissionDaoImpl.query(jdbc, query_sql);
		return result;
		
	}
	
	public int getSubmitPermissionInfoCount(String condition, String login_name) throws Exception {
	    
        JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
        
        String feature_number = "";
        String user_name = "";
        JSONObject con = condition_to_jsonobject(condition);
        if(con.has("feature_number"))
        {
            feature_number = con.getString("feature_number");
        }
        if(con.has("user_name"))
        {
            user_name = con.getString("user_name");
        }
        String query_sql = "SELECT count(1) ccount FROM cases_info_db.feature_permission WHERE 1=1";
        if(!"".equals(feature_number)){
            query_sql = query_sql+" and feature_number='"+feature_number+"'";
        }
        if(!"".equals(user_name)){
            query_sql = query_sql+" and user_name='"+user_name+"'";
        } 
        
        
        ArrayList<HashMap<String, Object>> result = submitPermissionDaoImpl.query(jdbc, query_sql);
        if(result.size()>0) {
            return Integer.parseInt((String)result.get(0).get("ccount"));
        }else {
            return 0; 
        }
    }
	
	public String deleteSubmitPermission(String feature_number, String user_name, String login_name, String case_type) throws Exception {
        
        JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
        
        
        String delete_sql = "delete FROM cases_info_db.feature_permission WHERE feature_number = '" + feature_number + 
                "' and user_name = '" + user_name + "' and type = '" + case_type + "'";
        
        logger.warn("User: " + login_name + " ---- execute: " + delete_sql);
        int size = submitPermissionDaoImpl.delete(jdbc, delete_sql);
        if(size == 1) {
            return "SUCCESS";
        }
        return "FAIL"; 
        
    }
	
	public String updateSubmitPermission(String feature_number, String user_name, String login_name, String case_type, String field, String value, String new_value) throws Exception {
        
        JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
        String Conditions = "";
        String sets = "";
        if ("feature_number".equals(field))
        {
            Conditions = "feature_number = '" + value + 
                    "' and user_name = '" + user_name + "' and type = '" + case_type + "'";
            sets = "feature_number = '" + feature_number + "'";
        }
        else if("user_name".equals(field))
        {
            Conditions = "feature_number = '" + feature_number + 
                    "' and user_name = '" + value + "' and type = '" + case_type + "'";
            sets = "user_name = '" + user_name + "'";
        }
        else if("type".equals(field))
        {
            Conditions = "feature_number = '" + feature_number + 
                    "' and user_name = '" + user_name + "' and type = '" + value + "'";
            sets = "type = '" + case_type + "'";
        }
        else {
            Conditions = "feature_number = '" + feature_number + 
                    "' and user_name = '" + user_name + "' and type = '" + case_type + "'";
            sets = field + " = '" + new_value + "'";
        }
        String update_sql = "update cases_info_db.feature_permission set " +sets + " WHERE " + Conditions;
        
        logger.warn("User: " + login_name + " ---- execute: " + update_sql);
        int size = submitPermissionDaoImpl.update(jdbc, update_sql);
        logger.warn(size);
        if(size == 1) {
            return "SUCCESS";
        }
        return "FAIL"; 
        
    }
	
public String newSubmitPermission(String user_name, String feature_number, String case_num, String ftc_date, String is_special, String sec_data_num, String case_type, String login_name) throws Exception {
        
        JDBCHelper jdbc = JDBCHelper.getInstance("mysql-1");
        String insert_sql = "insert into cases_info_db.feature_permission values ('" + user_name + "','" + feature_number + "'," + case_num + "," + ftc_date + ",'" + is_special + 
                "'," + sec_data_num + ",'" + case_type.toLowerCase() + "')";
               
        
        logger.warn("User: " + login_name + " ---- execute: " + insert_sql);
        int size = submitPermissionDaoImpl.update(jdbc, insert_sql);
        logger.warn(size);
        if(size == 1) {
            return "SUCCESS";
        }
        return "FAIL"; 
        
    }
	
}
