# Master Thesis from Konstantinos Chaitas
 
## Title: Design and Implementation of a Scalable, Distributed, Location-Based Pub/Sub System

The evaluation of this project can be found in the [DistributedGeobrokerEvaluation](https://github.com/CostasChaitas/DistributedGeobrokerEvaluation) project.

This project/research is based on the [Geobroker](https://github.com/MoeweX/geobroker) project from Jonathan Hasenburg.

## Installation

This is a Java, Maven and Akka project. Please install Java 8 and Maven 3.6.x

```
git clone https://github.com/CostasChaitas/DistributedGeobroker.git
cd DistributedGeobroker
mvn clean install
```
The Maven command builds the project and creates a self contained runnable JAR.

## Run a cluster using Maven

The following Maven command runs a single JVM with 1 Akka actor system on port 2551. Each actor system creates an actor for the HTTP API by default on port 8080.

```
mvn exec:java -Dexec.mainClass="com.chaitas.distributed.geobroker.Main"
```

To run on specific ports use the following -D option for passing in command line arguements(You need to pass one parameter for the actor system port and one paraemeter for the HTTP API port).
```
mvn exec:java -Dexec.mainClass="com.chaitas.distributed.geobroker.Main" -Dexec.args="2552 8000"
```

A common way to run tests is to start single JVMs in multiple command windows. This simulates running a multi-node Akka cluster. For example, run the following 3 commands in 3 command windows.

```
mvn exec:java -Dexec.mainClass="com.chaitas.distributed.geobroker.Main" -Dexec.args="2552 8080"
```

```
mvn exec:java -Dexec.mainClass="com.chaitas.distributed.geobroker.Main" -Dexec.args="2553 8001"
```

```
mvn exec:java -Dexec.mainClass="com.chaitas.distributed.geobroker.Main" -Dexec.args="2554 8002"
```

To check the nodes and the status of the cluster you can use : 
```
GET http://localhost:8558/cluster/members
```

#### Load balancer
In order to distribute the load between the servers/nodes, a load balancer can be used.
* haproxy - fast and reliable http reverse proxy and load balancer,

Simple configuration for haproxy daemon can be found in resources dir. Run it with: 
``` 
haproxy -f src/main/resources/haproxy.conf 
```

This will set up a round-robing load balancer with frontend on port 8000 and backends on 8080, 8081 and 8082.


## Run a cluster using Docker

First, make sure you have docker installed.

Package the application and make it available locally as a docker image:
```
mvn clean package docker:build
```

Once the docker image is available, you can run the cluster using :
```
docker-compose up
```

This will create 3 nodes, a seed and two regular members, called seed, node1, and node2 respectively. Below are listed the ports for each node.

* seed: 8000
* node1: 8001
* node2: 8002

To check the nodes and the status of the cluster you can use : 
```
GET http://localhost:8558/cluster/members
```