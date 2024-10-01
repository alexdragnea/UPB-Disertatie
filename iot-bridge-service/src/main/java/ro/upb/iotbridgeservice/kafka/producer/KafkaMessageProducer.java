package ro.upb.iotbridgeservice.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import ro.upb.common.dto.MeasurementRequestDto;
import ro.upb.iotbridgeservice.exception.KafkaProcessingEx;
import ro.upb.iotbridgeservice.exception.KafkaValidationEx;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageProducer {

    private final KafkaSender<String, MeasurementRequestDto> kafkaSender;

    public Mono<Void> sendIotMeasurement(String topic, MeasurementRequestDto sensor) {
        validateMeasurement(sensor);

        log.info("Sending IotMeasurement: {}, to topic: {}.", sensor, topic);

        return kafkaSender.send(Mono.just(SenderRecord.create(topic, null, null, null, sensor, null)))
                .doOnError(ex -> {
                    log.error("Failed to send message: {} to topic: {} due to: {}", sensor, topic, ex.getMessage());
                    throw new KafkaProcessingEx("Failed to send message to Kafka");
                })
                .doOnNext(result -> log.info("Successfully sent message: {} to topic: {} with offset: {}", sensor, topic, result.recordMetadata().offset()))
                .then();
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

        String unit = measurementRequestDto.getUnit();
        if (unit == null || unit.trim().isEmpty()) {
            throw new KafkaValidationEx("Unit must not be blank");
        }
    }
}