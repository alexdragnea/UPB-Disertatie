package ro.upb.iotcoreservice.kafka.consumer;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;
import ro.upb.common.constant.KafkaConstants;
import ro.upb.common.dto.MeasurementRequestDto;
import ro.upb.iotcoreservice.exception.KafkaProcessingEx;
import ro.upb.iotcoreservice.service.core.IotMeasurementService;

import java.time.Instant;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaMessageConsumer {

    private final IotMeasurementService iotMeasurementService;
    private final KafkaReceiver<String, MeasurementRequestDto> kafkaReceiver;

    @PostConstruct
    @KafkaListener(topics = KafkaConstants.IOT_EVENT_TOPIC, groupId = KafkaConstants.IOT_GROUP_ID)
    public void consumeMessages() {
        Flux<ReceiverRecord<String, MeasurementRequestDto>> kafkaFlux = kafkaReceiver.receive();

        kafkaFlux.doOnNext(record -> {
            try {
                log.info("Received IotMeasurement {} at {}.", record.value(), Instant.now());
                iotMeasurementService.persistIotMeasurement(record.value());
                record.receiverOffset().acknowledge();
            } catch (Exception ex) {
                log.error("Processing error: {}", ex.getMessage());
                throw new KafkaProcessingEx("Failed to process Kafka message");
            }
        }).onErrorContinue((throwable, o) -> log.error("Error processing record: {}", throwable.getMessage())).subscribe();
    }
}