# Terraform and Ansible EKS cluster 

## Purpose

The purpose of this code is to do two things:

1. To provision the nessessary resources on AWS to create the cluster and nessessary tools to configure it
2. To configure the created Kubernetes EKS cluster to run the application

## Used Tools

The following tools were used to create these tools

* Terraform
* AWS CLI
* Kubernetes
* Ansible
* Kubectl
* Docker desktop
* pip


## How to use this repo

1. First, move to the **terraformEKS** folder and follow the instructions to create the required infrastructure on AWS
2. Then, Move to the **ansible** folder and follow the instructions to deploy the application on the created EKS cluster
