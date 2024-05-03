package ro.upb.iotcoreservice.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ro.upb.iotcoreservice.dto.UserMeasurementDto;
import ro.upb.iotcoreservice.model.IotMeasurement;
import ro.upb.iotcoreservice.service.IotMeasurementService;

@RequestMapping("/v1/iot-core")
@RestController
@RequiredArgsConstructor
@Slf4j
public class IotMeasurementController {

    private final IotMeasurementService iotMeasurementService;

    @GetMapping
    public Flux<IotMeasurement> getMeasurements(@RequestParam(required = false) String userId, @RequestParam(required = false) String measurement) {
        if (userId != null && measurement != null) {
            log.info("Getting all IotMeasurements for userId: {} and measurement: {}.", userId, measurement);
            return iotMeasurementService.findAllByUserIdAndMeasurement(userId, measurement);
        } else {
            log.info("Getting all IotMeasurements.");
            return iotMeasurementService.findAll();
        }
    }

    @GetMapping("/measurements")
    public Mono<UserMeasurementDto> getUserMeasurements(@RequestParam String userId){
        log.info("Getting all IotMeasurements.");
        return iotMeasurementService.findUserMeasurements(userId);

    }
}
