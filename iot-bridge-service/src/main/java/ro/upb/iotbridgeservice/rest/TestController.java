package ro.upb.iotbridgeservice.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.upb.iotbridgeservice.kafka.producer.KafkaMessageProducer;

@RequestMapping("/send")
@RestController
public class TestController {

    private KafkaMessageProducer kafkaMessageProducer;

    @GetMapping
    public void send(){
        kafkaMessageProducer.sendMessage("iot_events", "test");
    }
}
