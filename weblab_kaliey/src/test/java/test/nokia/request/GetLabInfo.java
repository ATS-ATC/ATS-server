package test.nokia.request;

import com.alucn.casemanager.server.common.CaseConfigurationCache;
import com.alucn.casemanager.server.common.constant.Constant;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GetLabInfo {
	public static void main(String[] args) {
		JSONArray Servers = CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock, true, null);
		//result.put("Servers", Servers);
		for (int i =0 ;i<Servers.size();i++) {
			JSONObject lab = Servers.getJSONObject(i).getJSONObject(Constant.LAB);
			String deptid = lab.getString("status");
			System.out.println(deptid);
			if(deptid==null) {
				System.out.println("null");
			}
			
		}
	}
}
