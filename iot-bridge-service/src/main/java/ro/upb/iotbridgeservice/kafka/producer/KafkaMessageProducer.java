package ro.upb.iotbridgeservice.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ro.upb.common.avro.MeasurementMessage;
import ro.upb.common.dto.MeasurementRequest;
import ro.upb.iotbridgeservice.exception.KafkaProcessingEx;
import ro.upb.iotbridgeservice.exception.KafkaValidationEx;
import ro.upb.iotbridgeservice.metric.KafkaProducerMetric;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageProducer {

    private final KafkaTemplate<String, MeasurementMessage> kafkaTemplate;
    private final KafkaProducerMetric kafkaProducerMetric;

    public Mono<Void> sendIotMeasurement(String topic, MeasurementRequest sensor) {
        validateMeasurement(sensor);

        MeasurementMessage message = buildMessage(sensor);

        kafkaProducerMetric.incrementInFlightMessages();

        CompletableFuture<SendResult<String, MeasurementMessage>> future = kafkaTemplate.send(topic, message);

        return Mono.fromFuture(future)
                .doOnSuccess(result -> {
                    kafkaProducerMetric.decrementInFlightMessages();
                    log.info("Successfully sent message to topic: {} with offset: {}", topic, result.getRecordMetadata().offset());

                    // Compression Ratio Calculation
                    long originalSize = message.toString().length(); // Uncompressed size
                    long compressedSize = result.getRecordMetadata().serializedValueSize();
                    kafkaProducerMetric.recordCompressionRatio(originalSize, compressedSize);
                })
                .doOnError(ex -> {
                    kafkaProducerMetric.decrementInFlightMessages();
                    log.error("Failed to send message to topic: {} due to: {}", topic, ex.getMessage());
                    throw new KafkaProcessingEx(ex.getMessage());
                })
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