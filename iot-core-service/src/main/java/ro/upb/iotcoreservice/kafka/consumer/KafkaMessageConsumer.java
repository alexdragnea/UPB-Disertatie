package ro.upb.iotcoreservice.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ro.upb.common.avro.MeasurementMessage;
import ro.upb.common.constant.KafkaConstants;
import ro.upb.iotcoreservice.dto.WSMessage;
import ro.upb.iotcoreservice.service.core.DeduplicationService;
import ro.upb.iotcoreservice.service.core.IotMeasurementService;
import ro.upb.iotcoreservice.websocket.IotCoreWebSocketHandler;

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
    public void listen(@Payload MeasurementMessage message, Acknowledgment acknowledgment) {
        String id = String.valueOf(message.getId());

        // Use id to check if the message has been processed already
        deduplicationService.isMessageDuplicate(id)
                .flatMap(isDuplicate -> {
                    if (isDuplicate) {
                        log.info("Message with id {} is a duplicate, skipping.", id);
                        return Mono.fromRunnable(acknowledgment::acknowledge);
                    }

                    return deduplicationService.markMessageAsProcessed(id)
                            .then(Mono.fromCallable(() -> {
                                log.info("Received IotMeasurement {} at {}.", message, Instant.now());
                                iotMeasurementService.persistIotMeasurement(message);
                                return message;
                            }));
                })
                .flatMap(msg -> {
                    // Broadcast the message over WebSocket asynchronously
                    WSMessage wsMessage = new WSMessage(msg.getMeasurement().toString(), msg.getValue(), Instant.now().toString());
                    return Mono.fromRunnable(() -> {
                        try {
                            webSocketHandler.broadcast(objectMapper.writeValueAsString(wsMessage));
                        } catch (Exception e) {
                            log.error("Failed to broadcast message: {}", e.getMessage());
                        }
                    }).thenReturn(acknowledgment);
                })
                .doOnSuccess(Acknowledgment::acknowledge)
                .subscribe();
    }
}