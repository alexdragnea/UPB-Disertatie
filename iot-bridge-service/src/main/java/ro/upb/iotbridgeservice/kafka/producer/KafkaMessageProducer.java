package ro.upb.iotbridgeservice.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ro.upb.common.dto.MeasurementDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageProducer {

    private final KafkaTemplate<String, MeasurementDto> kafkaTemplate;

    public void sendIotMeasurement(String topic, MeasurementDto sensor) {
        log.info("Sending IotMeasurement: {}, to topic: {}.", sensor, topic);
        kafkaTemplate.send(topic, sensor);
    }
}
