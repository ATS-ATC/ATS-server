package test.nokia.request;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import net.sf.json.JSONArray;


public class labSocket {

	public static void main(String[] args) throws Exception{
		ServerSocket ss = new ServerSocket(10098);
		Socket s = ss.accept();
		
		InputStream in = s.getInputStream();
		byte[] buf = new byte[1024];
		int len = in.read(buf);
		System.out.println(new String(buf,0,len));
		
		OutputStream out = s.getOutputStream();
		
		JSONArray infos = new JSONArray();
		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"deptid\":\"1\",\"serverName\":\"BJRMS21E\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverType\": \"Group\",\"serverMate\": \"Primary\",\"mateServer\": \"BJRMS21F\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Dead\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"deptid\":\"1\",\"serverName\":\"BJRMS21C\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverType\": \"Line\",\"serverMate\": \"Primary\",\"mateServer\": \"BJRMS21D\",\"setName\": \"set2\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Running\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"deptid\":\"1\",\"serverName\":\"BJRMS21B\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverType\": \"Group\",\"serverMate\": \"Standalone\",\"mateServer\": \"N\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Ready\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"deptid\":\"1\",\"serverName\":\"BJRMS21D\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverType\": \"Line\",\"serverMate\": \"Secondary\",\"mateServer\": \"BJRMS21C\",\"setName\": \"set2\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Finished\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"deptid\":\"2\",\"serverName\":\"BJRMS21A\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverType\": \"Line\",\"serverMate\": \"Standalone\",\"mateServer\": \"N\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Idle\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"deptid\":\"2\",\"serverName\":\"BJRMS21F\",\"serverIp\":\"135.242.17.206\",\"serverRelease\":\"SP17.9\",\"serverProtocol\":\"ITU\",\"serverType\": \"Group\",\"serverMate\": \"Secondary\",\"mateServer\": \"BJRMS21E\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Idle\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
		infos.add("{\"head\":{\"reqType\":\"caselistack\",\"response\":\"\"},\"body\":{\"lab\":{\"deptid\":\"1\",\"serverName\":\"CHSP05B\",\"serverIp\":\"135.2.213.155\",\"serverRelease\":\"SP18.3\",\"serverProtocol\":\"ANSI\",\"serverType\": \"Line\",\"serverMate\": \"Standalone\",\"mateServer\": \"N\",\"setName\": \"set1\",\"serverSPA\":[\"AethosTest\",\"CDRPP311\",\"CDRPPGW311\",\"DIAMCL179\",\"DROUTER179\",\"ECTRL179\",\"ENWTPPS179\",\"EPAY179\",\"EPPSA179\",\"EPPSM179\",\"GATEWAY179\",\"NWTCOM111\",\"NWTGSM066\"],\"serverRTDB\":[\"SCRRTDBV7\",\"AECIDB179\",\"SGLDB28H\",\"TIDDB28C\",\"GPRSSIM08\",\"AIRTDB179\",\"CTRTDB179\",\"HTIDDB179\",\"PMOUDB179\",\"PROMDB179\",\"SIMDB179\",\"SYDB179\",\"GCIPL312\",\"VTXDB179\",\"SHRTDB28F\",\"CDBRTDB\",\"RCNRDB173\",\"HMRTDB173\",\"SESSDB311\",\"ACMDB104\",\"SIMIDXDB\",\"FSNDB173\",\"UARTDB287\",\"RERTDB279\",\"SFFDB28C\",\"GCURDB\",\"SLTBLRTDB\",\"ID2MDN01\",\"GTMDB28A\"]},\"taskStatus\":{\"status\":\"Idle\",\"runningCase\":\"\"},\"taskResult\":{\"success\":[],\"fail\":[]}}}");
		
		
		byte[] obuf = infos.toString().getBytes();
		out.write(obuf);
		s.close();
		ss.close();
	}

}
