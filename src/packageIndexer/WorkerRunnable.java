package packageIndexer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkerRunnable implements Runnable, IPkgIndexer { 
	private Socket clientSocket; // Socket connect to client 
	private Logger logger; // Server logger 
	private IndexerImpl indexer;

	public WorkerRunnable(Socket clientSocket, Logger logger, IndexerImpl indexer) { 
		this.clientSocket = clientSocket; 
		this.logger = logger; 
		this.indexer = indexer;
	} 

	public void run(){ 

		BufferedReader userInput = null;
		DataOutputStream userOutput = null;
		String s = null;
		try { 
			while(true)
			{
				// Creating input and output streams
				userInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				userOutput = new DataOutputStream(clientSocket.getOutputStream());

				// Reading a String from input stream.
				s = userInput.readLine();
				if(s == null)
					break;
				
				// Parse the client's message 
				Message m = new Message();
				String response = m.parseClientMsg(s, logger);
				if(response.equals(ok))
				{
					if(m.words[0].equals(indexPkg))
					{
						Pkg p = new Pkg(m.words[1], m.deps);
						response = indexer.index(p);
					}
					else if(m.words[0].equals(removePkg))
					{
						response = indexer.remove(m.words[1]);
					}
					else if(m.words[0].equals(queryPkg))
					{
						response = indexer.query(m.words[1]);
					}
					logger.info("REQ:" + s + "RES:" + response);
				}
				userOutput.writeBytes(response+'\n');
			}
		} 
		catch (Exception ex)
		{ 
			logger.log(Level.SEVERE, "Exception in pkgIndexerServerWorker", ex);
		} 
		finally 
		{ 

		}
	} 
}