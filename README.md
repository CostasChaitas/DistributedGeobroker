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


## Run a cluster using Kubernetes

First, make sure you have minikube installed. Start minikube : 
```
minikube start
```
In order that Kubernetes finds the locally published Docker image of our application run:
```
eval $(minikube docker-env)
```

Create a Kubernetes namespace and set it as the current namespace: 
```
kubectl create namespace master-thesis-namespace
kubectl config set-context --current --namespace=master-thesis-namespace
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

In case you want you want to scale the number of Pods : 

```
kubectl scale --replicas=5 deployment/master-thesis
```

You can also configure auto scaling of the pods based on some criteria, e.g CPU utilization : 
```
kubectl autoscale deployment master-thesis --cpu-percent=50 --min=3 --max=10
```
