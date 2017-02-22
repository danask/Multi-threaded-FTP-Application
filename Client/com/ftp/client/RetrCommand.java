package com.ftp.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.swing.JProgressBar;

public class RetrCommand  {
	
	private String hostname=null;
	private String dataPort=null;
	private boolean fileCheckedOnServer=false;
	private File file=null;
	private DataSocket dataSocket=null;
	String serverMsg=null;
	String fileSize=null;
	
	public RetrCommand(String hostname,String dataPort){
		this.hostname=hostname;
		this.dataPort=dataPort;
	}
	
	
	
	public String getFileSize() {
		return fileSize;
	}

	//to download file in FTP
	public String downLoadFile(DataInputStream controldis, DataOutputStream controldos, String options[], JProgressBar jProgressBar)
	{
		System.out.println("Start download file method");
		String clientMsg=null;
		String commandMsg=null;
		FileOutputStream fout=null;
        try {
        	//check file exists on server
        	if(!fileCheckedOnServer){
        		System.out.println("Sending PORT COMMAND");
				controldos.writeUTF(FTPCommand.PORT.getValue()+" "+Utility.getIpAddress()+","+dataPort);
				commandMsg=controldis.readUTF();
				System.out.println(commandMsg);
				System.out.println("Sending RETR COMMAND");
				controldos.writeUTF(FTPCommand.RETR.getValue());
				
				dataSocket=new DataSocket(hostname,Integer.valueOf(dataPort));
				FTPClient.threadsMap.put(Thread.currentThread(),dataSocket);
				dataSocket.connectToDataPort();
        		
				dataSocket.getDatados().writeUTF(options[0]);
				serverMsg=dataSocket.getDatadis().readUTF();
				fileSize=dataSocket.getDatadis().readUTF();
				
				System.out.println("Server Msg :"+serverMsg);
        	}
        	
        	if(serverMsg.equals("File not found on the server")){
				return serverMsg;
			}else if(serverMsg.equals("READY")){
				 file=new File(options[0]);
				 
				//check file exists on client
	            if(!fileCheckedOnServer && file.exists())
	            {
	            	fileCheckedOnServer=true;
	            	clientMsg = "File Already Exists.Want to OverWrite (Y/N) ?";
	            	return clientMsg;
	            }else{
	            	//if file not present on client or if file present and to overwrite 
	            	if(options.length==1 || (options.length > 1 && options[1]=="Y")){
	            		dataSocket.getDatados().writeUTF("Y");
						  fout=new FileOutputStream(file);
				            int ch;
				            String data;
				            
				            Double v=new Double(fileSize);
				            jProgressBar.setMaximum(v.intValue());
				            
				            
				            int val=0;
				            do
				            {
				            	data=dataSocket.getDatadis().readUTF();
				                ch=Integer.parseInt(data);
				                if(ch!=-1)
				                {
				                    fout.write(ch);   
				                    val++;
				                    jProgressBar.setValue(val);
				                    jProgressBar.setString(options[0]+" downloading... "+val);
				                }
				             
				            }while(ch!=-1);
				            System.out.println("After do while");
				            fout.close();
				            serverMsg=dataSocket.getDatadis().readUTF();
				            System.out.println(serverMsg);
				            dataSocket.closeDataConnections();
	            	}else{
	            		//file present and dont overwrite
	            		dataSocket.getDatados().writeUTF("N");
	            	}
	            	fileCheckedOnServer=false;
	            }
	        }
		} catch (IOException e) {
			try {
				fout.close();
				file.delete();
				System.out.println("Exception occurred in RetrCommand:downLoadFile()");
				//e.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	
		}
        return null;
	}
	
	
}
