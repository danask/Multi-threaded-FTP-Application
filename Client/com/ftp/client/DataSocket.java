package com.ftp.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class DataSocket {
	private Socket dataServerSocket=null;
	private DataInputStream datadis=null;
	private DataOutputStream datados=null;
	private String hostname=null;
	private Integer dataPort=null;
	
	public DataSocket(String hostname,Integer dataPort){
		this.hostname=hostname;
		this.dataPort=dataPort;
	}
	
	public DataInputStream getDatadis() {
		return datadis;
	}

	public void setDatadis(DataInputStream datadis) {
		this.datadis = datadis;
	}

	public DataOutputStream getDatados() {
		return datados;
	}

	public void setDatados(DataOutputStream datados) {
		this.datados = datados;
	}

	public boolean connectToDataPort(){
		boolean isConnect=false;
		try{
			dataServerSocket = new Socket(hostname, dataPort);
			datadis = new DataInputStream(dataServerSocket.getInputStream());
			datados = new DataOutputStream(dataServerSocket.getOutputStream());
			isConnect=true;
		} catch (UnknownHostException e) {
			isConnect=false;
			e.printStackTrace();
		} catch (IOException e) {
			try{
				dataServerSocket.close();
				isConnect=false;
				e.printStackTrace();
			}catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return isConnect;
	}
	
	public void closeDataConnections(){
		try {
			datadis.close();
			datados.close();
			dataServerSocket.close();
		} catch (IOException e) {
			//e.printStackTrace();
			System.out.println("Exception occurred in DataSocket:closeDataConnections()");
		}
	}
}
