FROM openjdk:21

# Expose the application port and JMX port
EXPOSE 8888
EXPOSE 1099

# Set the JAVA_OPTS environment variable for JMX configuration
ENV JAVA_OPTS="-Dcom.sun.management.jmxremote \
               -Dcom.sun.management.jmxremote.authenticate=false \
               -Dcom.sun.management.jmxremote.ssl=false \
               -Dcom.sun.management.jmxremote.local.only=false \
               -Dcom.sun.management.jmxremote.port=1099 \
               -Dcom.sun.management.jmxremote.rmi.port=1099 \
               -Djava.rmi.server.hostname=0.0.0.0"

# Copy the application jar file into the container
COPY ./target/iot-gateway-service-0.0.1-SNAPSHOT.jar iot-gateway-service.jar

# Start the application with JAVA_OPTS
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /iot-gateway-service.jar"]
