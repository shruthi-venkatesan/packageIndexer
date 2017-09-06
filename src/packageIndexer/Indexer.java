package packageIndexer;

public interface Indexer {
	String index(Pkg p); // index a package object
	String remove(String packageName); // remove the package by name
	String query(String packageName); // query the package by name
}
