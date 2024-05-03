package ro.upb.iotcoreservice.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ro.upb.iotcoreservice.dto.UserMeasurementDto;
import ro.upb.iotcoreservice.filter.MeasurementFilter;
import ro.upb.iotcoreservice.model.IotMeasurement;
import ro.upb.iotcoreservice.service.IotMeasurementService;
import ro.upb.iotcoreservice.service.MeasurementFilterService;

@RequestMapping("/v1/iot-core")
@RestController
@RequiredArgsConstructor
@Slf4j
public class IotMeasurementController {

    private final IotMeasurementService iotMeasurementService;
    private final MeasurementFilterService measurementFilterService;

    @GetMapping
    public Flux<IotMeasurement> getMeasurementsByFilter(@RequestBody MeasurementFilter filter) {
        return measurementFilterService.filterMeasurements(filter);
    }

    @GetMapping("/measurements")
    public Mono<UserMeasurementDto> getUserMeasurements(@RequestParam String userId) {
        log.info("Getting all IotMeasurements for userId {}.", userId);
        return iotMeasurementService.findUserMeasurements(userId);

    }
}
