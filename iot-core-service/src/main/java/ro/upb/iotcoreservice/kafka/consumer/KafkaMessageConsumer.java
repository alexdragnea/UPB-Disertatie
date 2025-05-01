package ro.upb.iotcoreservice.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsoniter.output.JsonStream;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;
import ro.upb.common.avro.MeasurementMessage;
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

    private final KafkaReceiver<String, MeasurementMessage> kafkaReceiver;
    private final IotMeasurementService iotMeasurementService;
    private final IotCoreWebSocketHandler webSocketHandler;
    private final DeduplicationService deduplicationService;
    private final KafkaConsumerMetric kafkaConsumerMetric;

    @PostConstruct
    public void startConsuming() {
        consumeMessages();
    }

    public void consumeMessages() {
        kafkaReceiver.receive()
                .flatMap(record -> processMessage(record)
                        .doOnSuccess(unused -> record.receiverOffset().acknowledge())
                        .onErrorResume(error -> {
                            log.error("Error processing message: {}", error.getMessage());
                            return Mono.empty();
                        }))
                .subscribe();
    }

    private Mono<Void> processMessage(ReceiverRecord<String, MeasurementMessage> record) {
        MeasurementMessage message = record.value();
        String id = String.valueOf(message.getId());

        return deduplicationService.isMessageDuplicate(id)
                .flatMap(isDuplicate -> {
                    if (isDuplicate) {
                        log.info("Message with id {} is a duplicate, skipping.", id);
                        kafkaConsumerMetric.incrementDuplicateMessages();
                        return Mono.empty();
                    }

                    return deduplicationService.markMessageAsProcessed(id)
                            .then(iotMeasurementService.persistIotMeasurement(message))
                            .then(broadcastMessage(message));
                });
    }

    private Mono<Void> broadcastMessage(MeasurementMessage message) {
        WSMessage wsMessage = new WSMessage(
                message.getMeasurement().toString(),
                message.getValue(),
                Instant.now().toString()
        );

        return Mono.fromCallable(() -> JsonStream.serialize(wsMessage)) // Use Jsoniter for serialization
                .subscribeOn(Schedulers.boundedElastic()) // Offload blocking serialization
                .flatMap(webSocketHandler::broadcast);
    }
}