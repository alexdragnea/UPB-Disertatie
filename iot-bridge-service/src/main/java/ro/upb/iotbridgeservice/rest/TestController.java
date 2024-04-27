package ro.upb.iotbridgeservice.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.upb.iotbridgeservice.kafka.producer.KafkaMessageProducer;

@RequestMapping("/send")
@RestController
@RequiredArgsConstructor
public class TestController {

    private final KafkaMessageProducer kafkaMessageProducer;

    @GetMapping
    public void send(){
        kafkaMessageProducer.sendMessage("iot_events", "test");
    }
}
