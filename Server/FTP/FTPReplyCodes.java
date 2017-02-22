package FTP;

public final class FTPReplyCodes {
	// Positive reply 1xx
	public static final String DATA_ALREADY_OPEN = "125 Data connection already open; starting transfer";
	public static final String OPENING_DATA = "150 File status okay; about to open data connection";
	
	// Success codes 2xx
	public static final String OKAY = "200 Command successfully executed";
	public static final String NOT_IMPLEMENTED = "202 Command not implemented; superfluous at this site";
	public static final String DATA_OPENED = "225 Data connection open; no transfer in progress";
	public static final String PASSIVE_MODE = "227 Entering Passive Mode";
	public static final String OP_COMPLETE = "226 Operation completed";
	public static final String LOGGED_IN = "230 User logged in; proceed";
	public static final String FILE_OP_COMPLETED = "250 Requested file operation okay; completed";
	public static final String READY_FOR_NEW_USER = "220 Welcome to TEAM 3 FTP service";
	
	// provisional negative responses 4xx
	public static final String UNABLE_TO_OPEN_DATA = "425 Can not open data connection";
	public static final String CONNECTION_CLOSED = "426 Connection closed, transfer aborted";
	public static final String FILE_UNAVAILABLE_TEMP = "450 Requested file operation not performed. File unavailable";
	
	// permanent negative response 5xx
	public static final String SYNTAX_ERROR = "500 Syntax error, command not recognized";
	public static final String SYNTAX_ERROR_PARAM = "501 Syntax error in parameters or arguments";
	public static final String COMMAND_NOT_IMPLEMENTED = "502 Command not implemented";
	public static final String FILE_UNAVAILABLE = "553 Requested file operation not performed. Invalid file name";
	public static final String LOGGED_IN_FAILED = "530 Username/Password invalid";
}
