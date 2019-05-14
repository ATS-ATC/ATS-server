package com.alucn.weblab.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.weblab.dao.impl.MainDaoImpl;
import com.alucn.weblab.utils.KalieyMysqlUtil;


@Service("mainService")
public class MainService {
	
	@Autowired
	private MainDaoImpl mainDaoImpl;
	
	private KalieyMysqlUtil jdbc = KalieyMysqlUtil.getInstance();
	
	private Map<String, Integer> caseCounter = new HashMap<String, Integer>();
	
	public ArrayList<HashMap<String, Object>> getCustomerCount() throws Exception{
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		String sql = "select a.customer,cc,ff,ii,rr from (\n" + 
				"select customer,count(1) cc from cases_info_db.case_tag where case_status='S' group by customer\n" +
				")a\n" + 
				"left join (\n" + 
				"select customer,count(1) ff from cases_info_db.case_tag where case_status='F' group by customer\n" +
				") b on a.customer=b.customer\n" + 
				"left join (\n" + 
				"select customer,count(1) ii from cases_info_db.case_tag where case_status='I' group by customer\n" +
				") c on a.customer=c.customer\n" + 
				"left join (\n" + 
				"select customer,count(1) rr from cases_info_db.case_tag where case_status='R' group by customer\n" +
				") d on a.customer=d.customer";
		ArrayList<HashMap<String, Object>> query = mainDaoImpl.query(jdbc, sql);
		return query;
	}
	public ArrayList<HashMap<String, Object>> getReleaseCount() throws Exception{
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		/*String sql = "select a.release,cc,ff from (\n" + 
				"select release,count(1) cc from DftTag where case_status='S' group by release order by release asc\n" + 
				")a \n" + 
				"left join (\n" + 
				"select release,count(1) ff from DftTag where case_status='F' group by release\n" + 
				") b on a.release=b.release";*/
		String sql="select a.base_release ,cc,ff,ii,rr from ( \n" +
				"        select base_release,count(1) cc from cases_info_db.case_tag where case_status='S' group by base_release\n" +
				"    )a \n" +
				"    left join ( \n" +
				"        select base_release,count(1) ff from cases_info_db.case_tag where case_status='F' group by base_release \n" +
				"    ) b on a.base_release=b.base_release \n" +
				"    left join ( \n" +
				"        select base_release,count(1) ii from cases_info_db.case_tag where case_status='I' group by base_release \n" +
				"    ) c on a.base_release=c.base_release \n" +
				"    left join ( \n" +
				"        select base_release,count(1) rr from cases_info_db.case_tag where case_status='R' group by base_release\n" +
				"    ) d on a.base_release=d.base_release";
		ArrayList<HashMap<String, Object>> query = mainDaoImpl.query(jdbc, sql);
		return query;
	}

	public Map<String, Integer> getStatistics() throws Exception {
    	caseCounter.put("S", 0);
    	caseCounter.put("F", 0);
    	caseCounter.put("P", 0);
    	caseCounter.put("O", 0);
    	caseCounter.put("PP", 0);
    	caseCounter.put("I", 0);
    	caseCounter.put("R", 0);
    	/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		int total=0;
    	for(String state: caseCounter.keySet()){
			String statisticsAll = "SELECT COUNT(case_name) FROM cases_info_db.case_tag WHERE case_status='"+state+"'; " ;
			/*
			if (state=="J") {
				statisticsAll = "SELECT COUNT(case_name) FROM DftTag WHERE case_status='F' AND jira_id <> ''; " ;
			}
			*/
			Integer value = 0;
			ArrayList<HashMap<String, Object>> result = mainDaoImpl.query(jdbc, statisticsAll);
			for(int i=0; i<result.size();i++){
				for(String key: result.get(i).keySet()){
					value = Integer.parseInt(result.get(i).get(key).toString().trim());
				}
			}
			total+=value;
			caseCounter.put(state, value);
    	}
    	caseCounter.put("T", total);
	    return caseCounter;
	}

	public MainDaoImpl getMainDaoImpl() {
		return mainDaoImpl;
	}

	public void setMainDaoImpl(MainDaoImpl mainDaoImpl) {
		this.mainDaoImpl = mainDaoImpl;
	}
	public ArrayList<HashMap<String, Object>> getWelcomeInfo(String useName) throws Exception {
		/*String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("DftCaseDB");
		JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);*/
		String sql ="select count(a.case_name) allCase,count(b.case_name) fCase,count(c.case_name) sCase\n" + 
				"from cases_info_db.case_tag a\n" +
				"left join cases_info_db.case_tag b on a.case_name=b.case_name and b.case_status='F'\n" +
				"left join cases_info_db.case_tag c on a.case_name=c.case_name and c.case_status='S'\n" +
				"where a.author='"+useName+"'";
		ArrayList<HashMap<String, Object>> result = mainDaoImpl.query(jdbc, sql);
		return result;
	}
	public ArrayList<HashMap<String, Object>> getWelcomeBottom(String useName, String limit, String offset, boolean hasRole) throws Exception{
		Boolean reporterFlag = getReporterFlag(useName);
		ArrayList<HashMap<String, Object>> resultList = new ArrayList<HashMap<String, Object>>();
		if(reporterFlag || hasRole) {
			resultList = getReporterList(useName,limit,offset,hasRole);
		}else {
			resultList = getUserNormalList(useName,limit,offset,hasRole);
		}
		return resultList;
	}
	public int getWelcomeBottomCount(String useName, boolean hasRole) throws Exception{
		Boolean reporterFlag = getReporterFlag(useName);
		int result=0;
		if(reporterFlag || hasRole) {
			result = getReporterListCount(useName,hasRole);
		}else {
			result = getUserNormalListCount(useName,hasRole);
		}
		return result;
	}
	private int getReporterListCount(String useName, boolean hasRole) throws Exception {
		String sql ="select count(*) as rc from \n" + 
		        "(select \n" + 
                "a.feature_number \n"+
                "from (\n" + 
                "select feature_number,user_name,min(ftc_date) ftc_date,sum(case_num) case_num from cases_info_db.feature_permission group by feature_number,user_name\n" + 
                ")a\n" + 
                "left join (\n" + 
                "select feature_number,author,count(1) dft from cases_info_db.case_tag where type='dft' group by feature_number,author\n" + 
                ") b on a.feature_number = b.feature_number and a.user_name= b.author\n" + 
                "left join (\n" + 
                "select feature_number,author,count(1) fail from cases_info_db.case_tag where case_status='F' group by feature_number,author\n" + 
                ") c on a.feature_number = c.feature_number and a.user_name= c.author\n" + 
                "left join (\n" + 
                "select feature_number,owner,count(1) uncheck from cases_info_db.error_case_info where err_reason is null and feature_number!='' group by feature_number,owner\n" + 
                ") d on a.feature_number = d.feature_number and a.user_name= d.owner\n" + 
                "left join (\n" + 
                "select trim(user_name) user_name,trim(to_reporter) to_reporter from cases_info_db.user_info \n" + 
                ")e on a.user_name= e.user_name\n" + 
                "where 1=1\n" + 
                "and a.ftc_date != 0\n";
                if(!hasRole) {
                    sql = sql +"and e.to_reporter='"+useName+"'\n" ;
                }
                sql = sql + "and e.to_reporter is not null\n" + 
                "group by a.feature_number,e.to_reporter) as reports";
		//System.out.println(sql);
		ArrayList<HashMap<String, Object>> result = mainDaoImpl.query(jdbc, sql);
		int reInt = 0;
		if(result.size()>0) {
			Object object = result.get(0).get("rc");
			reInt = Integer.parseInt(object.toString());
		}
		return reInt;
	}
	public ArrayList<HashMap<String, Object>> getReporterList(String useName, String limit, String offset, boolean hasRole) throws Exception {
		String sql ="select \n" + 
				"a.feature_number,\n" + 
				"min(a.ftc_date)ftc_date,\n" + 
				"sum(a.case_num) case_num,\n" + 
				"sum(ifnull(b.dft,0)) dft,\n" + 
				"sum(ifnull(c.fail,0)) fail,\n" + 
				"sum(ifnull(d.uncheck,0)) uncheck,\n" + 
				"ifnull((sum(ifnull(b.dft,0))/sum(a.case_num))*100,0) submit_rate,\n" + 
                "ifnull((sum(ifnull(c.fail,0))/sum(ifnull(b.dft,0)))*100,0) fail_rate, \n" +
                "to_reporter \n" +
				"from (\n" + 
				"select feature_number,user_name,min(ftc_date) ftc_date,sum(case_num) case_num from cases_info_db.feature_permission group by feature_number,user_name\n" + 
				")a\n" + 
				"left join (\n" + 
				"select feature_number,author,count(1) dft from cases_info_db.case_tag where type='dft' group by feature_number,author\n" + 
				") b on a.feature_number = b.feature_number and a.user_name= b.author\n" + 
				"left join (\n" + 
				"select feature_number,author,count(1) fail from cases_info_db.case_tag where case_status='F' group by feature_number,author\n" + 
				") c on a.feature_number = c.feature_number and a.user_name= c.author\n" + 
				"left join (\n" + 
				"select feature_number,owner,count(1) uncheck from cases_info_db.error_case_info where err_reason is null and feature_number!='' group by feature_number,owner\n" + 
				") d on a.feature_number = d.feature_number and a.user_name= d.owner\n" + 
				"left join (\n" + 
				"select trim(user_name) user_name,trim(to_reporter) to_reporter from cases_info_db.user_info \n" + 
				")e on a.user_name= e.user_name\n" + 
				"where 1=1\n" + 
				"and a.ftc_date != 0\n";
				if(!hasRole) {
					sql = sql +"and e.to_reporter='"+useName+"'\n" ;
				}
				sql = sql + "and e.to_reporter is not null\n" + 
				"group by a.feature_number,e.to_reporter\n"+
				"order by fail_rate desc,submit_rate asc\n"+
				"limit "+offset+","+limit;
		//System.out.println(sql);
		ArrayList<HashMap<String, Object>> result = mainDaoImpl.query(jdbc, sql);
		return result;
	}
	private int getUserNormalListCount(String useName, boolean hasRole) throws Exception {
		String sql ="select count(*) nc\n" + 
				"from (\n" + 
				"select feature_number,user_name,min(ftc_date) ftc_date,sum(case_num) case_num from cases_info_db.feature_permission group by feature_number,user_name\n" + 
				")a\n" + 
				"left join (\n" + 
				"select feature_number,author,count(1) dft from cases_info_db.case_tag where type='dft' group by feature_number,author\n" + 
				") b on a.feature_number = b.feature_number and a.user_name= b.author\n" + 
				"left join (\n" + 
				"select feature_number,author,count(1) fail from cases_info_db.case_tag where case_status='F' group by feature_number,author\n" + 
				") c on a.feature_number = c.feature_number and a.user_name= c.author\n" + 
				"left join (\n" + 
				"select feature_number,owner,count(1) uncheck from cases_info_db.error_case_info where err_reason is null and feature_number!='' group by feature_number,owner\n" + 
				") d on a.feature_number = d.feature_number and a.user_name= d.owner\n" + 
				"left join (\n" + 
				"select trim(user_name) user_name,trim(to_reporter) to_reporter from cases_info_db.user_info \n" + 
				")e on a.user_name= e.user_name\n" + 
				"where 1=1\n" + 
				"and a.ftc_date != 0\n" ;
				if(!hasRole) {
					sql = sql +"and a.user_name='"+useName+"'\n" ;
				}
		//System.out.println(sql);
		ArrayList<HashMap<String, Object>> result = mainDaoImpl.query(jdbc, sql);
		int reInt = 0;
		if(result.size()>0) {
			Object object = result.get(0).get("nc");
			reInt = Integer.parseInt(object.toString());
		}
		return reInt;
	}
	public ArrayList<HashMap<String, Object>> getUserNormalList(String useName, String limit, String offset, boolean hasRole) throws Exception {
		String sql ="select \n" + 
				"a.feature_number,a.ftc_date,a.case_num,ifnull(b.dft,0) dft,ifnull(c.fail,0) fail,ifnull(d.uncheck,0) uncheck,\n" + 
				"ifnull((dft/a.case_num)*100,0) submit_rate,\n" + 
				"ifnull((fail/dft)*100,0) fail_rate\n" + 
				"from (\n" + 
				"select feature_number,user_name,min(ftc_date) ftc_date,sum(case_num) case_num from cases_info_db.feature_permission group by feature_number,user_name\n" + 
				")a\n" + 
				"left join (\n" + 
				"select feature_number,author,count(1) dft from cases_info_db.case_tag where type='dft' group by feature_number,author\n" + 
				") b on a.feature_number = b.feature_number and a.user_name= b.author\n" + 
				"left join (\n" + 
				"select feature_number,author,count(1) fail from cases_info_db.case_tag where case_status='F' group by feature_number,author\n" + 
				") c on a.feature_number = c.feature_number and a.user_name= c.author\n" + 
				"left join (\n" + 
				"select feature_number,owner,count(1) uncheck from cases_info_db.error_case_info where err_reason is null and feature_number!='' group by feature_number,owner\n" + 
				") d on a.feature_number = d.feature_number and a.user_name= d.owner\n" + 
				"left join (\n" + 
				"select trim(user_name) user_name,trim(to_reporter) to_reporter from cases_info_db.user_info \n" + 
				")e on a.user_name= e.user_name\n" + 
				"where 1=1\n" + 
				"and a.ftc_date != 0\n" ;
				if(!hasRole) {
					sql = sql + "and a.user_name = '"+useName+"'\n";
				}
				
				sql = sql +"order by fail_rate desc,submit_rate asc limit "+offset+","+limit;
		//System.out.println(sql);
		ArrayList<HashMap<String, Object>> result = mainDaoImpl.query(jdbc, sql);
		return result;
	}
	public int getNormalInfoCount(String feature_number) throws Exception {
		String sql ="select count(1) as nc\n" + 
				"from (\n" + 
				"select feature_number,user_name,min(ftc_date) ftc_date,sum(case_num) case_num from cases_info_db.feature_permission group by feature_number,user_name\n" + 
				")a\n" + 
				"left join (\n" + 
				"select feature_number,author,count(1) dft from cases_info_db.case_tag where type='dft' group by feature_number,author\n" + 
				") b on a.feature_number = b.feature_number and a.user_name= b.author\n" + 
				"left join (\n" + 
				"select feature_number,author,count(1) fail from cases_info_db.case_tag where case_status='F' group by feature_number,author\n" + 
				") c on a.feature_number = c.feature_number and a.user_name= c.author\n" + 
				"left join (\n" + 
				"select feature_number,owner,count(1) uncheck from cases_info_db.error_case_info where err_reason is null and feature_number!='' group by feature_number,owner\n" + 
				") d on a.feature_number = d.feature_number and a.user_name= d.owner\n" + 
				"left join (\n" + 
				"select trim(user_name) user_name,trim(to_reporter) to_reporter from cases_info_db.user_info \n" + 
				")e on a.user_name= e.user_name\n" + 
				"where 1=1\n" + 
				"and a.ftc_date != 0\n" + 
				"and a.feature_number = '"+feature_number+"'";
		//System.out.println(sql);
		ArrayList<HashMap<String, Object>> result = mainDaoImpl.query(jdbc, sql);
		int reInt = 0;
        if(result.size()>0) {
            Object object = result.get(0).get("nc");
            reInt = Integer.parseInt(object.toString());
        }
        return reInt;
	}
	public ArrayList<HashMap<String, Object>> getNormalInfo(String feature_number, String limit, String offset) throws Exception {
		String sql ="select \n" + 
				"a.feature_number,a.user_name,e.to_reporter,a.ftc_date,a.case_num,ifnull(b.dft,0) dft,ifnull(c.fail,0) fail,ifnull(d.uncheck,0) uncheck,\n" + 
				"ifnull((dft/a.case_num)*100,0) submit_rate,\n" + 
				"ifnull((fail/dft)*100,0) fail_rate\n" + 
				"from (\n" + 
				"select feature_number,user_name,min(ftc_date) ftc_date,sum(case_num) case_num from cases_info_db.feature_permission group by feature_number,user_name\n" + 
				")a\n" + 
				"left join (\n" + 
				"select feature_number,author,count(1) dft from cases_info_db.case_tag where type='dft' group by feature_number,author\n" + 
				") b on a.feature_number = b.feature_number and a.user_name= b.author\n" + 
				"left join (\n" + 
				"select feature_number,author,count(1) fail from cases_info_db.case_tag where case_status='F' group by feature_number,author\n" + 
				") c on a.feature_number = c.feature_number and a.user_name= c.author\n" + 
				"left join (\n" + 
				"select feature_number,owner,count(1) uncheck from cases_info_db.error_case_info where err_reason is null and feature_number!='' group by feature_number,owner\n" + 
				") d on a.feature_number = d.feature_number and a.user_name= d.owner\n" + 
				"left join (\n" + 
				"select trim(user_name) user_name,trim(to_reporter) to_reporter from cases_info_db.user_info \n" + 
				")e on a.user_name= e.user_name\n" + 
				"where 1=1\n" + 
				"and a.ftc_date != 0\n" + 
				"and a.feature_number = '"+feature_number+"'\n"+
				"limit "+offset+","+limit;
		//System.out.println(sql);
		ArrayList<HashMap<String, Object>> result = mainDaoImpl.query(jdbc, sql);
		return result;
	}
	public Boolean getReporterFlag(String useName) throws Exception {
		String sql ="select user_name from cases_info_db.user_info \n" +
				"where trim(to_reporter)='"+useName+"'";
		//System.out.println(sql);
		ArrayList<HashMap<String, Object>> result = mainDaoImpl.query(jdbc, sql);
		if(result.size()>0) {
			return true;
		}else {
			return false;
		}
	}
}
