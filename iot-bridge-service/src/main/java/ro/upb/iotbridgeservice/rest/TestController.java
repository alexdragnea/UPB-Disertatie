package ro.upb.iotbridgeservice.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ro.upb.common.constant.KafkaConstants;
import ro.upb.iotbridgeservice.kafka.producer.KafkaMessageProducer;

@RequestMapping("/v1/iot-bridge")
@RestController
@RequiredArgsConstructor
public class TestController {

    private final KafkaMessageProducer kafkaMessageProducer;

    @PostMapping
    public void sendMessage(@RequestBody String message) {
        kafkaMessageProducer.sendMessage(KafkaConstants.IOT_EVENT_TOPIC, message);
    }
}
