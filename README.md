# Master Thesis from Konstantinos Chaitas
 
## Title: Design and Implementation of a Scalable, Distributed, Location-Based Pub/Sub System

This project/research is based on the [Geobroker project](https://github.com/MoeweX/geobroker) from Jonathan Hasenburg.

## Installation

This is a Java, Maven and Akka project. Please install Java 8 and Maven 3.6.x

```
git clone https://github.com/CostasChaitas/MasterThesis.git
cd MasterThesis
mvn clean install
```
The Maven command builds the project and creates a self contained runnable JAR.

## Run a cluster using Maven

The following Maven command runs a single JVM with 1 Akka actor system on port 2551. Each actor system creates an actor for the HTTP API by default on port 8080.

```
mvn exec:java -Dexec.mainClass=Main -pl Server
```

To run on specific ports use the following -D option for passing in command line arguements(You need to pass one parameter for the actor system port and one paraemeter for the HTTP API port).
```
mvn exec:java -Dexec.mainClass=Main -Dexec.args="2553 8001" -pl Cluster
```

A common way to run tests is to start single JVMs in multiple command windows. This simulates running a multi-node Akka cluster. For example, run the following 3 commands in 3 command windows.

```
mvn exec:java -Dexec.mainClass="com.chaitas.masterthesis.Main" -Dexec.args="2552 8080"
```

```
mvn exec:java -Dexec.mainClass="com.chaitas.masterthesis.Main" -Dexec.args="2553 8001"
```

```
mvn exec:java -Dexec.mainClass="com.chaitas.masterthesis.Main" -Dexec.args="2554 8002"
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
docker-compose-up
```

This will create 3 nodes, a seed and two regular members, called seed, node1, and node2 respectively. Below are listed the ports for each node.

* seed: 8000
* node1: 8001
* node2: 8002

To check the nodes and the status of the cluster you can use : 
```
GET http://localhost:8558/cluster/members
```


## Run a cluster using Kubernetes

First, make sure you have minikube installed. Start minikube : 
```
minikube start
```
In order that Kubernetes finds the locally published Docker image of our application run:
```
eval $(minikube docker-env)
```
Build local Docker registry:
```
mvn clean package docker:build
```
Create serviceAccount and role :
```
kubectl create -f kubernetes/akka-cluster-rbac.yml
```
Create deployment :
```
kubectl create -f kubernetes/akka-cluster-deployment.yml
```
Create service : 
```
kubectl create -f kubernetes/akka-cluster-service.yml
```
Get Kubernetes IP by running : 
```
minikube ip
```
Find the PORT for the services by running(there should be 2 services, 1 for the API and 1 for the Management) : 
```
kubectl get services
```
Use the services :
```
http://{{K8s_IP}}:{{Service_Port}}/cluster/members
ws://{{K8s_IP}}:{{Service_Port}}/api
```









