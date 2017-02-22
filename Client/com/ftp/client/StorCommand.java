package com.ftp.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JProgressBar;

public class StorCommand{
	
	private File file=null;
	private String hostname=null;
	private Integer dataPort=null;
	private boolean fileCheckedOnServer=false;
	private DataSocket dataSocket=null;
	
	public StorCommand(String hostname, Integer dataPort) {
		this.hostname = hostname;
		this.dataPort = dataPort;
	}
	
	public String uploadFileOnServer(DataInputStream controldis, DataOutputStream controldos, String options[], JProgressBar jProgressBar){
		String serverMsg =null;
			file=new File(options[0]);
		try{
			//to check file exists on client	
			if(file.exists()){
				
				//to check if file is present on server
				if(!fileCheckedOnServer){
					fileCheckedOnServer=true;
					String filePathParts[]=options[0].split("\\\\");
					String fileName=filePathParts[filePathParts.length-1];
					System.out.println("Sending PORT COMMAND");
					controldos.writeUTF(FTPCommand.PORT.getValue()+" "+Utility.getIpAddress()+","+dataPort);
					String msg=controldis.readUTF();
					System.out.println(msg);
					System.out.println("Sending STOR COMMAND");
					controldos.writeUTF(FTPCommand.STOR.getValue());
					dataSocket=new DataSocket(hostname,dataPort);
					FTPClient.threadsMap.put(Thread.currentThread(),dataSocket);
					dataSocket.connectToDataPort();
					dataSocket.getDatados().writeUTF(fileName);
					serverMsg = dataSocket.getDatadis().readUTF();
					if(serverMsg.equals("File already exists")){
						return StringConstants.FILE_ON_SERVER_EXISTS;
					}
				}
				
				if(options.length > 1 && options[1].equals(StringConstants.YES)){
					dataSocket.getDatados().writeUTF(StringConstants.YES);
					serverMsg=dataSocket.getDatadis().readUTF();
				}else if(options.length > 1 && options[1].equals(StringConstants.NO)){
					dataSocket.getDatados().writeUTF(StringConstants.NO);
					fileCheckedOnServer=false;
				}
				
				//uploading file to the server
				if(serverMsg !=null && serverMsg.toLowerCase().equals("send file")){
					FileInputStream fis=new FileInputStream(file);
					Long l = new Long(file.length());
					//controldos.writeUTF(String.valueOf(l));
					int result = l.intValue();
					jProgressBar.setVisible(true);
		            jProgressBar.setMaximum(result);
		            int val=0;
		            System.out.println("Filename :"+file.getName());
			        int ch;
			        do
			        {
			            	
			            ch=fis.read();
			            dataSocket.getDatados().writeUTF(String.valueOf(ch));
			            val++;
			            jProgressBar.setValue(val);
			            jProgressBar.setString(file.getName()+" uploading... "+val);
			        }
			        while(ch!=-1);
			        fis.close();
			        serverMsg=dataSocket.getDatadis().readUTF();
			        System.out.println("Server Msg :" + serverMsg);
			        dataSocket.closeDataConnections();
			        fileCheckedOnServer=false;
				}
				
			}else{
				return StringConstants.FILE_ON_CLIENT_NOTEXISTS;
			}
		}catch(IOException e){
			System.out.println("Exception occurred in StorCommand:uploadFileOnServer()");
			//e.printStackTrace();
		}
		return null;
	}

	


}
