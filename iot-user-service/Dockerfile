FROM openjdk:21
EXPOSE 8003
COPY ./target/iot-user-service-0.0.1-SNAPSHOT.jar iot-user-service.jar

ENV JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,address=*:5007,server=y,suspend=n"


ENTRYPOINT ["java","-jar","/iot-user-service.jar"]