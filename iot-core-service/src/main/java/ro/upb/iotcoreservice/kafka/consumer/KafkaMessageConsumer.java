package ro.upb.iotcoreservice.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ro.upb.common.constant.KafkaConstants;
import ro.upb.common.dto.MeasurementRequestDto;
import ro.upb.iotcoreservice.dto.WSMessage;
import ro.upb.iotcoreservice.exception.KafkaProcessingEx;
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
    private final IotCoreWebSocketHandler webSocketHandler; // Inject the WebSocketHandler

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

            // Create WSMessage
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC);
            String timestamp = Instant.now().atOffset(ZoneOffset.UTC).format(formatter);
            WSMessage wsMessage = new WSMessage(measurementRequestDto, timestamp);

            // Serialize the message
            String jsonMessage = objectMapper.writeValueAsString(wsMessage);

            // Broadcast the message to all WebSocket sessions
            webSocketHandler.broadcast(jsonMessage);

        } catch (Exception ex) {
            log.error("Processing error: {}", ex.getMessage());
            throw new KafkaProcessingEx("Failed to process Kafka message");
        }
    }
}
