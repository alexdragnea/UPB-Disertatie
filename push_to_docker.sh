#!/bin/bash

# Define the services
SERVICES=("iot-core-service" "iot-discovery-service" "iot-bridge-service" "iot-gateway-service")

# Function to build and push Docker images
build_and_push() {
    local SERVICE=$1
    echo "Building and pushing Docker image for $SERVICE..."
    
    # Change directory to the service
    cd "$SERVICE" || { echo "Failed to change directory to $SERVICE"; exit 1; }
    
    # Build Docker image
    docker build -t "alexdragnea/disertatie-$SERVICE:latest" .
    
    # Push Docker image
    docker push "alexdragnea/disertatie-$SERVICE:latest"
    
    # Change back to the previous directory
    cd ..
}

# Loop through each service and build and push Docker image
for SERVICE in "${SERVICES[@]}"; do
    build_and_push "$SERVICE"
done
