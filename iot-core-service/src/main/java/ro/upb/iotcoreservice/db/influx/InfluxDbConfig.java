package ro.upb.iotcoreservice.db.influx;

import com.influxdb.client.reactive.InfluxDBClientReactive;
import com.influxdb.client.reactive.InfluxDBClientReactiveFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxDbConfig {

    @Value("${influxdb.url}")
    private String influxDbUrl;

    @Value("${influxdb.token}")
    private String influxDbToken;

    @Value("${influxdb.bucket}")
    private String influxDbBucket;

    @Value("${influxdb.organization}")
    private String influxDbOrg;

    @Bean
    public InfluxDBClientReactive influxDBClientReactive() {
        return InfluxDBClientReactiveFactory.create(influxDbUrl, influxDbToken.toCharArray(), influxDbOrg, influxDbBucket);
    }
}