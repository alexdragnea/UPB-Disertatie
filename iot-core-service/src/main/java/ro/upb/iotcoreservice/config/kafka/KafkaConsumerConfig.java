package ro.upb.iotcoreservice.config.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import ro.upb.common.constant.KafkaConstants;
import ro.upb.common.dto.MeasurementRequest;
import ro.upb.iotcoreservice.kafka.deserializer.IotRequestDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, MeasurementRequest> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstants.IOT_GROUP_ID);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, IotRequestDeserializer.class);

        // Timeout configurations
        configProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10000);   // Session timeout
        configProps.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);   // Request timeout
        configProps.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000); // Max poll interval


        // new props for performance tweaking
        configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);  // Fetch 500 records per poll
        configProps.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1024);  // Minimum data (1 KB)
        configProps.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 50);  // Wait up to 50ms to fetch data
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);  // Disable auto-commit



        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), new IotRequestDeserializer());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MeasurementRequest> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MeasurementRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        // new prop for performance tweaking
        factory.setConcurrency(5);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        // add error handler
        return factory;
    }

    //For large-scale, high-frequency systems, consider integrating metrics (e.g., count of successfully or failed deserializations).
    // You could use something like Micrometer for that, though it's more of an advanced optimization for monitoring.

}