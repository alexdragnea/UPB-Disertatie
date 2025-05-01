package ro.upb.iotcoreservice.kafka.consumer;

import com.jsoniter.output.JsonStream;
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
import ro.upb.iotcoreservice.metrics.KafkaConsumerMetric;
import ro.upb.iotcoreservice.service.core.DeduplicationService;
import ro.upb.iotcoreservice.service.core.IotMeasurementService;
import ro.upb.iotcoreservice.websocket.IotCoreWebSocketHandler;

import java.time.Instant;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaMessageConsumer {

    private final IotMeasurementService iotMeasurementService;
    private final IotCoreWebSocketHandler webSocketHandler;
    private final DeduplicationService deduplicationService;
    private final KafkaConsumerMetric kafkaConsumerMetric;

    @KafkaListener(topics = KafkaConstants.IOT_EVENT_TOPIC, groupId = KafkaConstants.IOT_GROUP_ID)
    @RetryableTopic(
            kafkaTemplate = "kafkaTemplate",
            backoff = @Backoff(delay = 3000, maxDelay = 15000)
    )
    public void listen(@Payload MeasurementMessage message, Acknowledgment acknowledgment) {
        String id = String.valueOf(message.getId());

        deduplicationService.isMessageDuplicate(id)
                .flatMap(isDuplicate -> {
                    if (isDuplicate) {
                        log.info("Message with id {} is a duplicate, skipping.", id);
                        kafkaConsumerMetric.incrementDuplicateMessages();
                        return Mono.empty();
                    }

                    return deduplicationService.markMessageAsProcessed(id)
                            .then(iotMeasurementService.persistIotMeasurementReactive(message))
                            .doOnSuccess(success -> log.info("Persisted measurement: {}", message));
                })
                .then(Mono.defer(() -> {
                    WSMessage wsMessage = new WSMessage(
                            message.getMeasurement().toString(),
                            message.getValue(),
                            Instant.now().toString()
                    );
                    return Mono.fromCallable(() -> JsonStream.serialize(wsMessage))
                            .flatMap(webSocketHandler::broadcastReactive)
                            .onErrorResume(e -> {
                                log.error("Failed to broadcast message: {}", e.getMessage());
                                return Mono.empty();
                            });
                }))
                .doOnTerminate(acknowledgment::acknowledge)
                .subscribe();
    }
}