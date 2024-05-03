package ro.upb.iotbridgeservice.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ro.upb.common.dto.MeasurementRequestDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageProducer {

    private final KafkaTemplate<String, MeasurementRequestDto> kafkaTemplate;

    public void sendIotMeasurement(String topic, MeasurementRequestDto sensor) {
        log.info("Sending IotMeasurement: {}, to topic: {}.", sensor, topic);
        kafkaTemplate.send(topic, sensor);
    }
}
