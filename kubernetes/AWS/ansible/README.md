# Ansible EKS cluster 


## What resources are created
    
1. Namespace
2. Roles
3. Deployment
4. Service

## Simple setup

In order to deploy this repo run the following command: 
```
sudo ansible-playbook full-set-up.yaml
```

## Check the Kubernetes dashboard
In order to use the kubernetes dashboard that is already configured, you could use the following command : 
```
kubectl proxy
```

Then you can use the following : http://localhost:8001/api/v1/namespaces/kube-system/services/https:kubernetes-dashboard:/proxy/

In order to login, get your token using the following command:
```
aws eks get-token --cluster-name distributed-geobroker-cluster
```

### Cleaning up

You can destroy this deployment entirely by running:

```bash
sudo ansible-playbook tear-down.yml
```