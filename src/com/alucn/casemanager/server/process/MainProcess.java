package com.alucn.casemanager.server.process;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.alucn.casemanager.server.common.CaseCacheOpt;
import com.alucn.casemanager.server.common.CaseConfigurationCache;
import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.exception.SysException;
import com.alucn.casemanager.server.common.util.ParamUtil;


public class MainProcess {

	private static final Logger logger = Logger.getLogger(MainProcess.class);
	/**
	 * req start
	 * @param reqJson
	 * @return
	 */
	
	public String process(String reqJson, Socket socket, BlockingQueue<String> queue){
		String rspJson = "";//response message
		JSONObject reqHead = new JSONObject();//head
		JSONObject reqBody = new JSONObject();//body
		
		try {
			logger.debug("[Request processing start...]");
			//get head
			reqHead = ParamUtil.getReqHead(reqJson);
			//get body
			reqBody = ParamUtil.getReqBody(reqJson);
			
			String serverName = reqBody.getJSONObject(Constant.LAB).getString(Constant.SERVERNAME);
			ReceiveAndSendRun.serverName = serverName;
			CaseConfigurationCache.queueOfClient.put(serverName, queue);
			
			// type
			switch (reqHead.getString(Constant.REQTYPE)) {
				case Constant.CASEREQTYPUP:
					rspJson=CaseCacheOpt.updateCase(reqBody);
					break;
				case Constant.CASECOMMAND:
					rspJson=CaseCacheOpt.commandCase(reqHead);
					break;
				case Constant.CASEINORUP:
					rspJson=CaseCacheOpt.inOrUdatabase(reqBody);
					break;
				case Constant.CASELISTACK:
					rspJson=CaseCacheOpt.caseListAck(reqBody);
					break;
			}
		}catch (SysException e) {
			logger.error("["+reqJson+"]");
			logger.error("["+e.getMessage()+"]");
			logger.error(ParamUtil.getErrMsgStrOfOriginalException(e.getCause()));
		}catch (Exception e) {
			logger.error("["+reqJson+"]");
			logger.error(ParamUtil.getErrMsgStrOfOriginalException(e));
		} catch (Throwable e) {
			logger.error("["+reqJson+"]");
			logger.error(ParamUtil.getErrMsgStrOfOriginalException(e));
		}finally{
		}
		return rspJson;
	}
}
