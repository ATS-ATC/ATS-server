package test.nokia.request;

import com.alucn.casemanager.server.common.util.HttpReq;

import net.sf.json.JSONObject;

public class AutoInstallLab {
	
	public static void main(String[] args) {
		JSONObject reqdate = new JSONObject();
		reqdate.put("ins_flag", "0");
		reqdate.put("protocol", "ITU");
		String[] labname = {"CHSP05E"};
		reqdate.put("labname", labname);
		String[] DB = {"TIDDB","CDBRTDB"};
		reqdate.put("DB", DB);
		reqdate.put("mate", "N");
		reqdate.put("release", "SP18.9");
		String[] SPA = {"NWTGSM","DROUTER"};
		reqdate.put("SPA", SPA);
		
        String resResult = HttpReq.reqUrl("http://135.251.249.124:9333/spadm/default/certapi/certtask.json", reqdate.toString());
        System.out.println(resResult);
    }
}
