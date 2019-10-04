output "kubeconfig" {
  value = "${module.eks.kubeconfig}"
}

output "config-map" {
  value = "${module.eks.config-map-aws-auth}"
}

resource "local_file" "cluster" {
    content     = "${module.eks.kubeconfig}"
    filename = "config-files/config"
}

resource "local_file" "nodes" {
    content     = "${module.eks.config-map-aws-auth}"
    filename = "config-files/nodes.yaml"
}

