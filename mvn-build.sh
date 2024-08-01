#!/bin/bash

# List of services
services=(
  "iot-bridge-service"
  "iot-core-service"
  "iot-gateway-service"
  "iot-discovery-service"
  "iot-user-service"
)

# Iterate over each service and run the Maven command
for service in "${services[@]}"; do
  echo "Running Maven build for $service"
  (cd "$service" && ./mvnw clean install -DskipTests)
  if [ $? -ne 0 ]; then
    echo "Maven build failed for $service"
    exit 1
  fi
done

echo "Maven build completed for all services"
