package com.alucn.weblab.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.alucn.weblab.socket.TcpServer;

public class TestLabListener implements ServletContextListener{

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		new Thread(new TcpServer()).start();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		
	}

}
