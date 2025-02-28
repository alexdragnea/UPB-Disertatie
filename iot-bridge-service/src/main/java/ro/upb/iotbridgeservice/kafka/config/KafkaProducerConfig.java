package ro.upb.iotbridgeservice.kafka.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import ro.upb.common.dto.MeasurementRequest;
import ro.upb.iotbridgeservice.kafka.serializer.IotRequestSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, MeasurementRequest> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, IotRequestSerializer.class);

        // 🚀 **Performance Tweaks**
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 131072); // 🔥 Increase batch size (128 KB)
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 5);  // ⏳ Reduce wait time for batching
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "zstd"); // 🚀 Use ZSTD (faster & better than snappy)

        // ✅ **Reliability Tweaks**
        configProps.put(ProducerConfig.ACKS_CONFIG, "all"); // ✅ Ensure all replicas acknowledge messages (stronger durability)
        configProps.put(ProducerConfig.RETRIES_CONFIG, 5); // 🔄 Increase retries for transient failures
        configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 200); // ⏳ Slightly increase retry backoff to avoid congestion
        configProps.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000); // ⏳ Keep request timeout at 30s

        // 🎯 **Throughput Tweaks**
        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5); // 🚀 Reduce to 5 to ensure ordering in case of retries
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 268435456); // 🔥 Double buffer memory to 256MB

        return new DefaultKafkaProducerFactory<>(configProps);
    }


    @Bean
    public KafkaTemplate<String, MeasurementRequest> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}