#!/bin/bash


 helm repo add bitnami https://charts.bitnami.com/bitnami
 helm repo update
 kubectl create namespace observability
helm install prometheus bitnami/kube-prometheus -n observability \
  --create-namespace \
  --set grafana.enabled=true \
  --set alertmanager.enabled=false \
  --set kube-prometheus.blackboxExporter.enabled=false \
  --set kube-state-metrics.enabled=false \
  --set nodeExporter.enabled=false \
  --set prometheus.replicaCount=1 \
  --set admin.user=admin \
  --set admin.password=admin





 sleep 30

 helm install grafana bitnami/grafana \
   --namespace observability \
   --set admin.user=admin \
   --set admin.password=admin

# Kafka
kafka=(
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/kafka/kafka.yaml"
)

for kafka in "${kafka[@]}"; do
    kubectl apply -f "$kafka"
    sleep 30
done

# Redis
redis=(
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/redis/redis.yaml"
)
for redis in "${redis[@]}"; do
    kubectl apply -f "$redis"
    sleep 30
done

# PVC

pvc=(
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/db/pv-db.yaml"
)

for pvc in "${pvc[@]}"; do
    kubectl apply -f "$pvc"
    sleep 1
done


db=(
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/db/influxdb.yaml"
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/db/mongo.yaml"
)

for db in "${db[@]}"; do
    kubectl apply -f "$db"
    sleep 30
done

# Services
services=(
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/services/iot-dashboard-service-svc.yaml"
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/services/iot-core-service-svc.yaml"
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/services/iot-user-service-svc.yaml"
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/services/iot-discovery-service-svc.yaml"
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/services/iot-bridge-service-svc.yaml"
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/services/iot-gateway-service-svc.yaml"
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/services/service-monitor.yaml"
)

for service in "${services[@]}"; do
    kubectl apply -f "$service"
    sleep 1
done

# Deployments
deployments=(
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/deployments/iot-dashboard-service.yaml"
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/deployments/iot-discovery-service.yaml"
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/deployments/iot-user-service.yaml"
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/deployments/iot-gateway-service.yaml"
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/deployments/iot-core-service.yaml"
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/deployments/iot-bridge-service.yaml"
)


sleep 5

for deployment in "${deployments[@]}"; do
    kubectl apply -f "$deployment"
    sleep 60
done

# Horizontal Pod Autoscaler (HPA)
hpa_files=(
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie//master/infra/hpa/iot-core-service-hpa.yaml"
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie//master/infra/hpa/influxdb-hpa.yaml"
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/hpa/iot-discovery-service-hpa.yaml"
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/hpa/iot-bridge-service-hpa.yaml"
    "https://raw.githubusercontent.com/alexdragnea/UPB-Disertatie/master/infra/hpa/iot-gateway-service-hpa.yaml"
)

sleep 30

for hpa_file in "${hpa_files[@]}"; do
    kubectl apply -f "$hpa_file"
    sleep 5
done

# kubectl port-forward --namespace observability svc/prometheus-kube-prometheus-prometheus 9090:9090
# kubectl port-forward --namespace observability svc/grafana 3000:3000
# http://prometheus-kube-prometheus-prometheus.observability.svc.cluster.local:9090
