package ro.upb.iotbridgeservice.kafka.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import ro.upb.common.dto.MeasurementRequestDto;
import ro.upb.iotbridgeservice.kafka.serializer.IotRequestSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.delivery-timeout-ms:120000}")
    private int deliveryTimeoutMs;

    @Value("${spring.kafka.producer.request-timeout-ms:30000}")
    private int requestTimeoutMs;

    @Value("${spring.kafka.producer.linger-ms:10}")
    private int lingerMs;

    @Bean
    public ProducerFactory<String, MeasurementRequestDto> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, IotRequestSerializer.class);
        // Timeout configurations
        configProps.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeoutMs); // Delivery timeout
        configProps.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMs);   // Request timeout
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);                   // Linger timeout

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, MeasurementRequestDto> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}