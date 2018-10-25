package test.nokia.request;

import com.alucn.casemanager.server.common.util.HttpReq;

import net.sf.json.JSONObject;

public class AutoInstallLab {
	
	public static void main(String[] args) {
		JSONObject reqdate = new JSONObject();
		reqdate.put("ins_flag", "0");
		reqdate.put("protocol", "ITU");
		String[] labname = {"CHSP12B"};
		reqdate.put("labname", labname);
		String[] DB = {"CDBRTDB", "HTIDDB"};
		reqdate.put("DB", DB);
		reqdate.put("mate", "N");
		reqdate.put("release", "SP18.9");
		String[] SPA = {"NWTGSM", "CDRPPGW"};
		reqdate.put("SPA", SPA);
		
		//System.out.println("reqdate >> "+reqdate.toString());
		
		
		
        String resResult = HttpReq.reqUrl("http://135.251.249.124:9333/spadm/default/certapi/certtask.json", reqdate.toString());
		System.out.println("resResult >> "+resResult);
		System.out.println(resResult=="OK");
		System.out.println("OK".equals(resResult));
		
        /*JSONObject labResult = HttpReq.reqUrl("http://135.251.249.124:9333/spadm/default/labapi/dailylab/CHSP12Z.json");
        System.out.println("labResult >> "+labResult);
        if(labResult.isEmpty()) {
        	System.err.println("1");
        }*/
        
    }
}
