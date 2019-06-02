package com.alucn.casemanager.server.common;

import net.sf.json.JSONObject;

import com.alucn.casemanager.server.common.constant.Constant;
/**
 * case opt
 * @author wanghaiqi
 *
 */
public class CaseCacheOpt {
	//1.update
	public static String updateCase(JSONObject body) throws Exception{
		CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock,false,body);
		return Constant.UPDATE;
	}
	//2.command
	public static String commandCase(JSONObject head) throws Exception{
		/*if(head.getResult().equals(Constant.COMMANDSUCCESS)){
			CaseConfigurationCache cfc = CaseConfigurationCache.getInstance();
			cfc.readOrWriteSingletonCaseCommand(cfc.lock, false,null);
			logger.info("[command success ]");
		}*/
		return Constant.UPDATE;
	}
	//3.insertorupdate
	public static String inOrUdatabase(JSONObject body) throws Exception{
		CaseConfigurationCache.inOrUcaseStatus(body);
		return Constant.UPDATE;
	}
}
