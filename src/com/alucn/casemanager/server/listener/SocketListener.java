package com.alucn.casemanager.server.listener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.log4j.Logger;

import com.alucn.casemanager.server.common.ConfigProperites;
import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.casemanager.server.process.ReceiveAndSendRun;

/**
 * socket listener
 */
public class SocketListener implements ServletContextListener {

	private ServerSocket serverSocket;
	private int listenerPort;
	public static Logger logger = Logger.getLogger(SocketListener.class);
	public static long _COUNT = 0;

	public void contextDestroyed(ServletContextEvent arg0) {
		if (serverSocket != null) {
			if (!serverSocket.isClosed()) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					logger.error(ParamUtil.getErrMsgStrOfOriginalException(e));
				}
			}

		}
	}

	public void contextInitialized(ServletContextEvent arg0) {}

	public void initialize() throws Exception {
		try {
			logger.info("[SocketListener Init...]");
			
			// listener port
			listenerPort = Integer.parseInt(ConfigProperites.getInstance().getListenerPort());
			// maximum number of threads to handle socket requests
			final int synThreadMaxNumber = Integer.parseInt(ConfigProperites.getInstance().getThreadMaxNumber());
			logger.info("[maximum number of threads to handle socket requests" + synThreadMaxNumber + "]");
			
			
			try {
				serverSocket = new ServerSocket(listenerPort);
				logger.info("[listener port " + listenerPort + " success" + "]");
			} catch (IOException e) {
				logger.error("[listener port " + listenerPort + "failed" + "]");
				throw e;
			}
			
			// Init listener thread
			new Thread(new Runnable() {

				public void run() {
					// Init thread pool
					// unlimited buffer pool
					// ExecutorService executorService =
					// Executors.newCachedThreadPool();
					// limited buffer pool
					ExecutorService executorService = Executors
							.newFixedThreadPool(synThreadMaxNumber);
					
					//This type can be used to easily monitor thread pool status
					ThreadPoolExecutor threadPoolExecutor =(ThreadPoolExecutor)executorService;
					//Socket to be processed
					Socket socket = null;
					
					int readTimeout = Integer.parseInt(ConfigProperites.getInstance().getReadTimeout());
					int waitQueueMaxSize  = Integer.parseInt(ConfigProperites.getInstance().getQueueMaxSize());
					//Loop blocking port
					while (!serverSocket.isClosed()) {
						try {
							
							//Blocking the listening port, to a socket request, return request socket
							socket = serverSocket.accept();
							
							if(threadPoolExecutor.getActiveCount() >= synThreadMaxNumber && threadPoolExecutor.getQueue().size() >= waitQueueMaxSize){
								//Request host address
								logger.debug("socket.getInetAddress()  = " +socket.getInetAddress());
								//Number of active threads in the thread pool
								logger.debug("threadPoolExecutor.getActiveCount()  = " +threadPoolExecutor.getActiveCount());
							}else{
								//request host
								String host = socket.getInetAddress().toString().replace("/", "");
								logger.debug("socket.getInetAddress()  = " +host);
								logger.debug("activeCount=" +Thread.activeCount()+"  currentThreadName="+Thread.currentThread().getName()+"  currentThreadID="+Thread.currentThread().getId()+" socket run start  ");
								//Number of active threads in the thread pool
								logger.debug("threadPoolExecutor.getActiveCount()  = " +threadPoolExecutor.getActiveCount());
								 
								socket.setSoTimeout(readTimeout);
								executorService.execute(new ReceiveAndSendRun(socket));
							}
						} catch (Exception e) {
							logger.error("[Failed to monitor master thread socket processing]");
							if(null!=socket){
								//Request host address
								logger.error("[Socket request host address锛� " +socket.getInetAddress()+"]");
							}
							logger.error(ParamUtil.getErrMsgStrOfOriginalException(e));
						}
					}
				}

			}).start();

		} catch (Exception e) {
			logger.error("[Socket listener failed to start]");
			logger.error(ParamUtil.getErrMsgStrOfOriginalException(e));
			throw e;
		} 
	}
	
	/**
	 * @param i
	 * @return
	 */
	public  byte[] int2ByteArr(int i){
		byte[] result = new byte[4];
		result[0] = (byte)((i>>24)&0xFF);
		result[1] = (byte)((i>>16)&0xFF);
		result[2] = (byte)((i>>8)&0xFF);
		result[3] = (byte)(i&0xFF);
		return result;
	}
}
