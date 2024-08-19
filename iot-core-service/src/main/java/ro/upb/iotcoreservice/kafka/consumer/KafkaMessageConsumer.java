package ro.upb.iotcoreservice.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ro.upb.common.constant.KafkaConstants;
import ro.upb.common.dto.MeasurementRequestDto;
import ro.upb.iotcoreservice.exception.KafkaProcessingEx;
import ro.upb.iotcoreservice.service.IotMeasurementService;

import java.time.Instant;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaMessageConsumer {

    private final IotMeasurementService iotMeasurementService;

    @KafkaListener(topics = KafkaConstants.IOT_EVENT_TOPIC, groupId = KafkaConstants.IOT_GROUP_ID)
    @RetryableTopic(
            kafkaTemplate = "kafkaTemplate",
            backoff = @Backoff(delay = 3000, maxDelay = 15000)
    )
    @Transactional
    public void listen(MeasurementRequestDto measurementRequestDto) {
        try {
            log.info("Received IotMeasurement {} at {}.", measurementRequestDto, Instant.now());
            iotMeasurementService.persistIotMeasurement(measurementRequestDto);
        } catch (Exception ex) {
            log.error("Processing error: {}", ex.getMessage());
            throw new KafkaProcessingEx("Failed to process Kafka message");
        }
    }
}
