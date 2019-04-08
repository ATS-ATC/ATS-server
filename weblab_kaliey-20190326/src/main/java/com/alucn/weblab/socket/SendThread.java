package com.alucn.weblab.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SendThread implements Runnable{

	private DatagramSocket dSocket;
	
	public SendThread(DatagramSocket dSocket) {
		super();
		this.dSocket = dSocket;
	}

	@Override
	public void run() {
		try {
			BufferedReader buffr = new BufferedReader(new InputStreamReader(System.in));
			String line = null;
			while((line=buffr.readLine())!=null){
				if("886".equals(line)) break;
				byte[] buff = line.getBytes();
				DatagramPacket dPacket = new DatagramPacket(buff,buff.length,InetAddress.getByName("127.0.0.1"),10001);
				dSocket.send(dPacket);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
