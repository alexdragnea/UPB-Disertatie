package ro.upb.iotcoreservice.config.kafka;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.MicrometerConsumerListener;
import reactor.kafka.receiver.ReceiverOptions;
import ro.upb.common.avro.MeasurementMessage;
import ro.upb.common.constant.KafkaConstants;
import ro.upb.iotcoreservice.kafka.deserializer.MeasurementMessageDeserializer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private final MeterRegistry meterRegistry;

    @Bean
    public ReceiverOptions<String, MeasurementMessage> receiverOptions() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstants.IOT_GROUP_ID);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MeasurementMessageDeserializer.class);

        // Timeout configurations
        configProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10000);   // Session timeout
        configProps.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);   // Request timeout
        configProps.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000); // Max poll interval

        // Performance tweaks
        configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000);  // Fetch 1000 records per poll
        configProps.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1024);  // Minimum data (1 KB)
        configProps.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 25);  // Wait up to 50ms to fetch data
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);  // Disable auto-commit

        ReceiverOptions<String, MeasurementMessage> options = ReceiverOptions.create(configProps);
        return options.consumerListener(new MicrometerConsumerListener(meterRegistry))
                .subscription(Collections.singletonList(KafkaConstants.IOT_EVENT_TOPIC))
                .addAssignListener(partitions -> {
                    // Add custom logic for partition assignment if needed
                })
                .addRevokeListener(partitions -> {
                    // Add custom logic for partition revocation if needed
                });
    }

    @Bean
    public KafkaReceiver<String, MeasurementMessage> kafkaReceiver(ReceiverOptions<String, MeasurementMessage> receiverOptions) {
        return KafkaReceiver.create(receiverOptions);
    }
}