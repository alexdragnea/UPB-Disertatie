package ro.upb.iotcoreservice.config;

import org.testcontainers.containers.InfluxDBContainer;
import org.testcontainers.utility.DockerImageName;

public class InfluxDBTestContainer {

    private static final DockerImageName INFLUXDB_IMAGE = DockerImageName.parse("influxdb:2.0.7");
    private static final InfluxDBContainer<?> INFLUXDB_CONTAINER = new InfluxDBContainer<>(INFLUXDB_IMAGE);

    static {
        INFLUXDB_CONTAINER.start();
    }

    public static InfluxDBContainer<?> getInfluxDBContainer() {
        return INFLUXDB_CONTAINER;
    }

    public static String getInfluxDBUrl() {
        return INFLUXDB_CONTAINER.getUrl();
    }
}