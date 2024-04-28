package ro.upb.iotdiscoveryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class IotDiscoveryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IotDiscoveryServiceApplication.class, args);
    }

}
