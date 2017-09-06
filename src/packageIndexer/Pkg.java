package packageIndexer;

import java.util.List;

public class Pkg {
	
	private String pkgname;
	private List<String> dependencies;
	
	public Pkg(String pkgname, List<String> dependencies) {
		this.pkgname = pkgname;
		this.dependencies = dependencies;
	}

	public String getPkgname() {
		return pkgname;
	}

	public void setPkgname(String pkgname) {
		this.pkgname = pkgname;
	}

	public List<String> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<String> dependencies) {
		this.dependencies = dependencies;
	}
}
