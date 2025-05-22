package ro.upb.iotbridgeservice.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.util.retry.Retry;
import ro.upb.common.avro.MeasurementMessage;
import ro.upb.common.dto.MeasurementRequest;
import ro.upb.iotbridgeservice.exception.KafkaProcessingEx;
import ro.upb.iotbridgeservice.exception.KafkaValidationEx;
import ro.upb.iotbridgeservice.metric.KafkaProducerMetric;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageProducer {

    private final KafkaSender<String, MeasurementMessage> kafkaSender;
    private final KafkaProducerMetric kafkaProducerMetric;

    public Mono<Void> sendIotMeasurement(String topic, MeasurementRequest sensor) {
        validateMeasurement(sensor);

        MeasurementMessage message = buildMessage(sensor);

        kafkaProducerMetric.incrementInFlightMessages();

        SenderRecord<String, MeasurementMessage, String> record = SenderRecord.create(topic, null, null, UUID.randomUUID().toString(), message, UUID.randomUUID().toString());

        return kafkaSender.send(Mono.just(record))
                .doOnNext(result -> {
                    kafkaProducerMetric.decrementInFlightMessages();
                    log.info("Successfully sent message to topic: {} with offset: {}", topic, result.recordMetadata().offset());

                    // Compression Ratio Calculation
                    long originalSize = message.toString().length(); // Uncompressed size
                    long compressedSize = result.recordMetadata().serializedValueSize();
                    kafkaProducerMetric.recordCompressionRatio(originalSize, compressedSize);
                })
                .doOnError(ex -> {
                    kafkaProducerMetric.decrementInFlightMessages();
                    log.error("Kafka send failed for topic {}: {}", topic, ex.getMessage());
                })
                .retryWhen(Retry.backoff(3, Duration.ofMillis(500))
                        .maxBackoff(Duration.ofSeconds(5))
                        .jitter(0.3)
                        .doBeforeRetry(signal ->
                                log.warn("Retrying Kafka send attempt #{} due to {}", signal.totalRetries() + 1, signal.failure().getMessage()))
                )
                .onErrorMap(ex -> new KafkaProcessingEx("Kafka send failed after retries: " + ex.getMessage()))
                .then();
    }

    private MeasurementMessage buildMessage(MeasurementRequest sensor) {
        MeasurementMessage measurementMessage = new MeasurementMessage();
        measurementMessage.setId(UUID.randomUUID().toString());
        measurementMessage.setMeasurement(sensor.getMeasurement());
        measurementMessage.setUserId(sensor.getUserId());
        measurementMessage.setValue(sensor.getValue());
        measurementMessage.setUnit(sensor.getUnit());
        return measurementMessage;
    }

    private void validateMeasurement(MeasurementRequest measurementRequest) {
        if (measurementRequest == null) {
            throw new KafkaValidationEx("MeasurementRequest cannot be null");
        }

        String measurement = measurementRequest.getMeasurement();
        if (StringUtils.isBlank(measurement)) {
            throw new KafkaValidationEx("Measurement must not be blank");
        }
        if (measurement.length() > 50) {
            throw new KafkaValidationEx("Measurement must not exceed 50 characters");
        }

        String userId = measurementRequest.getUserId();
        if (userId == null || userId.trim().isEmpty()) {
            throw new KafkaValidationEx("User ID must not be blank");
        }
        if (userId.length() > 36) {
            throw new KafkaValidationEx("User ID must not exceed 36 characters");
        }

        Double value = measurementRequest.getValue();
        if (value == null) {
            throw new KafkaValidationEx("Value must not be null");
        }

        String unit = measurementRequest.getUnit();
        if (unit == null || unit.trim().isEmpty()) {
            throw new KafkaValidationEx("Unit must not be blank");
        }
    }
}