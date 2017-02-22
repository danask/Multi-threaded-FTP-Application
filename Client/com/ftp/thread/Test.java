package com.ftp.thread;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Test {
	public static void main(String[] args) {
		A a=new A();
		Thread t1=new Thread(a); 
		t1.run();
		t1.run();
		
		/*Thread t2=new Thread(a);
		t2.run();
		
		Thread t3=new Thread(a);
		t3.run();*/
		try {
			InetAddress ip=InetAddress.getLocalHost();
			System.out.println(ip.getHostAddress().replace(".", ","));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		
		
	}
}
