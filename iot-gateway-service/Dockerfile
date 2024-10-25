FROM openjdk:21
EXPOSE 8888
COPY ./target/iot-gateway-service-0.0.1-SNAPSHOT.jar iot-gateway-service.jar
ENTRYPOINT ["java","-jar","/iot-gateway-service.jar"]