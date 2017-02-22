package FTP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.util.Random;

public class CommandPASV {
	ServerSocket fTPServerDataSoc = null;
	private static final int LOW_PORT_NUMBER = 1024;
	private static final int HIGH_PORT_NUMBER = 65535;
	DataInputStream dis_control;
	DataOutputStream dos_control;

	public CommandPASV(DataInputStream dis_control, DataOutputStream dos_control) {
		super();
		this.dis_control = dis_control;
		this.dos_control = dos_control;
	}

	public ServerSocket issuePASV() {
		System.out.println("Passive command issued!");
		// opening a data port at server for client to connect to it
		try {
			int serverDataPort = getRandomNumberInRange(LOW_PORT_NUMBER, HIGH_PORT_NUMBER);
			fTPServerDataSoc = new ServerSocket(serverDataPort);
			// TODO if able to create data port reply by 227 then give the port
			// number
			dos_control.writeUTF(String.valueOf(serverDataPort));
			// TODO If unable to open data port reply by 425
		} catch (BindException e) {
			try {
				dos_control.writeUTF("");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fTPServerDataSoc;
	}

	// Random port generator for data connection
	private static int getRandomNumberInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
}