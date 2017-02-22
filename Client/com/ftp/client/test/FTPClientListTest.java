package com.ftp.client.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class FTPClientListTest {
	public static void main(String[] args) {
		DataOutputStream dos=null;
		DataInputStream dis=null;
		Socket serverSocket = null;
		
		try {
			// connecting a FTP server on control port 21
			serverSocket = new Socket("ec2-54-191-11-167.us-west-2.compute.amazonaws.com", 21);
			dos=new DataOutputStream(serverSocket.getOutputStream());
			dis=new DataInputStream(serverSocket.getInputStream());
			dos.writeUTF("PASV");
			String server_data_port = dis.readUTF();
			dos.writeUTF("USER");
			dos.writeUTF("lionel");
			String result=dis.readUTF();
			System.out.println(result);
			dos.writeUTF("LIST");
			String filesList=dis.readUTF();
			System.out.println(filesList);
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
