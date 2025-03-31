package ro.upb.iotbridgeservice.metric;

import com.netflix.spectator.impl.AtomicDouble;
import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class KafkaProducerMetric {
    private final AtomicDouble compressionRatio;
    private final AtomicLong inFlightMessages;
    private final Counter successCounter;
    private final Counter failureCounter;
    private final Timer sendLatencyTimer;
    private final DistributionSummary messageSizeSummary;

    public KafkaProducerMetric(MeterRegistry meterRegistry) {
        AtomicLong messagesSentRate = new AtomicLong(0);
        this.compressionRatio = new AtomicDouble(1.0);
        this.inFlightMessages = new AtomicLong(0);

        // Success and Failure Counters
        this.successCounter = Counter.builder("kafka.messages.sent.success")
                .description("Total number of successfully sent messages")
                .register(meterRegistry);

        this.failureCounter = Counter.builder("kafka.messages.sent.failure")
                .description("Total number of message failures")
                .register(meterRegistry);

        // Timer for Message Latency
        this.sendLatencyTimer = Timer.builder("kafka.message.send.latency")
                .description("Time taken to send Kafka messages")
                .publishPercentileHistogram()
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);

        // Message Size Summary
        this.messageSizeSummary = DistributionSummary.builder("kafka.messages.size")
                .description("Size of Kafka messages sent (in bytes)")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);

        // Gauges
        Gauge.builder("kafka.producer.rate", messagesSentRate, AtomicLong::get)
                .description("Kafka producer message send rate (messages per second)")
                .register(meterRegistry);

        Gauge.builder("kafka.compression.ratio", compressionRatio, AtomicDouble::get)
                .description("Kafka message compression ratio (compressed/original)")
                .register(meterRegistry);

        Gauge.builder("kafka.in.flight.messages", inFlightMessages, AtomicLong::get)
                .description("Number of Kafka messages currently being sent")
                .register(meterRegistry);
    }

    public void incrementSuccess() {
        successCounter.increment();
    }

    public void incrementFailure() {
        failureCounter.increment();
    }
    public void recordSendLatency(long durationMillis) {
        sendLatencyTimer.record(Duration.ofMillis(durationMillis));
    }

    public void recordMessageSize(long messageSize) {
        messageSizeSummary.record(messageSize);
    }

    public void recordCompressionRatio(long originalSize, long compressedSize) {
        if (originalSize > 0) {
            double ratio = (double) compressedSize / originalSize;
            compressionRatio.set(ratio);
        }
    }

    public void incrementInFlightMessages() {
        inFlightMessages.incrementAndGet();
    }

    public void decrementInFlightMessages() {
        inFlightMessages.decrementAndGet();
    }
}
