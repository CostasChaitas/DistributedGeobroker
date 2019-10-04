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

This project is aimed to be used in a Kubernetes environment using Docker images. Please install Docker and Kubernetes.
To build a Docker registry from this project please run the following command. 

```
mvn clean package docker:build
```
