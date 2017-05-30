package FTP;

import java.util.HashMap;

public class ReadWriteLockOnFileName {
	// lock control
	HashMap<String, String> lockControl = null;
	// file name
	String fileName = null;
	// reader count
	int readerCount = 0;

	public synchronized void readLock(String fileName) throws InterruptedException {
		this.fileName = fileName;
		// checks if the file name is already in use
		while (lockControl.containsKey(fileName)) {
			// if file name already in use for upload wait for it to complete
			if ((lockControl.get(fileName)).equals("upload")) {
				// wait until notified
				wait();
			}else{
				readerCount++;
			}
			// if it is not a upload operation, allow multiple downloads
		}
		if (!lockControl.containsKey(fileName)) {
			// increment the reader count
			readerCount++;
			// making entry to the lock control
			lockControl.put(fileName, "download");
		}
	}
	
	public synchronized void readUnlock(String fileName) throws InterruptedException{
		readerCount--;
		if(readerCount == 0){
			
		}
	}

}
