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
    public void listen(@Payload MeasurementRequest measurementRequest, Acknowledgment acknowledgment) {
        String id = measurementRequest.getId();

        // Use id to check if the message has been processed already
        deduplicationService.isMessageDuplicate(id)
                .flatMap(isDuplicate -> {
                    if (isDuplicate) {
                        log.info("Message with id {} is a duplicate, skipping.", id);
                        return Mono.fromRunnable(acknowledgment::acknowledge);
                    }

                    return deduplicationService.markMessageAsProcessed(id)
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
}



