#!/bin/bash

# Define deployments
deployments=(
    "iot-discovery-service"
    "iot-core-service"
    "iot-bridge-service"
)

# Define hpas
hpas=(
    "hpa-iot-discovery-service"
    "hpa-iot-core-service"
    "hpa-iot-bridge-service"
)

# Define services
services=(
    "iot-discovery-service"
    "iot-core-service"
    "iot-bridge-service"
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