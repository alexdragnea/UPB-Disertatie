package ro.upb.iotcoreservice.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ro.upb.common.constant.KafkaConstants;
import ro.upb.common.dto.MeasurementDto;
import ro.upb.iotcoreservice.service.IotMeasurementService;

import java.time.Instant;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaMessageConsumer {

    private final IotMeasurementService iotMeasurementService;

    @KafkaListener(topics = KafkaConstants.IOT_EVENT_TOPIC, groupId = KafkaConstants.IOT_GROUP_ID)
    public void listen(MeasurementDto measurementDto) {
        log.info("Received IotMeasurement {} at {}.", measurementDto, Instant.now());
        iotMeasurementService.persistIotMeasurement(measurementDto);
    }
}
