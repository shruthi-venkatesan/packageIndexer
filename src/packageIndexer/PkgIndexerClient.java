package packageIndexer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class PkgIndexerClient {

	public static void main(String arg[]) throws IOException, ClassNotFoundException {

		int portNum = 8080;
		BufferedReader op = null;
		BufferedWriter ip = null;		
		@SuppressWarnings("resource")
		Socket clientSocket = new Socket("localhost", portNum);
		
		String s = "INDEX|cloog|\n";	
		op = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		ip = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
		ip.write(s);
		String response = (String) op.readLine();
	        System.out.println("Server message: " + response);
			
	        s = "QUERY|cloog|\n";	
	       
	}
}
