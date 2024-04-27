package ro.upb.iotbridgeservice.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.upb.iotbridgeservice.kafka.producer.KafkaMessageProducer;
import ro.upb.iotbridgeservice.kafka.utils.KafkaConstants;

@RequestMapping("/send")
@RestController
@RequiredArgsConstructor
public class TestController {

    private final KafkaMessageProducer kafkaMessageProducer;

    @GetMapping
    public void sendMessage(@RequestBody String message) {
        kafkaMessageProducer.sendMessage(KafkaConstants.IOT_EVENT_TOPIC, message);
    }
}
