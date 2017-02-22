package FTP;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class FTPClient {
	public static void main(String[] args) {
		Socket serverSocket = null;
		try {
			// connecting a FTP server on control port 21
			// FTP server on amazon
			serverSocket = new Socket("ec2-54-191-11-167.us-west-2.compute.amazonaws.com", 21);
			// local FTP server
			// serverSocket = new Socket("localhost", 21);
			new CommandClient(serverSocket);
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

class CommandClient{
	Socket serverSocket;
	
	DataInputStream dis;
	DataOutputStream dos;
	BufferedReader bufferedReader;
	
	CommandClient(Socket serverSocket) {
		this.serverSocket = serverSocket;
		try {
			dis = new DataInputStream(serverSocket.getInputStream());
			dos = new DataOutputStream(serverSocket.getOutputStream());
			bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			// establishing PASV connection with server
			dos.writeUTF("PASV");
			// getting the data port from the server
			String server_data_port = dis.readUTF();
			System.out.println("Server Data Port: "+server_data_port);
			// disconnecting with the server
			dos.writeUTF("lionel");
			dos.writeUTF("LIST");
			String names = dis.readUTF();
			System.out.println("Server name: "+names);
			dos.writeUTF("QUIT");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				dis.close();
				dos.close();
				bufferedReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}