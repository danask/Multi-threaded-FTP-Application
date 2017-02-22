package com.ftp.client;

import javax.swing.JProgressBar;

public class DownloadProgressBar {
	
	public int maxBarSize=0;
	public JProgressBar downLoadBar=null;
	public int incrBar=1;
	public int noOfThreads=0;
	
	public DownloadProgressBar(JProgressBar downLoadBar){
		this.downLoadBar=downLoadBar;
	}
	
	public synchronized void updateMaxBarSize(double value)
	{
		Double v=new Double(value);
		maxBarSize=maxBarSize+v.intValue();
		downLoadBar.setMaximum(maxBarSize);
		System.out.println("Max size" +maxBarSize);
	}
	
	public synchronized void incrProgressBar(int value){
		//incrBar=incrBar+value;
		downLoadBar.setValue(incrBar++);
	}
	
	public synchronized JProgressBar getProgressBar(){
		return downLoadBar;
	}
	
	public synchronized void  countNoThreads(){
		noOfThreads++;
	}

	
}
