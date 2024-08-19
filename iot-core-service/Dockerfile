FROM openjdk:17
EXPOSE 8002
COPY ./target/iot-core-service-0.0.1-SNAPSHOT.jar iot-core-service.jar

ENV JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,address=*:5006,server=y,suspend=n"


ENTRYPOINT ["java","-jar","/iot-core-service.jar"]