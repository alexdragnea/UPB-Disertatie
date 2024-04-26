package ro.upb.iotcoreservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class IoTCoreServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IoTCoreServiceApplication.class, args);
    }

}
