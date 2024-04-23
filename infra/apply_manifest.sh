#!/bin/bash

# Services
services=(
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/services/iot-coreservice.yaml"
)

for service in "${services[@]}"; do
    kubectl apply -f "$service"
    sleep 1
done

# Deployments
deployments=(
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie//master/infra/deployments/iot-coreservice.yaml"
)

sleep 5

for deployment in "${deployments[@]}"; do
    kubectl apply -f "$deployment"
    sleep 1
done

# Horizontal Pod Autoscaler (HPA)
hpa_files=(
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie//master/infra/hpa/iot-coreservice-hpa.yaml"
)

for hpa_file in "${hpa_files[@]}"; do
    kubectl apply -f "$hpa_file"
    sleep 1
done