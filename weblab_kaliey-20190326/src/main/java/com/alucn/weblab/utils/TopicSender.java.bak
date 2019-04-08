package com.alucn.weblab.utils;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class TopicSender {
	
    @Resource
	private JmsTemplate jmsTopicTemplate;
 
	//发送消息
	public void sendMessage(Destination destination,final String message) { 
		System.out.println("TopicSender发送消息："+message);
		jmsTopicTemplate.send(destination, new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				// TODO Auto-generated method stub
				return session.createTextMessage(message);
			}
		});
	}
}

