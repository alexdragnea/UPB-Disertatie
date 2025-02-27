package ro.upb.iotcoreservice.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ro.upb.common.constant.KafkaConstants;
import ro.upb.common.dto.MeasurementRequest;
import ro.upb.iotcoreservice.dto.WSMessage;
import ro.upb.iotcoreservice.exception.KafkaProcessingEx;
import ro.upb.iotcoreservice.service.core.DeduplicationService;
import ro.upb.iotcoreservice.service.core.IotMeasurementService;
import ro.upb.iotcoreservice.websocket.IotCoreWebSocketHandler;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaMessageConsumer {

    private final IotMeasurementService iotMeasurementService;
    private final ObjectMapper objectMapper;
    private final IotCoreWebSocketHandler webSocketHandler;
    private final DeduplicationService deduplicationService;

    @KafkaListener(topics = KafkaConstants.IOT_EVENT_TOPIC, groupId = KafkaConstants.IOT_GROUP_ID)
    @RetryableTopic(
            kafkaTemplate = "kafkaTemplate",
            backoff = @Backoff(delay = 3000, maxDelay = 15000)
    )
    @Transactional
    public void listen(MeasurementRequest measurementRequest, Acknowledgment acknowledgment) {
        String messageId = measurementRequest.getUuid().toString();

        // Use DeduplicationService to check if the message has been processed already
        deduplicationService.isMessageDuplicate(messageId)
                .flatMap(isDuplicate -> {
                    if (isDuplicate) {
                        log.info("Message with ID {} is a duplicate, skipping.", messageId);
                        acknowledgment.acknowledge();  // Acknowledge the message without processing further
                        return Mono.empty();
                    }

                    try {
                        // Process the message
                        log.info("Received IotMeasurement {} at {}.", measurementRequest, Instant.now());
                        iotMeasurementService.persistIotMeasurement(measurementRequest);

                        // Create WebSocket message and send it
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC);
                        String timestamp = Instant.now().atOffset(ZoneOffset.UTC).format(formatter);
                        WSMessage wsMessage = new WSMessage(measurementRequest, timestamp);

                        String jsonMessage = objectMapper.writeValueAsString(wsMessage);
                        webSocketHandler.broadcast(jsonMessage);

                        // Mark the message as processed and store it in Redis with TTL
                        return deduplicationService.markMessageAsProcessed(messageId)
                                .then(Mono.just(acknowledgment));
                    } catch (Exception ex) {
                        log.error("Processing error: {}", ex.getMessage());
                        acknowledgment.acknowledge();  // Acknowledge the message so it's not retried again
                        return Mono.error(new KafkaProcessingEx("Failed to process Kafka message"));
                    }
                })
                .doOnTerminate(acknowledgment::acknowledge)  // Acknowledge the message after processing completes
                .subscribe();

    }

}
