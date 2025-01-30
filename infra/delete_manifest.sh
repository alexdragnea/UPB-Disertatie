#!/bin/bash

# Define deployments
deployments=(
    "iot-dashboard-service"
    "iot-discovery-service"
    "iot-core-service"
    "iot-bridge-service"
    "iot-gateway-service"
    "iot-user-service"
    "kafka"
    "kafka-drop-deployment"
    "zookeeper-deployment"
    "influxdb"
    "mongodb"
)

# Define hpas
hpas=(
    "hpa-iot-discovery-service"
    "hpa-iot-core-service"
    "hpa-iot-bridge-service"
    "hpa-iot-gateway-service"
    "hpa-iot-user-service"
    "hpa-influxdb"
)

# Define services
services=(
    "iot-dashboard-service"
    "iot-discovery-service"
    "iot-core-service"
    "iot-bridge-service"
    "iot-gateway-service"
    "iot-user-service"
    "kafka-service"
    "kafka-drop-service"
    "zookeeper-service"
    "influxdb"
    "mongodb"
)

read -p "This will delete the deployments, services, and HPAs. Are you sure? (y/n): " confirmation
if [ "$confirmation" != "y" ]; then
    echo "Operation canceled."
    exit 1
fi

# Delete deployments
for deployment in "${deployments[@]}"; do
    kubectl delete deployment "$deployment"
    if [ $? -eq 0 ]; then
        echo "Deployment '$deployment' deleted successfully."
    else
        echo "Error deleting deployment '$deployment'."
    fi
done

# Delete services
for service in "${services[@]}"; do
    kubectl delete service "$service"
    if [ $? -eq 0 ]; then
        echo "Service '$service' deleted successfully."
    else
        echo "Error deleting service '$service'."
    fi
done

# Delete hpas
for hpa in "${hpas[@]}"; do
    kubectl delete hpa "$hpa"
    if [ $? -eq 0 ]; then
        echo "HPA '$hpa' deleted successfully."
    else
        echo "Error deleting HPA '$hpa'."
    fi
done

kubectl delete statefulset kafka
kubectl delete statefulset zookeeper
kubectl delete pvc --all
kubectl delete pv --all
# kubectl delete servicemonitors service-monitor
# helm uninstall grafana
# helm uninstall prometheus