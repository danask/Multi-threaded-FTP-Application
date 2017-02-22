package com.ftp.client;

public enum FTPCommand {
	USER("USER"), QUIT("QUIT"), PORT("PORT"), TYPE("TYPE"), MODE("MODE"), STRU("STRU"), RETR("RETR"), 
	STOR("STOR"), NOOP("NOOP") ,PASV("PASV"),LIST("LIST");
	
	private String value;
	
	FTPCommand(String value){
		this.value=value;
	}

	public String getValue() {
		return value;
	}

}
