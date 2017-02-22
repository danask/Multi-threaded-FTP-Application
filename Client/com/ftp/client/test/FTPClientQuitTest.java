package com.ftp.client.test;

import com.ftp.client.FTPClient;

public class FTPClientQuitTest {
	public static void main(String[] args) {
		FTPClient ftpClient=new FTPClient();
		ftpClient.connectToControlPort("ec2-54-191-11-167.us-west-2.compute.amazonaws.com");
		ftpClient.clientCommand("QUIT",null);
	}
}
