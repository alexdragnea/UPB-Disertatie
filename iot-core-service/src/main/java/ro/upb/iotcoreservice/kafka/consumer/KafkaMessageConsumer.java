package ro.upb.iotcoreservice.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ro.upb.common.constant.KafkaConstants;
import ro.upb.common.dto.MeasurementRequest;
import ro.upb.iotcoreservice.dto.WSMessage;
import ro.upb.iotcoreservice.service.core.DeduplicationService;
import ro.upb.iotcoreservice.service.core.IotMeasurementService;
import ro.upb.iotcoreservice.websocket.IotCoreWebSocketHandler;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

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
    public void listen(@Payload MeasurementRequest measurementRequest, ConsumerRecord<String, MeasurementRequest> record, Acknowledgment acknowledgment) {
        // Extract the deduplicationKey from Kafka headers
        String deduplicationKey = getDeduplicationKey(record);
        if (deduplicationKey == null) {
            log.warn("No deduplication key found in message headers. Skipping message.");
            acknowledgment.acknowledge();
            return;
        }

        // Use DeduplicationService to check if the message has been processed already
        deduplicationService.isMessageDuplicate(deduplicationKey)
                .flatMap(isDuplicate -> {
                    if (isDuplicate) {
                        log.info("Message with deduplicationKey {} is a duplicate, skipping.", deduplicationKey);
                        return Mono.fromRunnable(acknowledgment::acknowledge);
                    }

                    return deduplicationService.markMessageAsProcessed(deduplicationKey)
                            .then(Mono.fromCallable(() -> {
                                log.info("Received IotMeasurement {} at {}.", measurementRequest, Instant.now());
                                iotMeasurementService.persistIotMeasurement(measurementRequest);

                                // Broadcast the message over WebSocket
                                WSMessage wsMessage = new WSMessage(measurementRequest, Instant.now().toString());
                                webSocketHandler.broadcast(objectMapper.writeValueAsString(wsMessage));

                                return acknowledgment;
                            }));
                })
                .doOnSuccess(Acknowledgment::acknowledge)
                .subscribe();
    }

    private String getDeduplicationKey(ConsumerRecord<String, MeasurementRequest> record) {
        return record.headers()
                .lastHeader("deduplicationKey") != null
                ? new String(record.headers().lastHeader("deduplicationKey").value(), StandardCharsets.UTF_8)
                : null;
    }
}



