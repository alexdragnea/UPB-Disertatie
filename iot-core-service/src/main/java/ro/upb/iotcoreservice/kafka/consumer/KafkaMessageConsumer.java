package ro.upb.iotcoreservice.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ro.upb.common.constant.KafkaConstants;
import ro.upb.common.dto.MeasurementRequestDto;
import ro.upb.iotcoreservice.exception.KafkaProcessingEx;
import ro.upb.iotcoreservice.exception.KafkaValidationEx;
import ro.upb.iotcoreservice.service.IotMeasurementService;

import java.time.Instant;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaMessageConsumer {

    private final IotMeasurementService iotMeasurementService;

    @KafkaListener(topics = KafkaConstants.IOT_EVENT_TOPIC, groupId = KafkaConstants.IOT_GROUP_ID)
    @Transactional
    public void listen(MeasurementRequestDto measurementRequestDto, Acknowledgment acknowledgment) {
        try {
            log.info("Received IotMeasurement {} at {}.", measurementRequestDto, Instant.now());

            validateMeasurement(measurementRequestDto);

            iotMeasurementService.persistIotMeasurement(measurementRequestDto);
            acknowledgment.acknowledge();
        } catch (KafkaValidationEx ex) {
            log.warn("Validation error: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Processing error: {}", ex.getMessage());
            throw new KafkaProcessingEx("Failed to process Kafka message");
        }
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
    }
}
