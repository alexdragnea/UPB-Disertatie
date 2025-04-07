package ro.upb.iotcoreservice.metrics;

import io.micrometer.core.instrument.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Slf4j
public class KafkaConsumerMetric {
    private final Counter duplicateMessagesCounter;

    public KafkaConsumerMetric(MeterRegistry meterRegistry) {
        // Duplicate Messages Counter
        this.duplicateMessagesCounter = Counter.builder("kafka.consumer.messages.duplicate")
                .description("Number of duplicated messages")
                .register(meterRegistry);
    }

    // Increment duplicate messages counter
    public void incrementDuplicateMessages() {
        duplicateMessagesCounter.increment();
    }
}