# terraform-aws-eks

Deploy a full AWS EKS cluster with Terraform

## What resources are created

1. VPC
2. Internet Gateway (IGW)
3. Public and Private Subnets
4. Security Groups, Route Tables and Route Table Associations
5. IAM roles, instance profiles and policies
6. An EKS Cluster
7. Autoscaling group and Launch Configuration
8. Worker Nodes in a private Subnet
9. The ConfigMap required to register Nodes with EKS
10. KUBECONFIG file to authenticate kubectl using the heptio authenticator aws binary

## Configuration

You can configure the properties of the EKS Kubernetes cluster using  **./variables.tf** file:

| Name                 | Description                       | Default       |
|----------------------|-----------------------------------|---------------|
| `cluster-name`       | The name of your EKS Cluster      | `my-cluster`  |
| `aws-region`         | The AWS Region to deploy EKS      | `us-west-2`   |
| `k8s-version`        | The desired K8s version to launch | `1.11`        |
| `node-instance-type` | Worker Node EC2 instance type     | `m4.large`    |
| `desired-capacity`   | Autoscaling Desired node capacity | `2`           |
| `max-size`           | Autoscaling Maximum node capacity | `5`           |
| `min-size`           | Autoscaling Minimum node capacity | `1`           |
| `vpc-subnet-cidr`    | Subnet CIDR                       | `10.0.0.0/16` |



### Terraform

You need to run the following commands to create the resources with Terraform:

```bash
terraform init
terraform plan
terraform apply
```

> TIP: you should save the plan state `terraform plan -out eks-state` or even better yet, setup [remote storage](https://www.terraform.io/docs/state/remote.html) for Terraform state. You can store state in an [S3 backend](https://www.terraform.io/docs/backends/types/s3.html), with locking via DynamoDB

### Configure kubectl for EKS

Setup your `KUBECONFIG`

```bash
terraform output kubeconfig > ~/.kube/eks-cluster
terraform output kubeconfig > ~/.kube/config
export KUBECONFIG=~/.kube/eks-cluster
```

### Authorize worker nodes

Get the config from terraform output, and save it to a yaml file:

```bash
terraform output config-map > config-map-aws-auth.yaml
```

Apply the config map to EKS:

```bash
kubectl apply -f config-map-aws-auth.yaml
```

You can verify the worker nodes are joining the cluster

```bash
kubectl get nodes --watch
```

### Cleaning up

You can destroy this cluster entirely by running:

```bash
terraform plan -destroy
terraform destroy  --force
```
