#
# Workstation External IP

data "http" "workstation-external-ip" {
  url = "http://whatismyip.akamai.com/"
}

locals {
  workstation-external-cidr = "${chomp(data.http.workstation-external-ip.body)}/32"
}
