**DESCRIPTION**

Packages are executables or libraries that can be installed in a system, often via a package manager such as apt, RPM, or Homebrew. Many packages use libraries that are also made available as packages themselves, so usually a package will require you to install its dependencies before you can install it on your system.

The system keeps track of package dependencies. Clients will connect to the server and inform which packages should be indexed, and which dependencies they might have on other packages. We want to keep our index consistent, so the server must not index any package until all of its dependencies have been indexed first. The server should also not remove a package if any other packages depend on it.

**REQUIREMENTS**

The server will open a TCP socket on port 8080. It must accept connections from multiple clients at the same time, all trying to add and remove items to the index concurrently. Clients are independent of each other, and it is expected that they will send repeated or contradicting messages. New clients can connect and disconnect at any moment, and sometimes clients can behave badly and try to send broken messages.

Messages from clients follow this pattern:

```
<command>|<package>|<dependencies>\n
```

Where:
* `<command>` is mandatory, and is either `INDEX`, `REMOVE`, or `QUERY`
* `<package>` is mandatory, the name of the package referred to by the command, e.g. `mysql`, `openssl`, `pkg-config`, `postgresql`, etc.
* `<dependencies>` is optional, and if present it will be a comma-delimited list of packages that need to be present before `<package>` is installed. e.g. `cmake,sphinx-doc,xz`
* The message always ends with the character `\n`

Here are some sample messages:
```
INDEX|cloog|gmp,isl,pkg-config\n
INDEX|ceylon|\n
REMOVE|cloog|\n
QUERY|cloog|\n
```

For each message sent, the client will wait for a response code from the server. Possible response codes are `OK\n`, `FAIL\n`, or `ERROR\n`. After receiving the response code, the client can send more messages.

The response code returned should be as follows:
* For `INDEX` commands, the server returns `OK\n` if the package can be indexed. It returns `FAIL\n` if the package cannot be indexed because some of its dependencies aren't indexed yet and need to be installed first. If a package already exists, then its list of dependencies is updated to the one provided with the latest command.
* For `REMOVE` commands, the server returns `OK\n` if the package could be removed from the index. It returns `FAIL\n` if the package could not be removed from the index because some other indexed package depends on it. It returns `OK\n` if the package wasn't indexed.
* For `QUERY` commands, the server returns `OK\n` if the package is indexed. It returns `FAIL\n` if the package isn't indexed.
* If the server doesn't recognize the command or if there's any problem with the message sent by the client it should return `ERROR\n`.

**DESIGN**

One of the first design decisions is to persist the index for the lifetime of the server i.e the index is not stored on disk. 
The indexer is coded with the assumption that the client queries will not lead to a cyclic dependency between packages.

The server is launched from the PkgIndexerServer class. For scaling our server, rather than starting a new thread per incoming connection, the connection is wrapped in a Runnable and handed off to a thread poool with a fixed number of threads. When a thread in the thread pool is idle, it will take a Runnable from the queue and execute it. Instead of executing all the requests concurrently, we execute a fixed number of requests concurrently and queue the rest up thereby improving the performance. To exit the server, ctrl+c needs to be issued.

A Hashmap is used to maintain the index in memory with key as package name and value as the *Pkg* object. We use a Synchronized map in JAVA to ensure that the entire index is locked while doing concurrent operations. A *Pkg* object consists of package name and its dependencies. The dependencies are currently stored as list of strings to simplify the implementation. Hashmap is used to guarantee a O(1) insertion, deletion and access. However, during removal of a single pacakge, the entire map's values have to be traversed to ensure the current package is not a dependent of any other package making removal expensive. Query takes 0(1). While indexing, since we need to ensure all the dependencies of this package are already indexed it will take O(n) where n is the number of dependencies of the package to be indexed. 

**USAGE**
* After pulling from the repository, compile the java files together and run the server as
```
javac packageIndexer/*.java
java packageIndexer.PkgIndexerServer
```
* The server can be run in a docker container as follows:
```
sudo docker build -t pkgindexer .
sudo docker run -p 8080:8080 pkgindexer
```
**LOGGING**
```
Sep 06, 2017 6:02:08 PM packageIndexer.WorkerRunnable run
INFO: REQ:QUERY|git-fixup|RES:OK
Sep 06, 2017 6:02:08 PM packageIndexer.WorkerRunnable run
INFO: REQ:QUERY|httperf|RES:FAIL
Sep 06, 2017 6:02:08 PM packageIndexer.WorkerRunnable run
INFO: REQ:INDEX|jena|RES:OK
Sep 06, 2017 6:02:08 PM packageIndexer.WorkerRunnable run
INFO: REQ:REMOVE|git-fixup|RES:OK
```
