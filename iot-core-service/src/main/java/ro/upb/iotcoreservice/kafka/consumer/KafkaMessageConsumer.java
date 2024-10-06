package ro.upb.iotcoreservice.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Sinks;
import ro.upb.common.constant.KafkaConstants;
import ro.upb.common.dto.MeasurementRequestDto;
import ro.upb.iotcoreservice.dto.WSMessage;
import ro.upb.iotcoreservice.exception.KafkaProcessingEx;
import ro.upb.iotcoreservice.service.core.IotMeasurementService;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaMessageConsumer {

    private final IotMeasurementService iotMeasurementService;
    private final Sinks.Many<String> sink;
    private final ObjectMapper objectMapper; // Add ObjectMapper for JSON serialization

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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC);
            String timestamp = Instant.now().atOffset(ZoneOffset.UTC).format(formatter);

            WSMessage wsMessage = new WSMessage(measurementRequestDto, timestamp);
            String jsonMessage = objectMapper.writeValueAsString(wsMessage);
            Sinks.EmitResult emitResult = sink.tryEmitNext(jsonMessage);
            log.info("Kafka message emit result status: " + emitResult.name() + " " + emitResult.isSuccess());
        } catch (Exception ex) {
            log.error("Processing error: {}", ex.getMessage());
            throw new KafkaProcessingEx("Failed to process Kafka message");
        }
    }
}