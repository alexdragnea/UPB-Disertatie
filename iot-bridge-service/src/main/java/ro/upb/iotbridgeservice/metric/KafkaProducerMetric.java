package ro.upb.iotbridgeservice.metric;


import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class KafkaProducerMetric {

    // Metrics for message send rate (throughput)
    private final AtomicLong messagesSentRate;

    // Metrics for message send time
    private final Timer messageSendTimeTimer;

    // Metrics for message size distribution
    private final DistributionSummary messageSizeSummary;

    // Metrics for failure/success counters
    private final Counter messagesSentSuccessCounter;
    private final Counter messagesSentFailureCounter;

    public KafkaProducerMetric(MeterRegistry meterRegistry) {
        // Message Send Rate (Throughput)
        this.messagesSentRate = new AtomicLong(0);

        // Timer for message send time
        this.messageSendTimeTimer = Timer.builder("kafka.message.send.time")
                .description("Time taken to send Kafka messages")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);

        // Distribution for message size
        this.messageSizeSummary = DistributionSummary.builder("kafka.messages.size")
                .description("Size of Kafka messages sent")
                .publishPercentiles(0.5, 0.95)
                .register(meterRegistry);

        // Success and failure counters for message sending
        this.messagesSentSuccessCounter = Counter.builder("kafka.messages.sent.success")
                .description("Number of successfully sent messages")
                .register(meterRegistry);

        this.messagesSentFailureCounter = Counter.builder("kafka.messages.sent.failure")
                .description("Number of failed message sending attempts")
                .register(meterRegistry);
    }

    public void incrementMessagesSentSuccess() {
        messagesSentSuccessCounter.increment();
    }

    public void incrementMessagesSentFailure() {
        messagesSentFailureCounter.increment();
    }

    public void recordMessageSendTime(long sendTimeMs) {
        messageSendTimeTimer.record(Duration.ofMillis(sendTimeMs));
    }

    public void recordMessageSize(long messageSize) {
        messageSizeSummary.record(messageSize);
    }

    public void incrementMessageSentRate() {
        messagesSentRate.incrementAndGet();
    }
}