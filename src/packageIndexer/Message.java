package packageIndexer;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class Message implements IPkgIndexer {

	String[] words; 
	List<String> deps; // maintain an array list of a package's dependencies
	
	public String[] getWords() {
		return words;
	}

	public void setWords(String[] words) {
		this.words = words;
	}

	public List<String> getDeps() {
		return deps;
	}

	public void setDeps(List<String> deps) {
		this.deps = deps;
	}

	public boolean isEncoded(String text){

	    Charset charset = Charset.forName("US-ASCII");
	    String checked=new String(text.getBytes(charset),charset);
	    return !checked.equals(text);

	}
	
	public String parseClientMsg(String s, Logger logger) 
	{
		// split client request by |
		words = s.trim().split("\\|");
		if(words == null || words.length > 3 || ! ( words[0].equals(indexPkg) || words[0].equals(removePkg) || words[0].equals(queryPkg)))
		{
			logger.severe(s + incorrectMsgError);
			return error;
		}
		
		if(words[0] == null || words[0].isEmpty())
		{
			logger.severe(missingCmdError);
			return error;
		}
		
		if(words[1] == null || words[1].isEmpty())
		{
			logger.severe(missingPkgError);
			return error;
		}
		
		// pkg name contains spaces or = or encoded
		if(words[1].contains(" ") || words[1].contains("=")|| isEncoded(words[1]))
		{	
			logger.info(words[1]+"first");
			return error;
		}
		
		// pkg name contains + concatenated by two strings
		if(!words[1].contains("-") && words[1].contains("+") && !words[1].endsWith("+") && !Character.isDigit(words[1].charAt(words[1].length()-1)))
		{
			return error;
		}
			
		if(words.length == 3 && !(words[2] == null || words[2].isEmpty()))
		{
			// split list of dependencies by comma
			deps = Arrays.asList(words[2].split(depDelimiter));
			if(deps == null || deps.isEmpty())
			{
				logger.severe(incorrectMsgError);
				return error;
			}
		}
		return ok;
	}
}