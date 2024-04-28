#!/bin/bash


# Kafka
kafka=(
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/kafka/zookeeper.yaml"
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/kafka/kafka.yaml"
)

for kafka in "${kafka[@]}"; do
    kubectl apply -f "$kafka"
    sleep 10
done

# Services
services=(
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/services/iot-core-service-svc.yaml"
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/services/iot-discovery-service-svc.yaml"
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/services/iot-bridge-service-svc.yaml"
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/services/iot-gateway-service-svc.yaml"
)

for service in "${services[@]}"; do
    kubectl apply -f "$service"
    sleep 1
done

# Deployments
deployments=(
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/deployments/iot-discovery-service.yaml"
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/deployments/iot-gateway-service.yaml"
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/deployments/iot-core-service.yaml"
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/deployments/iot-bridge-service.yaml"
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
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/hpa/iot-bridge-service-hpa.yaml"
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/hpa/iot-gateway-service-hpa.yaml"
)

for hpa_file in "${hpa_files[@]}"; do
    kubectl apply -f "$hpa_file"
    sleep 1
done