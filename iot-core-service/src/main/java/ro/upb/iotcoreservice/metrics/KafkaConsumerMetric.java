package ro.upb.iotcoreservice.metrics;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class KafkaConsumerMetric {
    // Metric for the message processing rate (Throughput)
    private final Counter messagesConsumedRate;

    // Metrics for message processing time
    private final Timer messageProcessingTimeTimer;

    // Metrics for message size distribution
    private final DistributionSummary messageSizeSummary;

    // Metrics for failure/success counters
    private final Counter messagesConsumedSuccessCounter;
    private final Counter messagesConsumedFailureCounter;

    private final AtomicLong consumerLag = new AtomicLong(0);

    public KafkaConsumerMetric(MeterRegistry meterRegistry) {
        // Message Consumption Rate (Throughput)
        this.messagesConsumedRate = Counter.builder("kafka.messages.consumed.rate")
                .description("Rate at which messages are consumed")
                .register(meterRegistry);

        // Timer for message processing time
        this.messageProcessingTimeTimer = Timer.builder("kafka.message.processing.time")
                .description("Time taken to process Kafka messages")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);

        // Distribution for message size
        this.messageSizeSummary = DistributionSummary.builder("kafka.messages.size")
                .description("Size of Kafka messages consumed")
                .publishPercentiles(0.5, 0.95)
                .register(meterRegistry);

        // Success and failure counters for message consumption
        this.messagesConsumedSuccessCounter = Counter.builder("kafka.messages.consumed.success")
                .description("Number of successfully consumed messages")
                .register(meterRegistry);

        this.messagesConsumedFailureCounter = Counter.builder("kafka.messages.consumed.failure")
                .description("Number of failed message consumption attempts")
                .register(meterRegistry);

        // Consumer lag gauge
        // Consumer lag metric
        Gauge consumerLagGauge = Gauge.builder("kafka.consumer.lag", consumerLag::get)
                .description("Consumer lag in Kafka")
                .register(meterRegistry);
    }

    // Increment success counter for message consumption
    public void incrementMessagesConsumedSuccess() {
        messagesConsumedSuccessCounter.increment();
    }

    // Increment failure counter for message consumption
    public void incrementMessagesConsumedFailure() {
        messagesConsumedFailureCounter.increment();
    }

    // Record message processing time in the timer
    public void recordMessageProcessingTime(long processingTimeMs) {
        messageProcessingTimeTimer.record(Duration.ofMillis(processingTimeMs));
    }

    // Record the size of consumed messages in the distribution summary
    public void recordMessageSize(long messageSize) {
        messageSizeSummary.record(messageSize);
    }

    // Update the consumer lag (this should be updated periodically based on Kafka state)
    public void updateConsumerLag(long lag) {
        consumerLag.set(lag);
    }

    // Increment the message consumption rate counter
    public void incrementMessageConsumedRate() {
        messagesConsumedRate.increment();
    }

    // Get the current message consumption rate (if needed)
    public long getMessageConsumedRate() {
        return (long) messagesConsumedRate.count();
    }
}
