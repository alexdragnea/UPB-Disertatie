mvn gatling:test -DbaseUrl="https://localhost:8888" -DapiKey='$2a$10$Kg9KJIc.mGBAlakA1FNdxeC4rVGovvrJK8bJhUOud6JPKnA5cAHny' -Dusers=5000 -Dduration=120


k3d cluster create multi-node-cluster --servers 3 --agents 2 --api-port 6550 -p "8086:8086@loadbalancer" -p "27017:27017@loadbalancer" -p "8761:8761@loadbalancer" -p "8888:8888@loadbalancer" -p "9000:9000@loadbalancer"
