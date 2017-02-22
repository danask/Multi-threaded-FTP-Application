package com.ftp.client.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.ftp.client.FTPClient;
import com.ftp.client.RetrCommand;

public class FTPClientRetrTest {
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
			System.out.println("Serve Data Port: "+server_data_port);
			dos.writeUTF("RETR");
			RetrCommand retrCommand=new RetrCommand("ec2-54-191-11-167.us-west-2.compute.amazonaws.com",server_data_port);
			String msg=retrCommand.downLoadFile(dis,dos,new String[]{"test.txt"}, null);
			System.out.println(msg);
			if(msg !=null && msg.equals("File Already Exists.Want to OverWrite (Y/N) ?")){
				retrCommand.downLoadFile(dis,dos,new String[]{"test.txt","Y"}, null);
			}
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