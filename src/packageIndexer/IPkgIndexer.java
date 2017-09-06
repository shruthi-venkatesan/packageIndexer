package packageIndexer;

public interface IPkgIndexer {

	// hard-coded value of port number on which server listens  
	int portNum = 8080;
	
	// error codes from server
	String ok = "OK";
	String error = "ERROR";
	String fail = "FAIL";
	
	// valid commands
	String indexPkg = "INDEX";
	String removePkg = "REMOVE";
	String queryPkg = "QUERY";
	
	// message processing
	String suffix = "\n";
	String depDelimiter = ",";
	
	// error description 
	String missingCmdError = "Command is missing";
	String missingPkgError = "Package name is missing";
	String incorrectMsgError = "Client message format is incorrect";

}
