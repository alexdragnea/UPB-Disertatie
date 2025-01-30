# README

## InfluxDB credentials
user: admin, password: adminupb, org: upb, bucket: iot-measurement-bucket

k3d cluster create multi-node-cluster --servers 3 --agents 2 --api-port 6550 -p "8888:8888@loadbalancer" -p "3333:3333@loadbalancer" -p "8761:8761@loadbalancer" 
