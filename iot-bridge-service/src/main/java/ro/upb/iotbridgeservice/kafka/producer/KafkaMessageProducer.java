package ro.upb.iotbridgeservice.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ro.upb.common.dto.MeasurementRequestDto;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageProducer {

    private final KafkaTemplate<String, MeasurementRequestDto> kafkaTemplate;

    public void sendIotMeasurement(String topic, MeasurementRequestDto sensor) {
        log.info("Sending IotMeasurement: {}, to topic: {}.", sensor, topic);

        CompletableFuture<SendResult<String, MeasurementRequestDto>> future = kafkaTemplate.send(topic, sensor);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Successfully sent message: {} to topic: {} with offset: {}", sensor, topic, result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send message: {} to topic: {} due to: {}", sensor, topic, ex.getMessage());
            }
        });
    }
}
