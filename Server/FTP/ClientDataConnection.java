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
	CommandPORT commandPORT = null;
	Lock readLock = null;
	ReadWriteLock readWriteLock = null;

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

	public void run() {
		switch (command) {

		case "RETR":
			System.out.println(this.username + " download thread created");
			CommandRETR commandRETR = new CommandRETR(dis_control, dos_control, username, this.commandPORT,
					getReadWriteLock());
			commandRETR.retrieve();
			break;

		case "STOR":
			System.out.println(this.username + " upload thread created");
			CommandSTOR commandSTOR = new CommandSTOR(dis_control, dos_control, username, this.commandPORT,
					getReadWriteLock());
			commandSTOR.store();
			break;

		default:
			System.out.println("Invalid data command issued");
		}
	}
}
