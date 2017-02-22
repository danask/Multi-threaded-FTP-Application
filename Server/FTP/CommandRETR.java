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

	public CommandRETR(DataInputStream dis_control, DataOutputStream dos_control, String username,
			CommandPORT commandPORT, ReadWriteLock readWriteLock) {
		super();
		this.dis_control = dis_control;
		this.dos_control = dos_control;
		this.username = username;
		this.commandPORT = commandPORT;
		this.readWriteLock = readWriteLock;
	}

	public void retrieve() {
		try {
			System.out.println(Thread.currentThread().getName() + "Waiting for read lock");
			readWriteLock.lockRead();
			System.out.println("Got read lock for file " + fileName + " by " + username + " on "
					+ Thread.currentThread().getName());
			commandPORT.listenDataPort();
			// dos_control.writeUTF(FTPReplyCodes.OPENING_DATA);
			dos_data = commandPORT.getDos_data();
			dis_data = commandPORT.getDis_data();
			// get file name from the client
			fileName = dis_data.readUTF();
			// opening a file handler
			File file = new File(username + "/" + fileName);

			// check if the file exists
			if (!file.exists()) {
				dos_data.writeUTF("File not found on the server");
				dos_data.writeUTF("-1");
				return;
			} else {
				double fileSize = file.length();
				System.out.println("Filesize " + fileSize + " bytes");
				// to send the file size to the client
				dos_data.writeUTF("READY");
				dos_data.writeUTF(String.valueOf(fileSize));
				fis = new FileInputStream(username + "/" + fileName);
				// getting the client response for the file
				System.out.println("Getting option");
				String option = dis_data.readUTF();
				System.out.println("Option: " + option);

				if (option.equalsIgnoreCase("Y")) {
					// reading the character from file and writing it to output
					// stream to client

					// listen for client data connection to the data port
					System.out.println(username + " Downloading---" + fileName);
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
					} while (read_char != -1);
					System.out
							.println("read char ----> " + read_char + " on thread " + Thread.currentThread().getName());
					// -1 indicates the EOF
					System.out.println("->");
					System.out.println(
							username + " download finished " + fileName + " on " + Thread.currentThread().getName());
				}
				// dos_data.writeUTF("File uploaded successfully from server");
				dos_data.writeUTF(FTPReplyCodes.OP_COMPLETE);
				System.out.println(username + " download: " + FTPReplyCodes.OP_COMPLETE);
			}
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println("read interrupted");
			e.printStackTrace();
		} finally {
			// close the data socket
			commandPORT.closeDataPort();
			// releasing the read lock if not released earlier
			readWriteLock.unlockRead();
		}
	}
}
