package ro.upb.iotbridgeservice.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ro.upb.common.dto.MeasurementRequestDto;
import ro.upb.iotbridgeservice.exception.KafkaValidationEx;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageProducer {

    private final KafkaTemplate<String, MeasurementRequestDto> kafkaTemplate;

    public void sendIotMeasurement(String topic, MeasurementRequestDto sensor) {
        validateMeasurement(sensor);

        log.info("Sending IotMeasurement: {}, to topic: {}.", sensor, topic);

        CompletableFuture<SendResult<String, MeasurementRequestDto>> future = kafkaTemplate.send(topic, sensor);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Successfully sent message: {} to topic: {} with offset: {}", sensor, topic, result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send message: {} to topic: {} due to: {}", sensor, topic, ex.getMessage());
                throw new KafkaException(ex.getMessage());
            }
        });
    }

    private void validateMeasurement(MeasurementRequestDto measurementRequestDto) {
        if (measurementRequestDto == null) {
            throw new KafkaValidationEx("MeasurementRequestDto cannot be null");
        }

        String measurement = measurementRequestDto.getMeasurement();
        if (measurement == null || measurement.trim().isEmpty()) {
            throw new KafkaValidationEx("Measurement must not be blank");
        }
        if (measurement.length() > 50) {
            throw new KafkaValidationEx("Measurement must not exceed 50 characters");
        }

        String userId = measurementRequestDto.getUserId();
        if (userId == null || userId.trim().isEmpty()) {
            throw new KafkaValidationEx("User ID must not be blank");
        }
        if (userId.length() > 36) {
            throw new KafkaValidationEx("User ID must not exceed 36 characters");
        }

        Double value = measurementRequestDto.getValue();
        if (value == null) {
            throw new KafkaValidationEx("Value must not be null");
        }
        if (value <= 0) {
            throw new KafkaValidationEx("Value must be greater than 0");
        }

        String unit = measurementRequestDto.getUnit();
        if (unit == null || unit.trim().isEmpty()){
            throw new KafkaValidationEx("Unit must not be blank");
        }
    }
}
