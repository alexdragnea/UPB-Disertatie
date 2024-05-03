package ro.upb.iotbridgeservice.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.upb.common.constant.KafkaConstants;
import ro.upb.common.dto.MeasurementRequestDto;
import ro.upb.iotbridgeservice.kafka.producer.KafkaMessageProducer;

@RequestMapping("/v1/iot-bridge")
@RestController
@RequiredArgsConstructor
public class IotBridgeController {

    private final KafkaMessageProducer kafkaMessageProducer;

    @PostMapping
    public void sendIotMeasurement(@RequestBody MeasurementRequestDto measurementRequestDto) {
        kafkaMessageProducer.sendIotMeasurement(KafkaConstants.IOT_EVENT_TOPIC, measurementRequestDto);
    }
}
