package com.alucn.weblab.utils;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class TopicReceiver1 implements MessageListener {
	 
	/*
	 * (非 Javadoc) 
	 * <p>Title: onMessage</p> 
	 * <p>Description: </p>
	 * @param arg0
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	@Override
	public void onMessage(Message message) {
		// TODO Auto-generated method stub
		TextMessage textMessage = (TextMessage) message;
		try {
			System.out.println("TopicReceiver1接收到消息内容是：" + textMessage.getText());
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

