package com.ftp.client;

public class ProgressBarPosition {
	
	public static int X_AXIS=250;
	public static int Y_AXIS=200;
	
	public synchronized static int  getY_AXIS(){
		Y_AXIS=Y_AXIS+50;
		return Y_AXIS;
	}
	
	public synchronized static void decrementYAxis(){
		Y_AXIS=Y_AXIS-50;
	}

}
