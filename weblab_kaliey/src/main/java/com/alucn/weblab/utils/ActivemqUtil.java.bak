package com.alucn.weblab.utils;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.spring.ActiveMQConnectionFactory;

public class ActivemqUtil {
	public static void main(String[] args) throws JMSException {
		
		
	}
	public void sendMsg(String userName,String password,String queueName) throws JMSException {
		ConnectionFactory cf = new ActiveMQConnectionFactory();
		Connection conn = cf.createConnection(userName, password);
		Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Queue queue = session.createQueue(queueName);
		
	}
}
