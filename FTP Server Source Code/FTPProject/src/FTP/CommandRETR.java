package FTP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class CommandRETR {
	DataInputStream dis_control;
	DataOutputStream dos_control;
	DataInputStream dis_data;
	DataOutputStream dos_data;
	String username;
	CommandPORT commandPORT = null;
	FileChannel fileChannel = null;
	FileInputStream fis = null;
	String fileName = "";
	ReadWriteLock readWriteLock = null;
	// volatile to read directly from memory for faster access
	private volatile boolean flag = true;

	public CommandRETR(DataInputStream dis_control, DataOutputStream dos_control, String username,
			CommandPORT commandPORT, ReadWriteLock readWriteLock) {
		super();
		this.dis_control = dis_control;
		this.dos_control = dos_control;
		this.username = username;
		this.commandPORT = commandPORT;
		this.readWriteLock = readWriteLock;
	}

	public void stopRunning() {
		System.out.println(Thread.currentThread().getName() + " called to stop running");
		flag = false;
	}

	public void retrieve() {
		try {
			commandPORT.listenDataPort();
			// dos_control.writeUTF(FTPReplyCodes.OPENING_DATA);
			dos_data = commandPORT.getDos_data();
			dis_data = commandPORT.getDis_data();
			// get file name from the client
			fileName = dis_data.readUTF();
			System.out.println(Thread.currentThread().getName() + "Waiting for read lock");
			readWriteLock.lockRead(fileName);
			System.out.println("Got read lock for file " + fileName + " by " + username + " on "
					+ Thread.currentThread().getName());
			// opening a file handler
			File downloadFile = new File(username + "/" + fileName);

			// check if the file exists
			if (!downloadFile.exists()) {
				dos_data.writeUTF("File not found on the server");
				dos_data.writeUTF("-1");
				return;
			} else {
				double fileSize = downloadFile.length();
				// to send the file size to the client
				dos_data.writeUTF("READY");
				dos_data.writeUTF(String.valueOf(fileSize));
				fis = new FileInputStream(username + "/" + fileName);
				// getting the client response for the file
				String option = dis_data.readUTF();

				if (option.equalsIgnoreCase("Y")) {
					// reading the character from file and writing it to output
					// stream to client

					// listen for client data connection to the data port
					System.out.print(username + " downloading " + fileName + " *");
					int read_char;
					int i = 0;
					do {
						if (i % 500000 == 0) {
							System.out.print("-");
						}
						read_char = fis.read();
						// convert it to string value
						dos_data.writeUTF(String.valueOf(read_char));
						i++;
					} while (read_char != -1 && flag);
				}
				if (flag) {
					System.out.println("->");
					dos_data.writeUTF(FTPReplyCodes.OP_COMPLETE);
					System.out.println(username + " download: " + FTPReplyCodes.OP_COMPLETE + " on thread "
							+ Thread.currentThread().getName());
				} else {
					dos_data.writeUTF(FTPReplyCodes.CONNECTION_CLOSED);
					System.out.println("Client disconnected..");
				}
			}
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println("read interrupted");
			e.printStackTrace();
		} finally {
			// close the data socket
			commandPORT.closeDataPort();
			// releasing the read lock if not released earlier
			readWriteLock.unlockRead(fileName);
			System.out.println("Released read lock for file " + fileName + " by " + username + " on "
					+ Thread.currentThread().getName());
		}
	}
}
