
mvn gatling:test -DbaseUrl="https://localhost:8888" -Dusers=20000 -Dduration=120

k3d cluster create disertatie-cluster --servers 3 --agents 2 --api-port 6550 -p "8888:8888@loadbalancer" -p "3333:3333@loadbalancer" -p "8761:8761@loadbalancer" -p "8086:8086@loadbalancer" -p "27015:27015@loadbalancer" -p "3000:3000@loadbalancer" -p "6379:6379@loadbalancer"
