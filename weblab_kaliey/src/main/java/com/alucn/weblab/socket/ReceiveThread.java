package com.alucn.weblab.socket;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ReceiveThread implements Runnable{
	
	private DatagramSocket dSocket;

	public ReceiveThread(DatagramSocket dSocket) {
		super();
		this.dSocket = dSocket;
	}
	@Override
	public void run() {
		try {
			while(true) {
				byte[] bytes =  new byte[1024];
				DatagramPacket dPacket = new DatagramPacket(bytes,bytes.length);
				dSocket.receive(dPacket);
				String ip = dPacket.getAddress().getHostAddress();
				String data = new String(dPacket.getData(), 0, dPacket.getLength());
				System.out.println(ip+":"+data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
