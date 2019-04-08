package com.alucn.weblab.socket;

import java.net.DatagramSocket;
import java.net.SocketException;

public class UdpThreadDemo {
	
	public static void main(String[] args) throws SocketException {
		
		DatagramSocket sendSocket = new DatagramSocket();
		DatagramSocket receiveSocket = new DatagramSocket(10001);
		
		new Thread(new SendThread(sendSocket)).start();
		new Thread(new ReceiveThread(receiveSocket)).start();
	}
}

