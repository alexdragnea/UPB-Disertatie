package ro.upb.iotbridgeservice.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ro.upb.common.dto.IotRequestDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageProducer {

    private final KafkaTemplate<String, IotRequestDto> kafkaTemplate;

    public void sendMessage(String topic, IotRequestDto sensor) {
        log.info("Sending message: {}, to topic: {}", sensor, topic);
        kafkaTemplate.send(topic, sensor);
    }
}
