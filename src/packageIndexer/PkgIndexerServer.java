package packageIndexer;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class PkgIndexerServer extends Thread implements IPkgIndexer  {

	public static void main(String[] args) throws IOException, ClassNotFoundException {

		Logger logger = Logger.getLogger(PkgIndexerServer.class.getName() ); 
		logger.info("Server is now running at port: " + portNum);
		FileHandler fh;  

		try {  
			// This block configure the logger with handler and formatter  
			String path=Paths.get(".").toAbsolutePath().normalize().toString();
			String logFile=path+new Timestamp(System.currentTimeMillis()) + ".log";
			fh = new FileHandler(logFile);  
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();  
			fh.setFormatter(formatter);  
		} 
		catch (Exception e) {  
			e.printStackTrace();  
		}  

		IndexerImpl indexer = IndexerImpl.getInstance(); 
		ThreadPoolServer server = new ThreadPoolServer(portNum, logger, indexer);
		new Thread(server).start();
	}
}