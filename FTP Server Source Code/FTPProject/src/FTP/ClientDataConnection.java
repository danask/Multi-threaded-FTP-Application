package FTP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.util.concurrent.locks.Lock;

public class ClientDataConnection extends Thread {
	ServerSocket FTPServerDataSoc = null;
	DataInputStream dis_control;
	DataOutputStream dos_control;
	String command = "";
	String username = "";
	Lock readLock = null;
	ReadWriteLock readWriteLock = null;
	CommandPORT commandPORT = null;
	CommandSTOR commandSTOR = null;
	CommandRETR commandRETR = null;

	public CommandRETR getCommandRETR() {
		return commandRETR;
	}

	public void setCommandRETR(CommandRETR commandRETR) {
		this.commandRETR = commandRETR;
	}

	public CommandSTOR getCommandSTOR() {
		return commandSTOR;
	}

	public void setCommandSTOR(CommandSTOR commandSTOR) {
		this.commandSTOR = commandSTOR;
	}

	public ReadWriteLock getReadWriteLock() {
		return readWriteLock;
	}

	// Data connection channel will get the control channel details from control
	// connection part
	public ClientDataConnection(DataInputStream dis_control, DataOutputStream dos_control, String command,
			String username, CommandPORT commandPORT, ReadWriteLock readWriteLock) {
		super();
		this.dis_control = dis_control;
		this.dos_control = dos_control;
		this.command = command;
		this.username = username;
		this.commandPORT = commandPORT;
		this.readWriteLock = readWriteLock;
		start();
	}

	public void stopRunning() {
		System.out.println(Thread.currentThread().getName() + " called to be stopped by "+username);
		CommandRETR stopCommandRETR = getCommandRETR(); 
		CommandSTOR stopCommandSTOR = getCommandSTOR();
		if(stopCommandRETR != null){
			stopCommandRETR.stopRunning();
		}
		if (stopCommandSTOR != null){
			stopCommandSTOR.stopRunning();
		}
	}

	public void run() {
		switch (command) {

		case "RETR":
			setCommandRETR(new CommandRETR(dis_control, dos_control, username, this.commandPORT, getReadWriteLock()));
			getCommandRETR().retrieve();
			break;

		case "STOR":
			setCommandSTOR(new CommandSTOR(dis_control, dos_control, username, this.commandPORT, getReadWriteLock()));
			getCommandSTOR().store();
			break;

		default:
			System.out.println("Invalid data command issued");
		}
	}
}
