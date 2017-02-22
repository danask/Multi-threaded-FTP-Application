package com.ftp.client.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import com.ftp.client.DownLoadThread;
import com.ftp.client.DownloadProgressBar;
import com.ftp.client.FTPClient;

public class DownloadActionListener implements ActionListener{

	private JList list;
	private JLabel messageText;
	private List<String>fileNames=null;
	private int listIndex=0;
	private DownloadProgressBar progressBar=null;
	private JProgressBar jProgressBar=null;
	private JFrame jFrame=null;
	
	public DownloadActionListener(JList list, JLabel messageText,JFrame jFrame) {
		this.list = list;
		this.messageText = messageText;
		//this.jProgressBar=jProgressBar;
		//progressBar=new DownloadProgressBar(jProgressBar);
		this.jFrame=jFrame;
	}
	
	public DownloadProgressBar getProgressBar() {
		return progressBar;
	}

	public void setProgressBar(DownloadProgressBar progressBar) {
		this.progressBar = progressBar;
	}


	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent e) {
		
		fileNames=list.getSelectedValuesList();
		for(int i=0;i<fileNames.size();i++){
			DownLoadThread downLoadThread=new DownLoadThread(messageText, jFrame, fileNames.get(i));
			FTPClient.threadsMap.put(downLoadThread, null);
			downLoadThread.start();
			try {
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
		}
	  }
}
