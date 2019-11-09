# Ansible EKS cluster 


## What resources are created
    
**General resources** 
1. Kubernetes metrics server
2. Kubernetes dashboard
3. Prometheus monitoring
4. Grafana monitoring   

**Application Specific resources**
1. Namespace
2. Roles
3. Deployment
4. Service
5. Auto-scaling

## Simple setup

In order to deploy the general tools listed above, run the following command: 
```
sudo ansible-playbook set-up-addons.yaml
```

In order to deploy the monitoring system run the following command: 
```
sudo ansible-playbook set-up-monitoring.yaml
```

In order to deploy the application run the following command: 
```
sudo ansible-playbook full-set-up.yaml
```

## Access the Kubernetes dashboard
In order to use the kubernetes dashboard that is already configured, you could use the following command : 
```
kubectl proxy
```
Then you can use the following : http://localhost:8001/api/v1/namespaces/kube-system/services/https:kubernetes-dashboard:/proxy/

In order to login, get your token using the following command:
```
aws eks get-token --cluster-name distributed-geobroker-thesis | jq -r '.status.token'
```

## Access Prometheus and Grafana dashboard
In order to use the Grafana dashboard that is already configured, you could use the following command: 
```
kubectl port-forward -n monitoring service/prometheus-grafana 3000:80
```

In order to get your password run the following command: 
```
kubectl get secret --namespace monitoring prometheus-grafana -o jsonpath="{.data.admin-password}" | base64 --decode ; echo
```

Then you can use the following : http://localhost:3000. Username is admin.


In order to use the Prometheus dashboard that is already configured, you could use the following command: 
```
kubectl port-forward -n monitoring prometheus-prometheus-prometheus-oper-prometheus-0 9090
```
Then you can use the following : http://localhost:9090.


### Cleaning up

You can destroy this deployment entirely by running:

```bash
sudo ansible-playbook tear-down.yml
```