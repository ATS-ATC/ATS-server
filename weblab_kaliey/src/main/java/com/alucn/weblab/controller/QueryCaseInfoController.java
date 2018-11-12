package com.alucn.weblab.controller;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
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

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alucn.weblab.service.QueryCaseInfoService;
import com.alucn.weblab.utils.jsonUtil;

import net.sf.json.JSONArray;


@Controller
public class QueryCaseInfoController { 
	
	@Autowired(required=true)
	private QueryCaseInfoService queryCaseInfoService;

	@RequestMapping(value="/getQueryCaseInfoTable")
	@ResponseBody
	public Map<String,Object> getQueryCaseInfoTable(HttpSession session,HttpServletRequest request ,String qtype) throws Exception{

		String limit = request.getParameter("limit")==null?"":request.getParameter("limit").toString().trim();
		String offset = request.getParameter("offset")==null?"":request.getParameter("offset").toString().trim();
		String feature = request.getParameter("feature")==null?"":request.getParameter("feature").toString().trim();
		
		Map<String,Object> returnMap = new HashMap<String,Object>();
		String userName = session.getAttribute("login").toString();
		//String auth = session.getAttribute("auth").toString();
		//ArrayList<HashMap<String, Object>> queryCaseInfoTable = queryCaseInfoService.getQueryCaseInfoTable(userName, auth, qtype,feature, offset, limit,"all");
		//int total = queryCaseInfoService.getQueryCaseInfoTableCount(userName, auth, qtype,feature);
		ArrayList<HashMap<String, Object>> queryCaseInfoTable = queryCaseInfoService.getQueryCaseInfoTable(userName, qtype,feature, offset, limit,"all");
		int total = queryCaseInfoService.getQueryCaseInfoTableCount(userName, qtype,feature);
		//System.out.println("queryCaseInfoTable:========"+queryCaseInfoTable);
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
		
		//System.out.println("etype============================"+etype);
		System.out.println("ftype============================"+ftype);
		
		String userName = session.getAttribute("login").toString();
		//String auth = session.getAttribute("auth").toString();
		ArrayList<HashMap<String, Object>> queryCaseInfoTable = queryCaseInfoService.getQueryCaseInfoTable(userName, qtype,"", "", "",etype);
		
		if(ftype!=null && !"".equals(ftype)) {
			if("csv".equals(ftype)) {
				Workbook wb = new HSSFWorkbook();
				
				
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
		        for(int i=0 ;i<queryCaseInfoTable.size();i++) {
		        	Row row = sh.createRow(i+1);
		        	HashMap<String,Object> hashMap = queryCaseInfoTable.get(i);
		        	int j=0;
		        	for(String key :hashMap.keySet()) {
		        		sh.autoSizeColumn(j);
		        		if(i==0) {
		        			Cell titleCell = title.createCell(j);
		        			titleCell.setCellValue(key);
		        			//titleCell.setCellStyle(tstyle);
		        		}
		        		Cell cell = row.createCell(j);
		        		String value = (String)hashMap.get(key);
		        		cell.setCellValue(value);
		        		//cell.setCellStyle(style);
		        		j++;
		        	}
		        	
		        }
		        Date day=new Date();    
		        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss"); 
		        //System.out.println(df.format(day));   
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
