# Terraform and Ansible EKS cluster 

## Purpose

The purpose of this code is to do two things:

1. To provision the nessessary resources on AWS to create the cluster and nessessary tools to configure it
2. To configure the application to run a simple application

## Used Tools

The following tools were used to create these tools

* Terraform
* AWS CLI
* Kubernetes
* Ansible
* Kubectl
* Docker desktop
* K8s go plugin https://github.com/ericchiang/k8s
* Openshift
* golang
* pip

## Intalling the nessessary tools

From the preceding list you'll need to manually install the:
* AWS cli: https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html
* pip3: https://www.python.org/downloads/
* Ansible: run in terminal `pip3 install ansible`
* Kubectl: https://kubernetes.io/docs/tasks/tools/install-kubectl/
* Docker Desktop: https://www.docker.com/products/docker-desktop

## Simple setup

if you want the simplest possible setup, install the nessessary tools above and change the execute this file appropriatly to point to the right destinations for the execturables in the file.

Then log into the AWS CLI and execute the `execute-this` executable and after around 13 minutes the cluster should be provisioned and configured to run the applications.

Alternatively use the following instructions to do the steps take in the executable file automatically


## How to use this repo

1. Move to the **terraformEKS** folder and follow the instructions to create the infrastructure on AWS
2. Move to the **ansible** folder and follow the instructions to deploy the code
