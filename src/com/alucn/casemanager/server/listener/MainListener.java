package com.alucn.casemanager.server.listener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import com.alucn.casemanager.server.common.ConfigProperites;
import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.exception.SysException;
import com.alucn.casemanager.server.common.util.JdbcUtil;
import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.casemanager.server.process.DistributeCase;

/**
 * Program start listener
 * @author wanghaiqi
 */
public class MainListener {
	public static String configFilesPath;
	public static Logger logger = Logger.getLogger(MainListener.class);
	/**
	 * @param args
	 */
	public static void init(String [] args){

		if (args.length!=1) {
			System.out.println("[The number of arguments is incorrect, and there is only one parameter for the profile path]");
		}else{
            configFilesPath = args[0];
            
			try {
				
				//log4j Initialization
				PropertyConfigurator.configure(configFilesPath+File.separator+"log4j.properties");
				//Init config
				String dbFile = ParamUtil.getUnableDynamicRefreshedConfigVal("CaseInfoDB");
				JdbcUtil jdbc = new JdbcUtil(Constant.DATASOURCE, dbFile);
				Map<String, String> mapConfigs = new HashMap<String, String>();
				String queryConfigs = "select * from config";
				ArrayList<HashMap<String,Object>> configs = jdbc.query(queryConfigs);
				for(int i=0; i<configs.size(); i++){
					mapConfigs.put(configs.get(i).get("con_key").toString(), configs.get(i).get("con_value").toString());
				}
				ConfigProperites.getInstance().refreshConfiguration(mapConfigs);
				//init timer
//				new DftagTimer(Integer.parseInt(ConfigProperites.getInstance().getCaseDftagTimerDelay()),Integer.parseInt(ConfigProperites.getInstance().getCaseDftagTimerDelay()));
				
				logger.info("-----------------------------------------------------------------------------------");
				logger.info("	java.class.path = "+System.getProperties().getProperty("java.class.path"));
				logger.info("	sun.boot.library.path = "+System.getProperties().getProperty("sun.boot.library.path"));
				logger.info("	java.runtime.version = "+System.getProperties().getProperty("java.runtime.version"));
				logger.info("-----------------------------------------------------------------------------------");
				
				File file = new File(configFilesPath);
				if(file.isDirectory()){
					logger.info("[Profile path：]"+configFilesPath);
					
					//start socket listener
					SocketListener socketListener = new SocketListener();
					socketListener.initialize();
					
					//start distribute case listener
					new Thread(new DistributeCase()).start();
					
					//start distribute command listener
//					new Thread(new DistributeCommand()).start();
				}else{
					logger.error("[Incorrect parameter path "+configFilesPath+" Not a directory]");
				}

			}catch (SysException e) {
				logger.error("["+e.getMessage()+"]");
				logger.error("[casemanager service startup failed]");
				logger.error(ParamUtil.getErrMsgStrOfOriginalException(e.getCause()));
				System.exit(1);
			} catch (Exception e) {
				logger.error("[casemanager service startup failed]");
				logger.error(ParamUtil.getErrMsgStrOfOriginalException(e));
				System.exit(1);
			}
			return;
        }
	}
	public static void main(String[] args) {
		init(args);
	}
}
