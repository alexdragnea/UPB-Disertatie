package ro.upb.iotuserservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class IotUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IotUserServiceApplication.class, args);
    }

}
