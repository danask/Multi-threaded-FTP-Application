package FTP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;

public class CommandSTOR {
	DataInputStream dis_control;
	DataOutputStream dos_control;
	DataInputStream dis_data;
	DataOutputStream dos_data;
	String username;
	CommandPORT commandPORT = null;
	FileLock fileLock = null;
	String fileName = "";
	ReadWriteLock readWriteLock = null;
	// volatile to read directly from memory for faster access
	private volatile boolean flag = true;
	File uploadFile = null;
	FileOutputStream fileOutputStream = null;

	public CommandSTOR(DataInputStream dis_control, DataOutputStream dos_control, String username,
			CommandPORT commandPORT, ReadWriteLock readWriteLock) {
		super();
		this.dis_control = dis_control;
		this.dos_control = dos_control;
		this.username = username;
		this.commandPORT = commandPORT;
		this.readWriteLock = readWriteLock;
	}

	public void stopRunning() {
		System.out.println(Thread.currentThread().getName()+ " called to stop running");
		flag = false;
	}

	// to store files for the client on to the server
	public void store() {
		try {
			// listen for client data connection to the data port
			commandPORT.listenDataPort();
			dis_data = commandPORT.getDis_data();
			dos_data = commandPORT.getDos_data();
			// get file name from client
			fileName = dis_data.readUTF();
			System.out.println(Thread.currentThread().getName() + " waiting for write lock");
			readWriteLock.lockWrite(fileName);
			System.out.println("Got write lock for file " + fileName + " by " + username + " on "
					+ Thread.currentThread().getName());
			// used to get user option
			String option;
			// get the file handler
			uploadFile = new File(username + "/" + fileName);
			// check if the file exist
			if (uploadFile.exists()) {
				// TODO check if there are error codes to handle such scenarios
				// return file exists message when file found
				dos_data.writeUTF("File already exists");
				// waiting for users option of file replacement
				option = dis_data.readUTF();
			} else {
				option = "Y";
			}
			// client doesn't want to replace the file, hence get out of the
			// store method
			if (option.equalsIgnoreCase("N")) {
				dos_data.flush();
				return;
			}
			dos_data.writeUTF("Send file");

			fileOutputStream = new FileOutputStream(username + "/" + fileName);

			// read line by line from client and store it to the file
			System.out.print(username + " uploading " + fileName + " *");
			int i = 0;
			int data_char;
			String temp_ch;
			do {
				if (i % 500000 == 0) {
					System.out.print("*");
				}
				temp_ch = dis_data.readUTF();
				data_char = Integer.parseInt(temp_ch);
				if (data_char != -1) {
					fileOutputStream.write(data_char);
				}
				i++;
			} while (data_char != -1 && flag);
			if (flag) {
				System.out.println("*>");
				// dos_data.writeUTF("File uploaded successfully");
				dos_data.writeUTF(FTPReplyCodes.OP_COMPLETE);
				System.out.println(username + " uploaded " + FTPReplyCodes.OP_COMPLETE + " on "
						+ Thread.currentThread().getName());
				// closing the file output stream
				fileOutputStream.close();
			}else{
				System.out.println("Client disconnected!! checked via flag");
				// closing the file output stream
				fileOutputStream.close();
				deleteFileOnQuit();
			}
		}catch(EOFException e1){
			// closing the file output stream
			try {
				fileOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Checked via the EOFException");
			deleteFileOnQuit();
		}
		catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// close the data socket
			commandPORT.closeDataPort();
			// releasing the write lock in case of exceptions
			try {
				readWriteLock.unlockWrite(fileName);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Write lock released for file " + fileName + " by " + username + " on "
					+ Thread.currentThread().getName());
		}
	}
	
	private void deleteFileOnQuit(){
		// closing the file output stream
		System.out.println(uploadFile != null);
		if (uploadFile != null && uploadFile.exists()) {
			System.out.println("Partial file exists on the server deleting the file");
			if(uploadFile.delete())
				System.out.println(fileName+" file deleted as connection closed");
			}
		try {
			dos_data.writeUTF(FTPReplyCodes.CONNECTION_CLOSED);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Client disconnected..");
	}
}
