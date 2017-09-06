package packageIndexer;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Collections;
import java.util.HashMap;

public class IndexerImpl implements IPkgIndexer, Indexer {
	
	private static IndexerImpl indexer;
	/*
	 * using hash map to maintain the index so that insertion, deletion and removal
	 * of packages is o(1)
	 */
	private static HashMap<String, Pkg> registry;
	private static Map<String, Pkg> pkgRegistry;
	
	/**
	 * singleton pattern to ensure only object to this class is shared
	 * by all threads
	 * @return
	 */
	public static IndexerImpl getInstance(){
		if(indexer == null)
        {
        	indexer = new IndexerImpl();
        	registry = new HashMap<String, Pkg>();
        	// synchronized map to ensure thread safe map operations
    		pkgRegistry = Collections.synchronizedMap(registry); 
        }
        return indexer;
    }
	
	public Map<String, Pkg> getPkgRegistry() {
		return pkgRegistry;
	}

	/*
	 * loop through all dependencies of the current pkg. 
	 * If any of the dependencies are not indexed, return fail.
	 * If all dependencies are indexed or package has no dependencies, 
	 * add package to index and return ok.
	 */
	@Override
	public String index(Pkg update_pkg) {
		List<String> deps = update_pkg.getDependencies();
		try {
			for(String dep: deps)
			{
				if(!query(dep).equals(ok))
				{
					return fail;
				}
			}
		}
		catch(Exception e)
		{
			
		}
		pkgRegistry.put(update_pkg.getPkgname(), update_pkg);
		return ok;
	}
	
	/* 
	 * If the package to be removed does not exist in index or the package is 
	 * not  a dependency of any package, then remove it from index and return ok.
	 * Else return fail.
	 */
	@Override
	public String remove(String packageName) {
		String res = query(packageName);
		if(res.equals(fail))
		{
			return ok;
		}
		
		/*
		 * Synchronize the iterator over the synchronized map so that concurrent iteration 
		 * of the hashmap by multiple threads is avoided
		 */
		synchronized(pkgRegistry)
		{
			Set<Map.Entry<String,Pkg>> entrySet = pkgRegistry.entrySet();
		    for(Entry<String,Pkg> entry: entrySet) 
			{
				Pkg p = entry.getValue();
				if(p.getDependencies() != null)
				{
					for(String dep: p.getDependencies())
					{
						if(dep.equals(packageName))
						{
							return fail;
						}
					}
				}
			}
		    pkgRegistry.remove(packageName);
		}
		return ok;
	}

	/* 
	 * If the package exists in the index return ok else return fail
	 */
	@Override
	public String query(String packageName) {
		
		if(pkgRegistry.containsKey(packageName))
		{
			return ok;
		}
		return fail;
	}
}