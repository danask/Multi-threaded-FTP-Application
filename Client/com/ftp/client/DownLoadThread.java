package com.ftp.client;

import java.awt.Color;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import com.ftp.client.ui.UIConstants;

public class DownLoadThread extends Thread {

	private JLabel messageText;
	private int listIndex=0;
	private JFrame jFrame=null;
	private String filename=null;
	private JProgressBar jProgressBar=null;
	
	public DownLoadThread( JLabel messageText,JFrame jFrame,String filename) {
		this.messageText = messageText;
		this.jFrame=jFrame;
		this.filename=filename;
	}
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
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
		jProgressBar.setForeground(Color.BLUE);
		jProgressBar.setStringPainted(true);
		jProgressBar.setString(filename+" downloading... ");
		
		FTPClient ftpClient=new FTPClient();
		ftpClient.setDownloadjProgressBar(jProgressBar);
		
		
		//messageText.setText("Downloading file...");
		int index=listIndex;
		String response = ftpClient.clientCommand("RETR", new String[]{filename});
		String fileSize=ftpClient.getFileSize();
		System.out.println("File Size "+fileSize);
		//messageText.setText("File downloaded successfully");
		if (response == "File not found on the server"){
			messageText.setText(response);
		} else{
			if (response == "File Already Exists.Want to OverWrite (Y/N) ?"){
				int confirmReply = JOptionPane.showConfirmDialog(null, 
						UIConstants.UI_FILEEXIT_CLIENT,
						UIConstants.UI_FILETITLE, 
						JOptionPane.YES_NO_OPTION);
				if (confirmReply == JOptionPane.YES_OPTION){
					ftpClient.clientCommand("RETR", new String[]{filename,"Y"});
					//messageText.setText("File downloaded successfully");
				}else{
					ftpClient.clientCommand("RETR", new String[]{filename,"N"});
					//messageText.setText("File override/replace cancelled");
				}
			}
		}
		
		jProgressBar.setString(this.filename+" downloaded ");
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
