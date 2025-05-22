package ro.upb.iotcoreservice.kafka.consumer;

import com.jsoniter.output.JsonStream;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaMessageConsumer {

    private final KafkaReceiver<String, MeasurementMessage> kafkaReceiver;
    private final IotMeasurementService iotMeasurementService;
    private final IotCoreWebSocketHandler webSocketHandler;
    private final DeduplicationService deduplicationService;
    private final KafkaConsumerMetric kafkaConsumerMetric;

    // Buffered list for batched WebSocket transmission
    private final List<WSMessage> wsBuffer = Collections.synchronizedList(new ArrayList<>());

    @PostConstruct
    public void startConsuming() {
        consumeMessages();
        initWebSocketFlush();
    }

    public void consumeMessages() {
        kafkaReceiver.receive()
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(record ->
                        processMessage(record)
                                .doOnSuccess(unused -> record.receiverOffset().acknowledge())
                                .onErrorResume(error -> {
                                    log.error("Error processing message: {}", error.getMessage());
                                    return Mono.empty();
                                })
                )
                .sequential()
                .subscribe();
    }

    private Mono<Void> processMessage(ReceiverRecord<String, MeasurementMessage> record) {
        String id = String.valueOf(record.value().getId());

        return deduplicationService.isMessageDuplicate(id)
                .flatMap(isDuplicate -> {
                    if (isDuplicate) {
                        log.info("Message with id {} is a duplicate, skipping.", id);
                        kafkaConsumerMetric.incrementDuplicateMessages();
                        return Mono.empty();
                    }

                    // Defer parsing + processing until confirmed not duplicate
                    return Mono.defer(() -> {
                        MeasurementMessage message = record.value();

                        return deduplicationService.markMessageAsProcessed(id)
                                .then(iotMeasurementService.persistIotMeasurement(message))
                                .then(bufferWebSocketMessage(message));
                    });
                });
    }

    private Mono<Void> bufferWebSocketMessage(MeasurementMessage message) {
        WSMessage wsMessage = new WSMessage(
                message.getMeasurement().toString(),
                message.getValue(),
                Instant.now().toString()
        );

        wsBuffer.add(wsMessage);
        return Mono.empty();
    }

    private void initWebSocketFlush() {
        Flux.interval(java.time.Duration.ofMillis(100))
                .flatMap(tick -> flushWebSocketBuffer())
                .subscribe();
    }

    private Mono<Void> flushWebSocketBuffer() {
        List<WSMessage> batch;
        synchronized (wsBuffer) {
            if (wsBuffer.isEmpty()) return Mono.empty();
            batch = new ArrayList<>(wsBuffer);
            wsBuffer.clear();
        }

        return Mono.fromCallable(() -> JsonStream.serialize(batch))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(webSocketHandler::broadcast); // Assumes broadcast(String)
    }
}
