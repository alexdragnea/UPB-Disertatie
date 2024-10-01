package ro.upb.iotcoreservice.config.influx;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;
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
    public InfluxDBClient influxDBClient() {
        return InfluxDBClientFactory.create(influxDbUrl, influxDbToken.toCharArray(), influxDbOrg, influxDbBucket);
    }

    @Bean
    public InfluxDBClientReactive influxDBClientReactive() {
        InfluxDBClientOptions options = InfluxDBClientOptions.builder().url(influxDbUrl).authenticateToken(influxDbToken.toCharArray()).org(influxDbOrg).bucket(influxDbBucket).build();

        return InfluxDBClientReactiveFactory.create(options);
    }
}