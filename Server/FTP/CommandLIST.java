package FTP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class CommandLIST {
	DataInputStream dis_control;
	DataOutputStream dos_control;
	String username;

	public CommandLIST(DataInputStream dis_control, DataOutputStream dos_control, String username) {
		super();
		this.dis_control = dis_control;
		this.dos_control = dos_control;
		this.username = username;
	}

	public void issueList() {
		try {
			// string file name
			StringBuffer fileName = new StringBuffer();
			StringBuffer fileNamesToSend = new StringBuffer();
			// opening a file handler
			File file = new File(username);
			// getting array list of files
			ArrayList<String> names = new ArrayList<String>(Arrays.asList(file.list()));
			for (String name : names) {
				fileName.append(name);
				fileName.append(",");
			}
			int fileNameLength = fileName.length();
			if(fileNameLength > 0){
				fileNamesToSend.append(fileName.substring(0, (fileNameLength - 1)));
			}
			System.out.println("File name list: "+fileNamesToSend.toString());
			dos_control.writeUTF(fileNamesToSend.toString());
			dos_control.writeUTF(FTPReplyCodes.OP_COMPLETE);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
