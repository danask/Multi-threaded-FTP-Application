package FTP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CommandPORT {
	ServerSocket fTServerSocket;
	DataInputStream dis_data;
	DataOutputStream dos_data;
	DataInputStream dis_control;
	DataOutputStream dos_control;
	String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public DataInputStream getDis_control() {
		return dis_control;
	}

	public void setDis_control(DataInputStream dis_control) {
		this.dis_control = dis_control;
	}

	public DataOutputStream getDos_control() {
		return dos_control;
	}

	public void setDos_control(DataOutputStream dos_control) {
		this.dos_control = dos_control;
	}

	public DataOutputStream getDos_data() {
		return dos_data;
	}

	public void setDos_data(DataOutputStream dos_data) {
		this.dos_data = dos_data;
	}

	public DataInputStream getDis_data() {
		return dis_data;
	}

	public void setDis_data(DataInputStream dis_data) {
		this.dis_data = dis_data;
	}

	public CommandPORT(DataInputStream dis_control, DataOutputStream dos_control, String username,
			ServerSocket fTServerSocket) {
		super();
		this.fTServerSocket = fTServerSocket;
		this.dis_control = dis_control;
		this.dos_control = dos_control;
		this.username = username;
		try {
			dos_control.writeUTF(FTPReplyCodes.OKAY);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(username + " PORT " + FTPReplyCodes.OKAY);
	}

	// Open data port to listen for client connection
	public void listenDataPort() {
		Socket clientDataSocket = null;
		try {
			System.out.println(username +" data port connected");
			clientDataSocket = fTServerSocket.accept();
			setDis_data(new DataInputStream(clientDataSocket.getInputStream()));
			setDos_data(new DataOutputStream(clientDataSocket.getOutputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void closeDataPort() {
		try {
			this.dos_data.close();
			this.dis_data.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
