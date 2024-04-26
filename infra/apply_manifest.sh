#!/bin/bash

# Services
services=(
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/services/iot-core-service-svc.yaml"
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/services/iot-discovery-service-svc.yaml"
)

for service in "${services[@]}"; do
    kubectl apply -f "$service"
    sleep 1
done

# Deployments
deployments=(
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/deployments/iot-core-service.yaml"
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/deployments/iot-discovery-service.yaml"
)

sleep 5

for deployment in "${deployments[@]}"; do
    kubectl apply -f "$deployment"
    sleep 30
done

# Horizontal Pod Autoscaler (HPA)
hpa_files=(
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie//master/infra/hpa/iot-core-service-hpa.yaml"
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/hpa/iot-discovery-service-hpa.yaml"
)

for hpa_file in "${hpa_files[@]}"; do
    kubectl apply -f "$hpa_file"
    sleep 1
done