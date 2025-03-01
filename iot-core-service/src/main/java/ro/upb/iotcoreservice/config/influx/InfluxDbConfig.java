package ro.upb.iotcoreservice.config.influx;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;
import com.influxdb.client.reactive.InfluxDBClientReactive;
import com.influxdb.client.reactive.InfluxDBClientReactiveFactory;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;


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

    private InfluxDBClient influxDBClient;
    private InfluxDBClientReactive influxDBClientReactive;

    @Bean
    @Lazy  // Ensures it's only created when needed
    public InfluxDBClient influxDBClient() {
        if (influxDBClient == null) {
            influxDBClient = InfluxDBClientFactory.create(influxDbUrl, influxDbToken.toCharArray(), influxDbOrg, influxDbBucket);
        }
        return influxDBClient;
    }

    @Bean
    @Lazy
    public InfluxDBClientReactive influxDBClientReactive() {
        if (influxDBClientReactive == null) {
            InfluxDBClientOptions options = InfluxDBClientOptions.builder()
                    .url(influxDbUrl)
                    .authenticateToken(influxDbToken.toCharArray())
                    .org(influxDbOrg)
                    .bucket(influxDbBucket)
                    .build();
            influxDBClientReactive = InfluxDBClientReactiveFactory.create(options);
        }
        return influxDBClientReactive;
    }

    @PreDestroy
    public void closeInfluxClients() {
        if (influxDBClient != null) {
            influxDBClient.close();
        }
        if (influxDBClientReactive != null) {
            influxDBClientReactive.close();
        }
    }
}
