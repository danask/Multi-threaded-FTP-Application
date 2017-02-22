# Multi-threaded-FTP-Application

Environment Requirements: -
-	jdk 1.8 or above
-	eclipse preferred as IDE
To run on amazon server
-	amazon aws account
-	putty
-	winscp

Running the program: -
On local environment:
-	using eclipse, load the eclipse project file using import existing project option in Eclipse
-	After the project has been loaded, run the program both the server and the client, using Eclipse's run program as 'a java application'
-	Program to run
	- Server:	FTPServer.java
	- Client:	FTPUserInterface.java
-	Since it is local environment, 
	-	host in the client should be 'localhost'
	-	username and password allowed for the application as of now
		-username - a, b, c
		-password - a, b, c
	-	Click on the connect button once all the credentials and details are entered as mentioned above.
	-	On hitting the connect button, users would be able to see all the files in their own directory.
-	To test in the amazon aws environment,
	-	upload the files in the server folder, i.e copy the FTP folder on to the server using WinSCP after entering the credentials.
	-	ensure that java is installed in the virtual environment.
	-	now connect to the aws environment using putty, with your credentials for the aws account.
	-	after connection has been established with aws server using putty, run the following commands.
		- 	cd FTP
		- 	javac -classpath .. FTPServer.java
		- 	cd ..
		-	sudo java FTP.FTPServer
	-	After running the above command, you should be able to see a message saying "Server is running. Waiting for clients!"
	-	Now run the client 'FTPUserInterface' program locally and enter the aws server public dns as host.
	-	On hitting the connect button, users would be able to see all the files in their own directory.

FTP Command list implemented: -
RETR
STOR
USER
PASV
PORT
NOOP
Extra command implemented
LIST

FTP command execution: -
-	You don't need to execute the commands individually, as the client handles all the FTP commands in the background.
-	For example, 
		-	on clicking connect, first a passive command ('PASV') is issued to the server from the client, followed by 'USER' and 'LIST' commands.
		-	on clicking download, a STOR request along with file details is sent to the server.
		
Sample Scenarion to test: -
-	Client uploads a file to server.
-	Client downloads a file from the server.
-	Client uploads a file to the server and at the same time downloads a file from the server. [Multi-threading]
-	Client downloads multiple files from the server. [Multi-threading]
-	Client uploads multiple files to the server. [Multi-threading]
-	Client downloads a file from the server and uploads a file to the server.[Multi-threading]
-	Client uploads a file to the server and downloads the same file from the server. [Multi-threading + Locking]
-	Client downloads a file from the server and again downloads the same file from the server. [Multi-threading + Locking]
-	Client downloads a file from the server and uploads the same file to the server. [Multi-threading + Locking]
-	Client uploads a file to the server and downloads the same file from the server twice. [Multi-threading + Locking]

High Level Design.
Design decision
At Server side
o It should allow multiple clients to connect to it.
o It should allow concurrent upload and download for each client.
o It should allow simultaneous upload and download of different files from each client.
o It should allow simultaneous upload and download of different files for many clients connected to the server.
o It should allow multiple download of the same files from any number of clients connected to it.
o It should lock the file which is being downloaded by any clients.
o It should lock the file which is being uploaded by any client, so that other clients may not read any inconsistent data.
At Client side
o It should allow multiple downloads to be initiated.
o It should allow multiple uploads to be initiated.
o It should show multiple progress bar to help users be informed about the current status of the file in action.
At both the ends
o On connection being aborted or disconnected, close all the data threads and control threads which had been opened.
o Handle all the failure scenarios with graceful termination of the services.
o On failure scenarios, the files should be deleted.
 When a new client connects to the server a new control thread is created on the server.
 For STOR and RETR commands, new data thread is spawned of the control thread.
 To have communication between the control thread and the data thread, control thread keeps track of the all the data threads spawned using hash-map.
 For locking,
o Shared lock for downloads.
o Exclusive lock for upload of the same file.

Implementation Details:-
Used multi-threading concepts of Java and Locking mechanism of writers have higher priority version 2.

Future Implementation.
Implementation of thread pools to avoid creation and deletion of threads frequently, as it is one of the costliest operations.