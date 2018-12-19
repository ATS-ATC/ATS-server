package test.nokia.request;

import java.io.IOException;

import com.alucn.casemanager.server.common.util.Fifowriter;

public class TestIo {
	public static void main(String[] args) {
		Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					while(true){
						System.out.println("I love you");
						Fifowriter.writerFile("D:/", "aaa.txt", "I love you");
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		t1.start();
		
		
		
		Thread t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					while(true){
						System.out.println("I do it");
						Fifowriter.writerFile("D:/", "aaa.txt", "I do it");
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		
		t2.start();
	}
}
