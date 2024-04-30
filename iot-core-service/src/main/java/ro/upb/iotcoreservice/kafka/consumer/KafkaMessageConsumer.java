package ro.upb.iotcoreservice.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ro.upb.common.constant.KafkaConstants;
import ro.upb.iotcoreservice.db.service.InfluxDbService;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaMessageConsumer {
    private final InfluxDbService influxDbService;

    @KafkaListener(topics = KafkaConstants.IOT_EVENT_TOPIC, groupId = KafkaConstants.IOT_GROUP_ID)
    public void listen(String message) {
        log.info("Received message: {}.", message);
        influxDbService.writeData();
    }
}
