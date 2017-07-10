package com.alucn.casemanager.server.common.constant;

import net.sf.json.JSONObject;

/**
 * 
 * @author wanghaiqi
 *
 */
public class Constant {
	
	public static final String HEAD = "head";
	public static final String REQTYPE = "reqType";
	public static final String RESPONSE = "response";
	public static final String BODY = "body";
	public static final String LAB = "lab";
	public static final String IP = "serverIp";
	public static final String SERVERNAME = "serverName";
	public static final String SERVERRELEASE = "serverRelease";
	public static final String SERVERPROTOCOL = "serverProtocol";
	public static final String SERVERSPA = "serverSPA";
	public static final String SERVERRTDB = "serverRTDB";
	public static final String TASKSTATUS = "taskStatus";
	public static final String STATUS = "status";
	public static final String RUNNINGCASE = "runningCase";
	public static final String TASKRESULT = "taskResult";
	public static final String SUCCESS = "success";
	public static final String NAME = "name";
	public static final String FAIL = "fail";
	public static final String CHARACTER_SET_ENCODING_UTF8 = "UTF-8";
	public static final String EMBEDDED_MESSAGE_REQ = "PREREQUESTHEALTH"; 
	public static final String EMBEDDED_MESSAGE_RSP = "PRERESPONSEHEALTH";
	public static final String EMBEDDED_MESSAGE_RES = "PRERESPONSERESCON";
	public static final String EMBEDDED_MESSAGE_WAIT = "PRERESPONSEWAIT";
	public static final String UPDATE = "UPDATESUCCESS";
	public static final String ACKUPDATE = "ACKUPDATESUCCESS";
	public static final String DATASOURCE= "sqlite-0";
	public static final String CASESTATUSREADY = "Ready";
	public static final String CASESTATUSDEAD = "Dead";
	public static final String CASESTATUSIDLE = "Idle";
	public static final String CASESTATUSFINSHED = "Finished";
	public static final String CASESTATUSRUNNING = "Running";
	public static final String CASEINORUP = "insertorupdate";
	public static final String CASEREQTYPUP = "update";
	public static final String CASECOMMAND = "command";
	public static final String CASELISTACK = "caselistack";
	public static final String AVAILABLECASE = "availableCase";
	public static final String CASELIST = "case_list";
	public static final String SPAANDRTDB = "spaANDrtdb.txt";
	public static final JSONObject CASESTATUSPRE = JSONObject.fromObject("{\"head\": {\"reqType\": \"\",\"response\": \"\"},\"body\": {\"lab\": {\"serverIp\": \"\",\"serverName\": \"test\",\"serverRelease\": \"test\",\"serverProtocol\": \"test\",\"serverSPA\": [],\"serverRTDB\": []},\"taskStatus\": {\"status\": \"Dead\",\"runningCase\": \"\"},\"taskResult\": {\"success\": [{\"name\": \"\",\"time\": \"\"}],\"fail\": []}}}");
	public static final String LISTENERPORT="case.socket.listener.port";
	public static final String READPERIODTIMEOUT="case.socket.read_period_timeout";
	public static final String READTIMEOUT="case.socket.read_timeout";
	public static final String QUEUEMAXSIZE="case.socket.wait_queue_max_size";
	public static final String THREADMAXNUMBER = "case.syn.thread_max_number";
	public static final String CASECLIENTSTATUSTIME = "case.client.status.time";
	public static final String CASECLIENTSOCKETTIME = "case.client.socket.time";
	public static final String CASECLIENTCOMMANDTIME = "case.client.command.time";
	public static final String CASECLIENTDISTRIBUTETIME = "case.client.distribute.time";
	public static final String CASECLIENTRETRYTIME = "case.client.retry.time";
	public static final String CASEDFTAGTIMERDELAY = "case.dftag.timer.delay";
	public static final String CASEDFTAGTIMERPERIOD = "case.dftag.timer.period";
	public static final String MAXCASESIZEFORONELAB = "max_case_size_for_one_lab";
	public static final String CASECLIENTSFTPSENDSHELLNAME = "case.client.sftp.sendshellname";
	public static final String CASECLIENTSFTPSOURCEPATH = "case.client.sftp.sourcepath";
	public static final String CASECLIENTSFTPTARGETPATH = "case.client.sftp.targetpath";
	public static final String CASECLIENTSFTPUSERNAME = "case.client.sftp.username";
	public static final String CASECLIENTSFTPPASSWORD = "case.client.sftp.password";
	public static final String CASECLIENTSFTPPORT = "case.client.sftp.port";
}
