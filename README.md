# MasterThesis

## Title: Design and Implementation of a Scalable, Distributed, Location-Based Pub/Sub System

This project/research is based on the [Geobroker project](https://github.com/MoeweX/geobroker) from Jonathan Hasenburg.

## Instructions

This is a Maven, Java, Akka project. 

As this project contains multiple git submodules, one needs to run the following after cloning:
```
git submodule init
git submodule update
```

In order to build the project please run the following commands
```
mvn clean install
```
Or individually for each project module
```
mvn -pl ${Commons} clean install
```
The following Maven command runs a single JVM with 1 Akka actor system on port 2551. Each actor system creates an actor for the HTTP API by default on port 8080.

```
mvn exec:java -Dexec.mainClass=Main -pl Server
```

To run on specific ports use the following -D option for passing in command line arguements(You need to pass one parameter for the actor system port and one paraemeter for the HTTP API port).
```
mvn exec:java -Dexec.mainClass=Main -Dexec.args="2552 8081" -pl Cluster
```

A common way to run tests is to start single JVMs in multiple command windows. This simulates running a multi-node Akka cluster. For example, run the following 3 commands in 3 command windows.

```
mvn exec:java -Dexec.mainClass=Main -Dexec.args="2551 8080" -pl Cluster
```

```
mvn exec:java -Dexec.mainClass=Main -Dexec.args="2552 8081" -pl Cluster
```

```
mvn exec:java -Dexec.mainClass=Main -Dexec.args="2553 8082" -pl Cluster
```



## Measuring requests per second
In order to distribute the load between the servers/nodes, a load balancer can be userd :

* haproxy - fast and reliable http reverse proxy and load balancer,

Simple configuration for haproxy daemon can be found in resources dir. Run it with: 
``` haproxy -f src/main/resources/haproxy.conf ```

This will set up a round-robing load balancer with frontend on port 8000 and backends on 8080, 8081 and 8082.
