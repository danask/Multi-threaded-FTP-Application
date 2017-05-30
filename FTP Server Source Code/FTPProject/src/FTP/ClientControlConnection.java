package FTP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

class ClientControlConnection extends Thread {
	Socket clientSocket;
	DataInputStream dis_control;
	DataOutputStream dos_control;
	DataInputStream dis_data;
	DataOutputStream dos_data;
	ServerSocket fTPServerDataSoc = null;
	CommandPORT commandPORT = null;
	String username;
	ReadWriteLock readWriteLock = null;

	public ReadWriteLock getReadWriteLock() {
		return readWriteLock;
	}

	// Create a hash map
	HashMap<Integer, ClientDataConnection> dataThreadMap = new HashMap<Integer, ClientDataConnection>();

	ClientControlConnection(Socket clientSocket, ReadWriteLock readWriteLock) {
		this.clientSocket = clientSocket;
		this.readWriteLock = readWriteLock;
		try {
			// getting client input and output stream
			// Control thread - one for every client
			dis_control = new DataInputStream(clientSocket.getInputStream());
			dos_control = new DataOutputStream(clientSocket.getOutputStream());
			System.out.println("Welcome to Team 3 FTP service");
			dos_control.writeUTF(FTPReplyCodes.READY_FOR_NEW_USER);
			System.out.println(FTPReplyCodes.READY_FOR_NEW_USER);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Client Connected..." + clientSocket.getInetAddress());
		// create a new thread for the connected client
		start();
	}

	private void quit() {
		try {
			// closing the control socket
			if (clientSocket != null)
				System.out.println("Closing control socket for thread: "+Thread.currentThread());
				clientSocket.close();
			// closing the data socket
			if (fTPServerDataSoc != null)
				System.out.println("Closing data socket for thread: "+Thread.currentThread());
				fTPServerDataSoc.close();
			// looping the through the data threads to close the respective sockets
			Set set = dataThreadMap.entrySet();
			// Get an iterator
			Iterator i = set.iterator();

			// Stop operation
			while (i.hasNext()) {
				Map.Entry me = (Map.Entry) i.next();
				ClientDataConnection clientDataConnection = (ClientDataConnection) me.getValue();
				clientDataConnection.stopRunning();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		String command = "";
		System.out.println("Waiting for control command from client: ");
		int dataCallCount = 0;
		disconnect: while (true) {
			try {
				command = dis_control.readUTF();
				ClientDataConnection clientDataConnection;
				System.out.println("Command issued: " + command + " on thread "+Thread.currentThread().getName());
				String toUpperCommand = command.toUpperCase();
				// since PORT contains IP address
				if (toUpperCommand.contains("PORT")) {
					toUpperCommand = "PORT";
				}
				switch (toUpperCommand) {
				// To handle PASV command issued from client
				case "PASV":
					CommandPASV commandPASV = new CommandPASV(dis_control, dos_control);
					this.fTPServerDataSoc = commandPASV.issuePASV();
					break;

				// TODO if client does not disconnect then initiate a auto
				// disconnect after certain time
				case "QUIT":
					quit();
					break disconnect;

				// to retrieve files for the client
				case "RETR":
					dataCallCount += 1;
					clientDataConnection = new ClientDataConnection(dis_control, dos_control, "RETR", username,
							this.commandPORT, getReadWriteLock());
					dataThreadMap.put(dataCallCount, clientDataConnection);
					break;

				// to store files for the client
				case "STOR":
					dataCallCount += 1;
					clientDataConnection = new ClientDataConnection(dis_control, dos_control, "STOR", username,
							this.commandPORT, getReadWriteLock());
					dataThreadMap.put(dataCallCount, clientDataConnection);
					break;

				// to get user name from client
				case "USER":
					CommandUSER commandUSER = new CommandUSER(dis_control, dos_control);
					this.username = commandUSER.issueUser();
					break;

				// to get list command from client and return file details
				case "LIST":
					CommandLIST commandLIST = new CommandLIST(dis_control, dos_control, username);
					commandLIST.issueList();
					break;

				// to open a data port for the client to listen to on passive
				// mode
				case "PORT":
					this.commandPORT = new CommandPORT(dis_control, dos_control, username, fTPServerDataSoc);
					break;

				// to send a OK response
				case "NOOP":
					this.dos_control.writeUTF(FTPReplyCodes.OKAY);
					break;

				default:
					System.out.println("Invalid Command issued");
					// TODO set error code 500
					dos_control.writeUTF("Invalid Command issued");
				}

			} catch (EOFException e) {
				// do nothing
			} catch (SocketException socketException) {
				break;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
}