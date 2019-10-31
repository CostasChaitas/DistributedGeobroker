# Master Thesis from Konstantinos Chaitas
 
## Title: Design and Implementation of a Scalable, Distributed, Location-Based Pub/Sub System

The evaluation of this project can be found in the [DistributedGeobrokerEvaluation](https://github.com/CostasChaitas/DistributedGeobrokerEvaluation) project.

This project/research is based on the [Geobroker](https://github.com/MoeweX/geobroker) project from Jonathan Hasenburg.

## Installation

This project is aimed to be deployed in a Kubernetes environment on AWS EKS, using Docker images. If you want to use the project locally, please move to the **local** branch and follow the instructions.

In order to build the Docker registry from this project, please install Java 8 and Maven 3.6.x and Docker. Then run the following commands: 

```
git clone https://github.com/CostasChaitas/DistributedGeobroker.git
cd DistributedGeobroker
mvn clean install
mvn clean package docker:build
```

## Deployment

This project uses Terraform and Ansible scripts to automatically deploy the application on a Kubernetes cluster on AWS EKS. Please move to the **kubernetes** folder and follow the instructions.