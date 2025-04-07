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

    public KafkaProducerMetric(MeterRegistry meterRegistry) {
        AtomicLong messagesSentRate = new AtomicLong(0);
        this.compressionRatio = new AtomicDouble(1.0);
        this.inFlightMessages = new AtomicLong(0);


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
