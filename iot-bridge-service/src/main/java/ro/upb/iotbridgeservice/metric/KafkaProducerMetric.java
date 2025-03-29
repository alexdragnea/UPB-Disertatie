package ro.upb.iotbridgeservice.metric;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class KafkaProducerMetric {

    private final AtomicLong messagesSentRate;
    private final Timer messageSendTimeTimer;
    private final DistributionSummary messageSizeSummary;
    private final Counter messagesSentSuccessCounter;
    private final Counter messagesSentFailureCounter;

    public KafkaProducerMetric(MeterRegistry meterRegistry) {
        this.messagesSentRate = new AtomicLong(0);
        this.messageSendTimeTimer = Timer.builder("kafka.message.send.time")
                .description("Time taken to send Kafka messages")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);
        this.messageSizeSummary = DistributionSummary.builder("kafka.messages.size")
                .description("Size of Kafka messages sent")
                .publishPercentiles(0.5, 0.95)
                .register(meterRegistry);
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