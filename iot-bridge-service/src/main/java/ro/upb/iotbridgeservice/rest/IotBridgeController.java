package ro.upb.iotbridgeservice.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ro.upb.common.constant.KafkaConstants;
import ro.upb.common.dto.MeasurementRequest;
import ro.upb.iotbridgeservice.kafka.producer.KafkaMessageProducer;
import ro.upb.iotbridgeservice.service.auth.AuthService;

import static ro.upb.common.constant.WebConstants.API_KEY_HEADER;

@RequestMapping("/v1/iot-bridge")
@RestController
@RequiredArgsConstructor
@Slf4j
public class IotBridgeController {

    private final KafkaMessageProducer kafkaMessageProducer;
    private final AuthService authService;

    @PostMapping
    public Mono<ResponseEntity<Void>> sendIotMeasurement(@RequestBody MeasurementRequest measurementRequest, @RequestHeader(API_KEY_HEADER) String apiKey) {
        return authService.isAuthorizedWithApiKey(measurementRequest.getUserId(), apiKey).flatMap(isAuthorized -> {
            if (Boolean.TRUE.equals(isAuthorized)) {
                return kafkaMessageProducer.sendIotMeasurement(KafkaConstants.IOT_EVENT_TOPIC, measurementRequest)
                        .then(Mono.just(ResponseEntity.accepted().<Void>build()))
                        .onErrorResume(ex -> {
                            log.error("Failed to send IoT measurement: {}", ex.getMessage());
                            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                        });
            } else {
                return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
            }
        });
    }
}