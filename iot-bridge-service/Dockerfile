FROM openjdk:17
EXPOSE 8001
COPY ./target/iot-bridge-service-0.0.1-SNAPSHOT.jar iot-bridge-service.jar

ENV JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,address=*:5005,server=y,suspend=n"


ENTRYPOINT ["java","-jar","/iot-bridge-service.jar"]