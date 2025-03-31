package ro.upb.iotcoreservice.metrics;

import io.micrometer.core.instrument.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Slf4j
public class KafkaConsumerMetric {
    private final Counter messagesConsumedSuccessCounter;
    private final Counter duplicateMessagesCounter;
    private final Counter messagesConsumedFailureCounter;
    private final Timer messageProcessingTimeTimer;
    private final Timer offsetCommitTimeTimer;

    public KafkaConsumerMetric(MeterRegistry meterRegistry) {
        // Success & Failure Counters
        this.messagesConsumedSuccessCounter = Counter.builder("kafka.consumer.messages.consumed.success")
                .description("Number of successfully consumed messages")
                .register(meterRegistry);

        this.duplicateMessagesCounter = Counter.builder("kafka.consumer.messages.duplicate")
                .description("Number of duplicated messages")
                .register(meterRegistry);

        this.messagesConsumedFailureCounter = Counter.builder("kafka.consumer.messages.consumed.failure")
                .description("Number of failed consumed messages")
                .register(meterRegistry);

        // Processing Time Timer
        this.messageProcessingTimeTimer = Timer.builder("kafka.consumer.message.processing.time")
                .description("Time taken to process Kafka messages")
                .publishPercentileHistogram()
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);

        // Offset Commit Time Timer
        this.offsetCommitTimeTimer = Timer.builder("kafka.consumer.offset.commit.time")
                .description("Time taken to commit Kafka offsets")
                .publishPercentileHistogram()
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);

    }

    // Increment success counter
    public void incrementMessagesConsumedSuccess() {
        messagesConsumedSuccessCounter.increment();
    }

    // Increment failure counter
    public void incrementMessagesConsumedFailure() {
        messagesConsumedFailureCounter.increment();
    }

    // Increment duplicate messages counter
    public void incrementDuplicateMessages() {
        duplicateMessagesCounter.increment();
    }

    // Record processing time
    public void recordMessageProcessingTime(long processingTimeMs) {
        messageProcessingTimeTimer.record(Duration.ofMillis(processingTimeMs));
    }

    // Record offset commit time
    public void recordOffsetCommitTime(long offsetCommitTimeMs) {
        offsetCommitTimeTimer.record(Duration.ofMillis(offsetCommitTimeMs));
    }
}