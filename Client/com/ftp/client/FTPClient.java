package com.ftp.client;


import java.util.List;
import java.util.Map;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JProgressBar;

public class FTPClient {
	
	public static Socket controlServerSocket = null;
	public static DataInputStream controldis=null;
	public static DataOutputStream controldos=null;
	public static String dataPort=null;
	public static String hostname=null;
	public String fileSize=null;
	
	private JProgressBar downloadjProgressBar=null;
	private JProgressBar jProgressBar=null;
	
	private StorCommand storCommand=null;
	private RetrCommand retrCommand=null;
	
	public static Map<Thread,DataSocket>threadsMap=new HashMap<Thread,DataSocket>();
	//public static String downloadPath="D:\\Master's Program\\Semester3\\Advance SE\\FTP\\FTP";
	
	
	public FTPClient(){
		
	}
	
	public DataInputStream getControldis() {
		return controldis;
	}

	public DataOutputStream getControldos() {
		return controldos;
	}
	
	public String getFileSize() {
		return fileSize;
	}
	
	public JProgressBar getjProgressBar() {
		return jProgressBar;
	}

	public void setjProgressBar(JProgressBar jProgressBar) {
		this.jProgressBar = jProgressBar;
	}
	
	public JProgressBar getDownloadjProgressBar() {
		return downloadjProgressBar;
	}

	public void setDownloadjProgressBar(JProgressBar downloadjProgressBar) {
		this.downloadjProgressBar = downloadjProgressBar;
	}

	public String connectToControlPort(String hostName){
		boolean isConnect=false;
		String msg=null;
		try{
			hostname=hostName;
			controlServerSocket = new Socket(hostname, 21);
			controldis = new DataInputStream(controlServerSocket.getInputStream());
			controldos = new DataOutputStream(controlServerSocket.getOutputStream());
			msg=controldis.readUTF();
			
		} catch (UnknownHostException e) {
			msg=StringConstants.SOMETHING_WENT_WRONG;
			e.printStackTrace();
		} catch (IOException e) {
			try{
				controlServerSocket.close();
				msg=StringConstants.SOMETHING_WENT_WRONG;
				e.printStackTrace();
			}catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return msg;
	}
	
	public String clientCommand(String command, String[] options) {
		FTPCommand ftpCommand = FTPCommand.valueOf(command.toUpperCase());
		System.out.println(ftpCommand);
		String message=null;
		String commandMsg=null;
		try{
			switch (ftpCommand) {
				case USER:
					System.out.println("Sending USER COMMAND");
					controldos.writeUTF(FTPCommand.USER.getValue());
					controldos.writeUTF(options[0]);
					controldos.writeUTF(options[1]);
					message=controldis.readUTF();
					System.out.println("User creation :"+message);
				break;
				case QUIT:
					System.out.println("Sending QUIT COMMAND");
					controldos.writeUTF(FTPCommand.QUIT.getValue());
					closeControlConnections();
				break;
				case PORT:

				break;
				case TYPE:

				break;
				case MODE:

				break;
				case STRU:

				break;
				case RETR:
					if(retrCommand ==null){
						retrCommand=new RetrCommand(hostname, dataPort);
					}
					message=retrCommand.downLoadFile(controldis, controldos, options, downloadjProgressBar);
					fileSize=retrCommand.getFileSize();
				break;
				case STOR:
					if(storCommand == null){
						 storCommand=new StorCommand(hostname,Integer.valueOf(dataPort));
					}
					message=storCommand.uploadFileOnServer(controldis, controldos, options, jProgressBar);
				break;
				case NOOP:

				break;
				case PASV:
					System.out.println("Sending PASV COMMAND");
					controldos.writeUTF(FTPCommand.PASV.getValue());
					dataPort = controldis.readUTF();
					System.out.println(dataPort);
				break;
				case LIST:
					System.out.println("Sending LIST COMMAND");
					controldos.writeUTF(FTPCommand.LIST.getValue());
					message=controldis.readUTF();
					commandMsg=controldis.readUTF();
					System.out.println(commandMsg);
				break;
				default:
				break;
			}		
		}catch (IOException e) {
			System.out.println("Exception occurred in FTPClient:clientCommand()");
			//e.printStackTrace();
		}
		return message;
	}
	
	// close control connections
	private void closeControlConnections(){
		try {
			DownLoadThread downloadThread=null;
			UploadThread uploadThread=null;
			for(Map.Entry<Thread, DataSocket>entry:threadsMap.entrySet()){;
				
				/*if (entry.getKey()  instanceof DownLoadThread) {
					DownLoadThread downloadThread=(DownLoadThread)entry.getKey();
					System.out.println("**********Its download thread object**********");
					System.out.println(downloadThread.getFilename());
					System.out.println("Before file delete");
					Files.deleteIfExists(FileSystems.getDefault().getPath(FTPClient.downloadPath+"\\"+downloadThread.getFilename()));
					System.out.println("After file delete");
				}*/
				if (entry.getKey() instanceof DownLoadThread){
					downloadThread=(DownLoadThread)entry.getKey();
					downloadThread.getjProgressBar().setVisible(false);
				}else if(entry.getKey() instanceof UploadThread){
					uploadThread=(UploadThread)entry.getKey();
					uploadThread.getjProgressBar().setVisible(false);
				}
				DataSocket dataSocket=entry.getValue();
				dataSocket.closeDataConnections();
			}
			controldis.close();
			controldos.close();
			controlServerSocket.close();
		} catch (IOException e) {
			System.out.println("Exception occurred in FTPClient:closeControlConnections()");
			//e.printStackTrace();
		}
	}
	
}

