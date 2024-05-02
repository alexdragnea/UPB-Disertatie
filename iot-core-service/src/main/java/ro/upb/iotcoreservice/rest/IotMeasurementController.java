package ro.upb.iotcoreservice.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ro.upb.iotcoreservice.model.IotMeasurement;
import ro.upb.iotcoreservice.service.IotMeasurementService;

@RequestMapping("/v1/iot-core")
@RestController
@RequiredArgsConstructor
@Slf4j
public class IotMeasurementController {

    private final IotMeasurementService iotMeasurementService;

    @GetMapping("/{userId}/{measurement}")
    public Flux<IotMeasurement> getMeasurementsByUserId(@PathVariable int userId, @PathVariable String measurement) {
        log.info("Getting all IotMeasurements for userId: {} and measurement: {}.", userId, measurement);
        return iotMeasurementService.findAllByUserIdAndMeasurement(userId, measurement);
    }

    @GetMapping("/all")
    public Flux<IotMeasurement> getAllMeasurements() {
        log.info("Getting all IotMeasurements.");
        return iotMeasurementService.findAll();
    }
}
