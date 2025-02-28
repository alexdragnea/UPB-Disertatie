package ro.upb.iotbridgeservice.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ro.upb.common.dto.MeasurementRequest;
import ro.upb.iotbridgeservice.exception.KafkaProcessingEx;
import ro.upb.iotbridgeservice.exception.KafkaValidationEx;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageProducer {

    private final KafkaTemplate<String, MeasurementRequest> kafkaTemplate;

    public void sendIotMeasurement(String topic, MeasurementRequest sensor) {
        validateMeasurement(sensor);

        String deduplicationKey = UUID.randomUUID().toString();

        log.info("Sending Measurement: {}, to topic: {}.", sensor, topic);

        ProducerRecord<String, MeasurementRequest> record = new ProducerRecord<>(topic, sensor.getUserId(), sensor);

        record.headers().add(new RecordHeader("deduplicationKey", deduplicationKey.getBytes(StandardCharsets.UTF_8)));

        CompletableFuture<SendResult<String, MeasurementRequest>> future = kafkaTemplate.send(record);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Successfully sent message: {} to topic: {} with offset: {}", sensor, topic, result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send message: {} to topic: {} due to: {}", sensor, topic, ex.getMessage());
                throw new KafkaProcessingEx(ex.getMessage());
            }
        });
    }

    private void validateMeasurement(MeasurementRequest measurementRequest) {
        if (measurementRequest == null) {
            throw new KafkaValidationEx("MeasurementRequestDto cannot be null");
        }

        String measurement = measurementRequest.getMeasurement();
        if (measurement == null || measurement.trim().isEmpty()) {
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
        if (unit == null || unit.trim().isEmpty()){
            throw new KafkaValidationEx("Unit must not be blank");
        }
    }
}
