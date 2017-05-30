package FTP;

import java.util.HashMap;
import java.util.Map;

// code reference: http://tutorials.jenkov.com/java-concurrency/read-write-locks.html
// writers have higher priority version 2
public class ReadWriteLock {

	private Map<Thread, Integer> readingThreads = new HashMap<Thread, Integer>();
	private Map<String, String> lockControl = new HashMap<String, String>();

	private int writeAccesses = 0;
	private int writeRequests = 0;
	private Thread writingThread = null;
	String fileName = null;

	public synchronized void lockRead(String fileName) throws InterruptedException {
		Thread callingThread = Thread.currentThread();
		while (!canGrantReadAccess(callingThread, fileName)) {
			wait();
		}

		readingThreads.put(callingThread, (getReadAccessCount(callingThread) + 1));
	}

	private boolean canGrantReadAccess(Thread callingThread, String fileName) {
		if (fileNameCheckForRead(fileName))
			return true;
		if (isWriter(callingThread))
			return true;
		if (hasWriter())
			return false;
		if (isReader(callingThread))
			return true;
		if (hasWriteRequests())
			return false;
		return true;
	}

	public synchronized void unlockRead(String fileName) {
		Thread callingThread = Thread.currentThread();
		if (!isReader(callingThread)) {
			throw new IllegalMonitorStateException(
					"Calling Thread does not" + " hold a read lock on this ReadWriteLock");
		}
		int accessCount = getReadAccessCount(callingThread);
		if (accessCount == 1) {
			readingThreads.remove(callingThread);
			// when it is the last reader remove the entry
			fileNameRemove(fileName);
		} else {
			readingThreads.put(callingThread, (accessCount - 1));
		}
		notifyAll();
	}

	public synchronized void lockWrite(String fileName) throws InterruptedException {
		writeRequests++;
		Thread callingThread = Thread.currentThread();
		while (!canGrantWriteAccess(callingThread, fileName)) {
			wait();
		}
		writeRequests--;
		writeAccesses++;
		writingThread = callingThread;
	}

	public synchronized void unlockWrite(String fileName) throws InterruptedException {
		/*if (!isWriter(Thread.currentThread())) {
			throw new IllegalMonitorStateException(
					"Calling Thread does not" + " hold the write lock on this ReadWriteLock");
		}*/
		writeAccesses--;
		// remove the entry of the file name which has finished upload
		fileNameRemove(fileName);
		if (writeAccesses == 0) {
			writingThread = null;
		}
		notifyAll();
	}

	private boolean canGrantWriteAccess(Thread callingThread, String fileName) {
		if (fileNameCheckForWrite(fileName))
			return true;
		if (isOnlyReader(callingThread))
			return true;
		if (hasReaders())
			return false;
		if (writingThread == null)
			return true;
		if (!isWriter(callingThread))
			return false;
		return true;
	}

	private int getReadAccessCount(Thread callingThread) {
		Integer accessCount = readingThreads.get(callingThread);
		if (accessCount == null)
			return 0;
		return accessCount.intValue();
	}

	private boolean hasReaders() {
		return readingThreads.size() > 0;
	}

	private boolean isReader(Thread callingThread) {
		return readingThreads.get(callingThread) != null;
	}

	private boolean isOnlyReader(Thread callingThread) {
		return readingThreads.size() == 1 && readingThreads.get(callingThread) != null;
	}

	private boolean hasWriter() {
		return writingThread != null;
	}

	private boolean isWriter(Thread callingThread) {
		return writingThread == callingThread;
	}

	private boolean hasWriteRequests() {
		return this.writeRequests > 0;
	}

	private boolean fileNameCheckForRead(String fileName) {
		String localFileName = fileName;
		// reassigning the file name to null
		this.fileName = null;
		// checks if the file name is already in use
		while (lockControl.containsKey(localFileName)) {
			// if file name already in use for upload wait for it to complete
			if ((lockControl.get(localFileName)).equals("upload")) {
				// wait until notified
				return false;
			}else{
				return true;
			}
		}
		if (!lockControl.containsKey(localFileName)) {
			// making entry to the lock control
			lockControl.put(localFileName, "download");
			return true;
		}
		// if it is not a upload operation, allow multiple downloads
		return true;
	}

	private boolean fileNameCheckForWrite(String fileName) {
		String localFileName = fileName;
		// reassigning the file name to null
		this.fileName = null;
		// checks if the file name is already in use
		while (lockControl.containsKey(localFileName)) {
			// this condition someone is writing wait for the write to finish
			return false;
		}
		if (!lockControl.containsKey(localFileName)) {
			// making entry to the lock control
			lockControl.put(localFileName, "upload");
			return true;
		}
		// if it is not a upload operation, allow multiple downloads
		return true;
	}

	private void fileNameRemove(String fileName) {
		String localFileName = fileName;
		// reassigning the file name to null
		this.fileName = null;
		// remove the entry of that file name after read operation
		if (lockControl.containsKey(localFileName)) {
			lockControl.remove(localFileName);
		}
	}
}
