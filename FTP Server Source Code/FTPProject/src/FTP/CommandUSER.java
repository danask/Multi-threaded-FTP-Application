package FTP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class CommandUSER {
	DataInputStream dis_control;
	DataOutputStream dos_control;
	FileInputStream configFile;
	String username;
	String password;

	public CommandUSER(DataInputStream dis_control, DataOutputStream dos_control) {
		super();
		this.dis_control = dis_control;
		this.dos_control = dos_control;
	}

	public String issueUser() {
		try {
			Boolean valid = false;
			Properties properties = new Properties();
			configFile = new FileInputStream("config.properties");

			// load property
			properties.load(configFile);

			// get user name from client
			username = dis_control.readUTF();
			password = dis_control.readUTF();

			String passwordFromFile = (String) properties.get(username);
			if (passwordFromFile != null && passwordFromFile.equals(password)) {
				valid = true;
			}
			if (valid) {
				// check if the directory already exists
				if (new File(username).exists()) {
					// dos_control.writeUTF("success");
					dos_control.writeUTF(FTPReplyCodes.LOGGED_IN);
					System.out.println(username + " " + FTPReplyCodes.LOGGED_IN);
				}
				// else check if you are able to create a directory
				else {
					// create a directory for the user for the server
					Boolean created = new File(username).mkdir();
					// if unable to create return failed and return from the
					// method
					if (!created) {
						// dos_control.writeUTF("failed");
						dos_control.writeUTF(FTPReplyCodes.LOGGED_IN_FAILED);
						System.out.println(username + " " + FTPReplyCodes.LOGGED_IN_FAILED);
						return null;
					} else {
						// dos_control.writeUTF("success");
						dos_control.writeUTF(FTPReplyCodes.LOGGED_IN);
						System.out.println(username + " " + FTPReplyCodes.LOGGED_IN);
					}
				}
			}
			else{
				// login failed
				dos_control.writeUTF("failed");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return username;
	}
}
