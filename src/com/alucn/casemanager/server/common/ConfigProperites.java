package com.alucn.casemanager.server.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Logger;

import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.casemanager.server.listener.MainListener;

/**
 * system config
 * @author wanghaiqi
 *
 */
public class ConfigProperites {
	public static Logger logger = Logger.getLogger(ConfigProperites.class);
	private final static ReentrantLock tlock = new ReentrantLock();
	private static ConfigProperites singletonConfigProperites;
	//Attribute configuration for non dynamic refresh
	Properties singletonProperties ;
	private String listenerPort;
	private String readPeriodTimeout;
	private String readTimeout;
	private String queueMaxSize;
	private String threadMaxNumber;
	private String caseClientStatusTime;
	private String caseClientSocketTime;
	private String caseClientCommandTime;
	private String caseClientDistributeTime;
	private String caseClientRetryTime;
	private String caseDftagTimerDelay;
	private String caseDftagTimerPeriod;
	private String maxCaseSizeForOneLab;
	private String caseClientSftpSendShellName;
	private String caseClientSftpSourcePath;
	private String caseClientSftpTargetPath;
	private String caseClientSftpUserName;
	private String caseClientSftpPassword;
	private String caseClientSftpPort;
	
	public String getListenerPort() {
		return listenerPort;
	}

	public void setListenerPort(String listenerPort) {
		this.listenerPort = listenerPort;
	}

	public String getReadPeriodTimeout() {
		return readPeriodTimeout;
	}

	public void setReadPeriodTimeout(String readPeriodTimeout) {
		this.readPeriodTimeout = readPeriodTimeout;
	}

	public String getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(String readTimeout) {
		this.readTimeout = readTimeout;
	}

	public String getQueueMaxSize() {
		return queueMaxSize;
	}

	public void setQueueMaxSize(String queueMaxSize) {
		this.queueMaxSize = queueMaxSize;
	}

	public String getThreadMaxNumber() {
		return threadMaxNumber;
	}

	public void setThreadMaxNumber(String threadMaxNumber) {
		this.threadMaxNumber = threadMaxNumber;
	}

	public String getCaseClientStatusTime() {
		return caseClientStatusTime;
	}

	public void setCaseClientStatusTime(String caseClientStatusTime) {
		this.caseClientStatusTime = caseClientStatusTime;
	}

	public String getCaseClientSocketTime() {
		return caseClientSocketTime;
	}

	public void setCaseClientSocketTime(String caseClientSocketTime) {
		this.caseClientSocketTime = caseClientSocketTime;
	}

	public String getCaseClientCommandTime() {
		return caseClientCommandTime;
	}

	public void setCaseClientCommandTime(String caseClientCommandTime) {
		this.caseClientCommandTime = caseClientCommandTime;
	}

	public String getCaseClientDistributeTime() {
		return caseClientDistributeTime;
	}

	public void setCaseClientDistributeTime(String caseClientDistributeTime) {
		this.caseClientDistributeTime = caseClientDistributeTime;
	}

	public String getCaseClientRetryTime() {
		return caseClientRetryTime;
	}

	public void setCaseClientRetryTime(String caseClientRetryTime) {
		this.caseClientRetryTime = caseClientRetryTime;
	}

	public String getCaseDftagTimerDelay() {
		return caseDftagTimerDelay;
	}

	public void setCaseDftagTimerDelay(String caseDftagTimerDelay) {
		this.caseDftagTimerDelay = caseDftagTimerDelay;
	}

	public String getCaseDftagTimerPeriod() {
		return caseDftagTimerPeriod;
	}

	public void setCaseDftagTimerPeriod(String caseDftagTimerPeriod) {
		this.caseDftagTimerPeriod = caseDftagTimerPeriod;
	}

	public String getMaxCaseSizeForOneLab() {
		return maxCaseSizeForOneLab;
	}

	public void setMaxCaseSizeForOneLab(String maxCaseSizeForOneLab) {
		this.maxCaseSizeForOneLab = maxCaseSizeForOneLab;
	}

	public String getCaseClientSftpSendShellName() {
		return caseClientSftpSendShellName;
	}

	public void setCaseClientSftpSendShellName(String caseClientSftpSendShellName) {
		this.caseClientSftpSendShellName = caseClientSftpSendShellName;
	}

	public String getCaseClientSftpSourcePath() {
		return caseClientSftpSourcePath;
	}

	public void setCaseClientSftpSourcePath(String caseClientSftpSourcePath) {
		this.caseClientSftpSourcePath = caseClientSftpSourcePath;
	}

	public String getCaseClientSftpTargetPath() {
		return caseClientSftpTargetPath;
	}

	public void setCaseClientSftpTargetPath(String caseClientSftpTargetPath) {
		this.caseClientSftpTargetPath = caseClientSftpTargetPath;
	}

	public String getCaseClientSftpUserName() {
		return caseClientSftpUserName;
	}

	public void setCaseClientSftpUserName(String caseClientSftpUserName) {
		this.caseClientSftpUserName = caseClientSftpUserName;
	}

	public String getCaseClientSftpPassword() {
		return caseClientSftpPassword;
	}

	public void setCaseClientSftpPassword(String caseClientSftpPassword) {
		this.caseClientSftpPassword = caseClientSftpPassword;
	}

	public String getCaseClientSftpPort() {
		return caseClientSftpPort;
	}

	public void setCaseClientSftpPort(String caseClientSftpPort) {
		this.caseClientSftpPort = caseClientSftpPort;
	}
	
	
	public void refreshConfiguration(Map<String, String> configs){
		final ReentrantLock lock = tlock;
		lock.lock();
		try{
			ConfigProperites singletonConfigProperite = new ConfigProperites();
			singletonConfigProperite.setCaseClientCommandTime(configs.get(Constant.CASECLIENTCOMMANDTIME));
			singletonConfigProperite.setCaseClientDistributeTime(configs.get(Constant.CASECLIENTDISTRIBUTETIME));
			singletonConfigProperite.setCaseClientRetryTime(configs.get(Constant.CASECLIENTRETRYTIME));
			singletonConfigProperite.setCaseClientSftpPassword(configs.get(Constant.CASECLIENTSFTPPASSWORD));
			singletonConfigProperite.setCaseClientSftpPort(configs.get(Constant.CASECLIENTSFTPPORT));
			singletonConfigProperite.setCaseClientSftpSendShellName(configs.get(Constant.CASECLIENTSFTPSENDSHELLNAME));
			singletonConfigProperite.setCaseClientSftpSourcePath(configs.get(Constant.CASECLIENTSFTPSOURCEPATH));
			singletonConfigProperite.setCaseClientSftpTargetPath(configs.get(Constant.CASECLIENTSFTPTARGETPATH));
			singletonConfigProperite.setCaseClientSftpUserName(configs.get(Constant.CASECLIENTSFTPUSERNAME));
			singletonConfigProperite.setCaseClientSocketTime(configs.get(Constant.CASECLIENTSOCKETTIME));
			singletonConfigProperite.setCaseClientStatusTime(configs.get(Constant.CASECLIENTSTATUSTIME));
			singletonConfigProperite.setCaseDftagTimerDelay(configs.get(Constant.CASEDFTAGTIMERDELAY));
			singletonConfigProperite.setCaseDftagTimerPeriod(configs.get(Constant.CASEDFTAGTIMERPERIOD));
			singletonConfigProperite.setListenerPort(configs.get(Constant.LISTENERPORT));
			singletonConfigProperite.setMaxCaseSizeForOneLab(configs.get(Constant.MAXCASESIZEFORONELAB));
			singletonConfigProperite.setQueueMaxSize(configs.get(Constant.QUEUEMAXSIZE));
			singletonConfigProperite.setReadPeriodTimeout(configs.get(Constant.READPERIODTIMEOUT));
			singletonConfigProperite.setThreadMaxNumber(configs.get(Constant.THREADMAXNUMBER));
			singletonConfigProperite.setReadTimeout(configs.get(Constant.READTIMEOUT));
			singletonConfigProperites = singletonConfigProperite;
		}finally{
			lock.unlock();
		}
	}

	/**
	 * 	singleton
	 */
	private ConfigProperites(){
		singletonProperties = new Properties();
		InputStream in = null;
		try {
			ConfigProperites.class.getClassLoader();
			
			if(null==MainListener.configFilesPath){
				//Configuration files in the project root directory
				in = getClass().getResourceAsStream(File.separator+"dynamic-refresh-unable.properties");
//				File conf = new File("D:\\conf.properties");
//				in = new FileInputStream(conf);
			}else{
				//Jar package external profile directory, a configuration parameter of the entry class
				String confPath =MainListener.configFilesPath +File.separator+  "dynamic-refresh-unable.properties";
				logger.info("[path of conf.properties：]"+confPath);
				File conf = new File(confPath);
				in = new FileInputStream(conf);
			}

		
			if (null != in){
				singletonProperties.load(in);
			}else {
					throw new Exception("fail to load properties properties");
				}
			
		} catch (Exception e) {
			singletonProperties = null;
			logger.error(ParamUtil.getErrMsgStrOfOriginalException(e));
		} finally{
			
				try {
					if(in != null){
						in.close();
						in = null;
					}
				} catch (IOException e) {
					logger.error(ParamUtil.getErrMsgStrOfOriginalException(e));
				}
				
			}
	}
		
	/**
	 *singleton
	 * @return
	 */
	public static ConfigProperites getInstance(){
		return singletonConfigProperites = singletonConfigProperites == null ? new ConfigProperites():singletonConfigProperites;
	}
	/**
	 * @param key
	 * @return
	 */
	public  String  getSingletonProperitesVal(String key){
		return singletonProperties.getProperty(key);
	}
}
