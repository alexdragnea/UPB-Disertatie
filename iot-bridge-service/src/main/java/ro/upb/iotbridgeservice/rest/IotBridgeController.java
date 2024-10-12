package ro.upb.iotbridgeservice.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import ro.upb.common.constant.KafkaConstants;
import ro.upb.common.dto.MeasurementRequestDto;
import ro.upb.iotbridgeservice.kafka.producer.KafkaMessageProducer;
import ro.upb.iotbridgeservice.service.auth.AuthService;

import static ro.upb.common.constant.WebConstants.API_KEY_HEADER;

@RequestMapping("/v1/iot-bridge")
@RestController
@RequiredArgsConstructor
public class IotBridgeController {

    private final KafkaMessageProducer kafkaMessageProducer;
    private final AuthService authService;
    private final Scheduler scheduler = Schedulers.newParallel("bridge-scheduler", 4);

    @PostMapping
    public Mono<ResponseEntity<Void>> sendIotMeasurement(@RequestBody MeasurementRequestDto measurementRequestDto, @RequestHeader(API_KEY_HEADER) String apiKey) {
        return authService.isAuthorizedWithApiKey(measurementRequestDto.getUserId(), apiKey)
                .subscribeOn(scheduler) // Use the parallel scheduler for the authorization check
                .flatMap(isAuthorized -> {
                    if (Boolean.TRUE.equals(isAuthorized)) {
                        return Mono.fromRunnable(() -> kafkaMessageProducer.sendIotMeasurement(KafkaConstants.IOT_EVENT_TOPIC, measurementRequestDto))
                                .then(Mono.just(ResponseEntity.accepted().build()));
                    } else {
                        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
                    }
                });
    }
}
