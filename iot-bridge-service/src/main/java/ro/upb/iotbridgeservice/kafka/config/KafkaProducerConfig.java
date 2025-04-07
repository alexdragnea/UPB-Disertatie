package ro.upb.iotbridgeservice.kafka.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.MicrometerProducerListener;
import org.springframework.kafka.core.ProducerFactory;
import ro.upb.common.avro.MeasurementMessage;
import ro.upb.iotbridgeservice.kafka.serializer.MeasurementMessageSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Autowired
    private MeterRegistry meterRegistry;

    @Bean
    public ProducerFactory<String, MeasurementMessage> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MeasurementMessageSerializer.class); // Custom serializer

        // Performance Tweaks
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 131072);
        // use this if you want to batch messages more frequently
//        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 10);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 50);
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "zstd");

        // Reliability Tweaks
        configProps.put(ProducerConfig.ACKS_CONFIG, "1");
        // use this if you want to ensure all replicas have the message
//        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        // use this if you want to ensure idempotence (redis handles this)
//        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 200);
        configProps.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 15000);

        // Ensure Ordering
        // use this if you want to ensure ordering of messages
//        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 268435456);

        DefaultKafkaProducerFactory<String, MeasurementMessage> producerFactory = new DefaultKafkaProducerFactory<>(configProps);
        producerFactory.addListener(new MicrometerProducerListener<>(meterRegistry));
        return producerFactory;
    }

    @Bean
    public KafkaTemplate<String, MeasurementMessage> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
