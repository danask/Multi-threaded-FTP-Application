package com.ftp.client;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Utility {
	
	public  static String getIpAddress(){
		String address=null;
		try {
			InetAddress ip=InetAddress.getLocalHost();
			address=ip.getHostAddress().replace(".", ",");
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return address;
	}
	
	public static Integer calculateProgressBarValue(double size){
		System.out.println("In calculate");
		Double value=new Double(size);
		return value.intValue();
	}

}
