package ro.upb.iotbridgeservice.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ro.upb.common.constant.KafkaConstants;
import ro.upb.common.dto.MeasurementRequestDto;
import ro.upb.iotbridgeservice.kafka.producer.KafkaMessageProducer;
import ro.upb.iotbridgeservice.service.auth.AuthService;

import static org.apache.hc.core5.http.HttpHeaders.AUTHORIZATION;

@RequestMapping("/v1/iot-bridge")
@RestController
@RequiredArgsConstructor
public class IotBridgeController {

    private final KafkaMessageProducer kafkaMessageProducer;
    private final AuthService authService;

    @PostMapping
    public Mono<ResponseEntity<Void>> sendIotMeasurement(@RequestBody MeasurementRequestDto measurementRequestDto,
                                                         @RequestHeader(AUTHORIZATION) String authorizationHeader) {
        return authService.isAuthorized(measurementRequestDto.getUserId(), authorizationHeader)
                .flatMap(isAuthorized -> {
                    if (Boolean.TRUE.equals(isAuthorized)) {
                        kafkaMessageProducer.sendIotMeasurement(KafkaConstants.IOT_EVENT_TOPIC, measurementRequestDto);
                        return Mono.just(ResponseEntity.accepted().build());
                    } else {
                        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
                    }
                });
    }


}
