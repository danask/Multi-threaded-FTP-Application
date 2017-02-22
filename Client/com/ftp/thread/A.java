package com.ftp.thread;

public class A implements Runnable {
	
	int a=10;
	
	@Override
	public void run() {
		System.out.println(a+10);
	}

}
