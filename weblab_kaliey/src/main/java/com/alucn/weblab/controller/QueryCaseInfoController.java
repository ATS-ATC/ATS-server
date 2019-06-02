package com.alucn.weblab.controller;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alucn.weblab.service.ConfigOptService;
import com.alucn.weblab.service.QueryCaseInfoService;
import com.alucn.weblab.utils.TimeUtil;
import com.alucn.weblab.utils.jsonUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


@Controller
public class QueryCaseInfoController { 
	private static Logger logger = Logger.getLogger(QueryCaseInfoController.class);
	@Autowired(required=true)
	private QueryCaseInfoService queryCaseInfoService;
	@Autowired(required=true)
	private ConfigOptService configOptService;

	@RequestMapping(value="/getQueryCaseInfoTable")
	@ResponseBody
	public Map<String,Object> getQueryCaseInfoTable(HttpSession session,HttpServletRequest request ,String qtype) throws Exception{

		String limit = request.getParameter("limit")==null?"":request.getParameter("limit").toString().trim();
		String offset = request.getParameter("offset")==null?"":request.getParameter("offset").toString().trim();
		String feature = request.getParameter("feature")==null?"":request.getParameter("feature").toString().trim();
		String mate = request.getParameter("mate")==null?"":request.getParameter("mate").toString().trim();
		String lab = request.getParameter("labnumber")==null?"":request.getParameter("labnumber").toString().trim();
		String sort = request.getParameter("sort")==null?"":request.getParameter("sort").toString().trim();
		String sortOrder = request.getParameter("sortOrder")==null?"":request.getParameter("sortOrder").toString().trim();
		
		
		
		Map<String,Object> returnMap = new HashMap<String,Object>();
		String userName = session.getAttribute("login").toString();
		//String auth = session.getAttribute("auth").toString();
		//ArrayList<HashMap<String, Object>> queryCaseInfoTable = queryCaseInfoService.getQueryCaseInfoTable(userName, auth, qtype,feature, offset, limit,"all");
		//int total = queryCaseInfoService.getQueryCaseInfoTableCount(userName, auth, qtype,feature);
		//ArrayList<HashMap<String, Object>> queryCaseInfoTable = queryCaseInfoService.getQueryCaseInfoTableNew(userName, qtype,feature, offset, limit,"all");
		ArrayList<HashMap<String,Object>> queryCaseInfoTable = queryCaseInfoService.getQueryCaseInfoTableNew(userName, qtype,feature, offset, limit,"all",sort,sortOrder,mate,lab);
		//计算hodingduration
		if(queryCaseInfoTable.size()>0) {
			String warnning= "0";
			JSONArray config = configOptService.getConfig();
			if(config.size()>0) {
				for (int i = 0; i < config.size(); i++)
				{
				    JSONObject con_obj = config.getJSONObject(i);
				    String con_key = con_obj.getString("con_key");
					if("case.dashboard.warning".equals(con_key)) {
						warnning=con_obj.getString("con_value");
					}
				}
			}
			for (HashMap<String, Object> hashMap : queryCaseInfoTable) {
				String submit_date = (String) hashMap.get("submit_date");
				if(!"".equals(submit_date)&&submit_date!=null) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
					Date parse = sdf.parse(submit_date);
					Date date = new Date();
					int equation = TimeUtil.equation(parse.getTime(),date.getTime());
					hashMap.put("hodingduration", equation);
				}else {
					hashMap.put("hodingduration", "-1");
				}
				hashMap.put("warnning",warnning);
			}
		}
		int total = queryCaseInfoService.getQueryCaseInfoTableCount(userName, qtype,feature,mate,lab);
		returnMap.put("rows", queryCaseInfoTable);
		returnMap.put("total", total);
		
		return returnMap;
	}
	@RequestMapping(value="/getQueryCaseInfo")
	public String getQueryCaseInfo(Model model,String qtype) {
		if(!"".equals(qtype)){
			model.addAttribute("qtype", qtype);
		}else {
			return "";
		}
		return "query-case-status-info";
	}
	
	@RequestMapping(value="/getExportCaseInfoTable")
	@ResponseBody
	public void getExportCaseInfoTable(HttpSession session,HttpServletRequest request,HttpServletResponse response ,String qtype,String etype,String ftype) throws Exception{
		
		
		String userName = session.getAttribute("login").toString();
		//String auth = session.getAttribute("auth").toString();
		ArrayList<HashMap<String, Object>> queryCaseInfoTable = queryCaseInfoService.getQueryCaseInfoTableNew(userName, qtype,"", "", "",etype,"","","","");
		
		if(ftype!=null && !"".equals(ftype)) {
			if("csv".equals(ftype)) {
				
				//HSSFWorkbook  wb = new HSSFWorkbook();
				XSSFWorkbook  wb = new XSSFWorkbook();//java.lang.NoSuchFieldError: RETURN_NULL_AND_BLANK
				/*InputStream iStream = new FileInputStream("");
				XSSFWorkbook  wb = new XSSFWorkbook(iStream);*/
				
				
				// Style the cell with borders all around.
			    /*CellStyle style = wb.createCellStyle();
			    style.setBorderBottom(BorderStyle.THIN);
			    style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			    style.setBorderLeft(BorderStyle.THIN);
			    style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			    style.setBorderRight(BorderStyle.THIN);
			    style.setRightBorderColor(IndexedColors.BLACK.getIndex());
			    style.setBorderTop(BorderStyle.THIN);
			    style.setTopBorderColor(IndexedColors.BLACK.getIndex());
			    
			    style.setFillPattern(style.getFillPatternEnum().SOLID_FOREGROUND);
			    style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			    
			    CellStyle tstyle = wb.createCellStyle();
			    tstyle.setBorderBottom(BorderStyle.THIN);
			    tstyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			    tstyle.setBorderLeft(BorderStyle.THIN);
			    tstyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			    tstyle.setBorderRight(BorderStyle.THIN);
			    tstyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
			    tstyle.setBorderTop(BorderStyle.THIN);
			    tstyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
			    
			    tstyle.setFillPattern(tstyle.getFillPatternEnum().SOLID_FOREGROUND);
			    tstyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());*/
			    
		        Sheet sh = wb.createSheet();
		        Row title = sh.createRow(0);
		        logger.info("XSSFWorkbook : 1");
		        for(int i=0 ;i<queryCaseInfoTable.size();i++) {
		        	//logger.info("queryCaseInfoTable : "+i);
		        	Row row = sh.createRow(i+1);
		        	HashMap<String,Object> hashMap = queryCaseInfoTable.get(i);
		        	int j=0;
		        	for(String key :hashMap.keySet()) {
		        		//sh.autoSizeColumn(j);自适应单元格长度，会使导出性能下降，慎用
		        		if(i==0) {
		        			Cell titleCell = title.createCell(j);
		        			titleCell.setCellValue(""+key);
		        			//titleCell.setCellStyle(tstyle);
		        		}
		        		Cell cell = row.createCell(j);
		        		String value = hashMap.get(key)+"";
		        		cell.setCellValue(value);
		        		//cell.setCellStyle(style);
		        		j++;
		        	}
		        	
		        }
		        logger.info("XSSFWorkbook : 2");
		        Date day=new Date();    
		        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss"); 
		        response.setCharacterEncoding("utf-8");
		        response.setContentType("multipart/form-data");
		        response.setHeader("Content-Disposition", "attachment;fileName=CS_"+qtype+"_"+df.format(day)+".csv");
		        try {  
		            OutputStream out = response.getOutputStream();
		            BufferedOutputStream bufout = new BufferedOutputStream(out);
		            bufout.flush();
		            wb.write(bufout);
		            bufout.close();
		        } catch (IOException e) {  
		            e.printStackTrace();  
		        }
			}
			if("json".equals(ftype)) {
				JSONArray result = JSONArray.fromObject(queryCaseInfoTable);
				String formatJson = jsonUtil.formatJson(result.toString());
				Date day=new Date();    
		        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss"); 
		        response.setCharacterEncoding("utf-8");
		        response.setContentType("multipart/form-data");
		        response.setHeader("Content-Disposition", "attachment;fileName=CS_"+qtype+"_"+df.format(day)+".json");
		        try {  
		            OutputStream out = response.getOutputStream();
		            BufferedOutputStream bufout = new BufferedOutputStream(out);
		            bufout.flush();
		            
		            Writer write = new OutputStreamWriter(bufout, "UTF-8");
		            write.write(formatJson);
		            write.flush();
		            write.close();
		            bufout.close();
		        } catch (IOException e) {  
		            e.printStackTrace();  
		        }
			}
			
			
		}
		
		
	}
}
