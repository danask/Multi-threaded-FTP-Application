package com.ftp.server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

public class FTPServer {
	public static void main(String args[]){
		// Server socket variable
		ServerSocket FTPServerControlSoc = null;
		try{
			// Making server control socket on port 21
			FTPServerControlSoc = new ServerSocket(21);
			System.out.println("FTP server started on port 21");
			while(true){
				System.out.println("Waiting for new connection:- ");
				// waiting for client to connect to the server socket and calling clients constructor
				new ClientConnected(FTPServerControlSoc.accept());
			}
		}
		catch(IOException ioexception){
			System.err.println("IO Exception occured : "+ioexception.getMessage());
		}
		finally {
			try {
				// closing the server socket
				System.out.println("Server socket closed!");
				FTPServerControlSoc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

class ClientConnected extends Thread{
	Socket clientSocket;
	DataInputStream dis;
	DataOutputStream dos;
	ServerSocket FTPServerDataSoc = null;
	
	ClientConnected(Socket clientSocket) {
		this.clientSocket = clientSocket;
		try {
			// getting client input and output stream
			dis = new DataInputStream(clientSocket.getInputStream());
			dos = new DataOutputStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Client Connected...");
		// create a new thread for the connected client
		start();
	}
	
	protected void pasv(){
		System.out.println("Passive command issued!");
		// opening a data port at server for client to connect to it
		// TODO Generate a data port above 1023 and below 65535
		try {
			FTPServerDataSoc = new ServerSocket(2222);
			// TODO if able to create data port reply by 227 then give the port number
			dos.writeUTF("2222");
			// TODO If unable to open data port reply by 425
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void run(){
		while(true){
			System.out.println("Waiting for control command from client: ");
			try {
				String command = dis.readUTF();
				System.out.println("Command from client: "+command);
				String toUpperCommand = command.toUpperCase();
				// To handle PASV command issued from client
				if(toUpperCommand.equals("PASV")){
					pasv();
				}
				// TODO if client does not disconnect then initiate a auto disconnect after certain time
				if(toUpperCommand.equals("DISCONNECT")){
					System.out.println("Client disconnected");
					FTPServerDataSoc.close();
					break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}
