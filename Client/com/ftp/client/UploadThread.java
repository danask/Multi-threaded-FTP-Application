package com.ftp.client;

import java.awt.Color;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import com.ftp.client.ui.UIConstants;

public class UploadThread extends Thread {
	
	private JFrame jFrame=null;
	private File file=null;
	private DefaultListModel<String> listValues =null;
	private JLabel messageText=null;
	private JProgressBar jProgressBar=null;
	
	public UploadThread(JFrame jFrame,File file,DefaultListModel<String>listValues,JLabel messageText) {
		this.jFrame=jFrame;
		this.file=file;
		this.listValues=listValues;
		this.messageText=messageText;
	}
	
	public JProgressBar getjProgressBar() {
		return jProgressBar;
	}

	public void setjProgressBar(JProgressBar jProgressBar) {
		this.jProgressBar = jProgressBar;
	}

	@Override
	public void run() {
		
		jProgressBar = new JProgressBar();
		jFrame.add(jProgressBar);
		jProgressBar.setVisible(true);
		jProgressBar.setBounds(ProgressBarPosition.X_AXIS,ProgressBarPosition.getY_AXIS(),450,30);
		jProgressBar.setForeground(Color.RED);
		jProgressBar.setStringPainted(true);
		jProgressBar.setString(file.getName()+" uploading... ");
		
		FTPClient client = new FTPClient();
		client.setjProgressBar(jProgressBar);
		
		String response = client.clientCommand("STOR", new String[]{ file.getAbsolutePath() });
		System.out.println("File Path: " + file.getAbsolutePath());
		if (response == StringConstants.FILE_ON_SERVER_EXISTS){
			int confirmReply = JOptionPane.showConfirmDialog(null, 
					UIConstants.UI_FILETITLE, 
					UIConstants.UI_FILEEXIT,
					JOptionPane.YES_NO_OPTION);
			
			if (confirmReply ==  JOptionPane.YES_OPTION){
				client.clientCommand(FTPCommand.STOR.getValue(), new String[]{ file.getAbsolutePath(), "Y" });
				//messageText.setText("Uploaded successfully");
			}else{
				client.clientCommand(FTPCommand.STOR.getValue(), new String[]{ file.getAbsolutePath(), "N" });
				//messageText.setText("File override cancelled.");
				System.out.println("Clicked no");
			}
		}else if(response==StringConstants.FILE_ON_CLIENT_NOTEXISTS){
			messageText.setText("File not present on machine.");
		}else{
			//messageText.setText("Uploaded successfully");
			listValues.removeAllElements();
			String list=client.clientCommand(FTPCommand.LIST.getValue(), null);
			for(String str:list.split(",")){
				listValues.addElement(str);
			}
		}
		jProgressBar.setString(file.getName()+" uploaded ");
		try {
			Thread.currentThread().sleep(500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		jProgressBar.setVisible(false);
		ProgressBarPosition.decrementYAxis();
		
		FTPClient.threadsMap.remove(Thread.currentThread());
	}
}
