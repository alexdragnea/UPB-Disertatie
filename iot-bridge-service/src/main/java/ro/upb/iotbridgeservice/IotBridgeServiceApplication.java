package ro.upb.iotbridgeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class IotBridgeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IotBridgeServiceApplication.class, args);
    }

}
