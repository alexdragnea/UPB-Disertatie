mvn gatling:test -DbaseUrl="https://localhost:8888" -DapiKey='$2a$10$td7Z9a43j7na6xVtAksCyOr11kJPlB3Z/sXXFtt4tAXE5kU1aRUye' -Dusers=10000 -Dduration=120


k3d cluster create multi-node-cluster --servers 3 --agents 2 --api-port 6550 -p "8086:8086@loadbalancer" -p "27017:27017@loadbalancer" -p "8761:8761@loadbalancer" -p "8888:8888@loadbalancer" -p "9000:9000@loadbalancer"
