package ro.upb.iotcoreservice.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ro.upb.iotcoreservice.kafka.utils.KafkaConstants;

@Component
@Slf4j
public class KafkaMessageConsumer {

    @KafkaListener(topics = KafkaConstants.IOT_EVENT_TOPIC)
    public void listen(String message) {
        log.info("Received message: {}, on topic: {}", message, KafkaConstants.IOT_EVENT_TOPIC);
    }
}
