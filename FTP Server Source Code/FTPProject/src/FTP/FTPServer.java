package FTP;

import java.io.IOException;
import java.net.ServerSocket;

public class FTPServer {
	public static void main(String args[]) {
		// Server socket variable
		ServerSocket FTPServerControlSoc = null;
		// Read write lock
		final FTP.ReadWriteLock readWriteLock = new FTP.ReadWriteLock();
		try {
			// Making server control socket on port 21
			FTPServerControlSoc = new ServerSocket(21);
			System.out.println("FTP server started on port 21");
			while (true) {
				System.out.println("Waiting for new connection:- ");
				// waiting for client to connect to the server socket and
				new ClientControlConnection(FTPServerControlSoc.accept(), readWriteLock);
			}
		} catch (IOException ioexception) {
			System.err.println("IO Exception occured : " + ioexception.getMessage());
		} finally {
			try {
				// closing the server socket
				System.out.println("Server socket closed!");
				FTPServerControlSoc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}