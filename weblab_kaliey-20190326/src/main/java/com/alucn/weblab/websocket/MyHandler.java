package com.alucn.weblab.websocket;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Service
public class MyHandler extends TextWebSocketHandler {
    //在线用户列表
    private static final Map<String, WebSocketSession> users;
    //用户标识
    private static final String CLIENT_ID = "username";

    static {
        users = new HashMap<String, WebSocketSession>();
    }
    
	@Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //System.out.println("成功建立连接");
        String username= getClientId(session);
        if (username != null) {
            users.put(username, session);
            JSONObject mJsonObject = new JSONObject();
            mJsonObject.put("users", users.keySet());
            mJsonObject.put("onlineFlag", "true");
            if(users.size()>0) {
            	//System.out.println(users.keySet());
            	sendMessageToAllUsers(new TextMessage(mJsonObject.toString()));
            }
            //session.sendMessage(new TextMessage("成功建立socket连接"));
            //System.out.println(username);
            //System.out.println(session);
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        //System.out.println(message.getPayload());
        //WebSocketMessage message1 = new TextMessage("server:"+message);
        //session.sendMessage(message1);
    	String username= getClientId(session);
        //sendMessageToAllUsers(new TextMessage(username+":"+message.getPayload()));
    	;
    	JSONObject mJsonObject = new JSONObject();
    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String format = df.format(new Date());
    	mJsonObject.put("onlineFlag", "false");
    	mJsonObject.put("payload", message.getPayload());
    	mJsonObject.put("username", username);
    	mJsonObject.put("len", message.getPayloadLength());
    	mJsonObject.put("time", format);
    	
    	sendMessageToAllUsers(new TextMessage(mJsonObject.toString()));
    }

    /**
     * 发送信息给指定用户
     * @param clientId
     * @param message
     * @return
     */
    public boolean sendMessageToUser(String clientId, TextMessage message) {
        if (users.get(clientId) == null) {
            return false;
        }
        WebSocketSession session = users.get(clientId);
        if (!session.isOpen()) {
            return false;
        }
        try {
            session.sendMessage(message);
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    /**
     * 广播信息
     * @param message
     * @return
     */
    public boolean sendMessageToAllUsers(TextMessage message) {
        boolean allSendSuccess = true;
        Set<String> clientIds = users.keySet();
        WebSocketSession session = null;
        for (String clientId : clientIds) {
            try {
                session = users.get(clientId);
                if (session.isOpen()) {
                    session.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
                allSendSuccess = false;
            }
        }

        return  allSendSuccess;
    }


    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        //System.out.println("连接出错");
        users.remove(getClientId(session));
        JSONObject mJsonObject = new JSONObject();
        mJsonObject.put("users", users.keySet());
        mJsonObject.put("onlineFlag", "true");
        if(users.size()>0) {
        	//System.out.println(users.keySet());
        	sendMessageToAllUsers(new TextMessage(mJsonObject.toString()));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        //System.out.println("连接已关闭：" + status);
        users.remove(getClientId(session));
        JSONObject mJsonObject = new JSONObject();
        mJsonObject.put("users", users.keySet());
        mJsonObject.put("onlineFlag", "true");
        if(users.size()>0) {
        	//System.out.println(users.keySet());
        	sendMessageToAllUsers(new TextMessage(mJsonObject.toString()));
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 获取用户标识
     * @param session
     * @return
     */
    private String getClientId(WebSocketSession session) {
        try {
            String clientId = (String) session.getAttributes().get(CLIENT_ID);
            return clientId;
        } catch (Exception e) {
            return null;
        }
    }
}
