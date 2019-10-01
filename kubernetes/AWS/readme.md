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

## simple setup

if you want the simplest possible setup, install the nessessary tools above and change the execute this file appropriatly to point to the right destinations for the execturables in the file.

Then log into the AWS CLI and execute the `execute-this` executable and after around 13 minutes the cluster should be provisioned and configured to run the applications.

Alternatively use the following instructions to do the steps take in the executable file automatically


## How provision the cluster

Follow the instructions to provision the cluster

1. login to the AWS CLI
2. Clone this repo: `git clone https://github.com/GDW1/EKSTerraformSpinup.git`
3. Go into the folder
4. Plan the Terraform: `terraform plan`
5. Assuming that there were no errors you can apply: `terraform apply`. This should take around 13 minutes.

Upon completion there should be two outputs:
* a kubeconfig
* a nodeconfig

Place the kubeconfig where kubectl keeps its config (usually ~/.kube/config) and
put the nodeconfig in a yaml file and run `kubectl apply -f {path-to-yaml-file}`

You can verify the initialization of the cluster by entering `kubectl get nodes` into terminal and seeing if any nodes show up.

## Tearing down the cluster

Before tearing down the cluster make sure that you tear down the anisble work first to insure that the only thing remaining was the work down with Terraform. To do this go into the ansible folder and run `ansible-playbook tear-down.yaml`

Then use the command `terraform plan -destroy` to make sure that it can correctly tear down the infastructure

Lastly run the command `terraform destroy` to delete the infastructure. This should take around 12 minutes 
