package ro.upb.iotcoreservice.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ro.upb.iotcoreservice.dto.UserMeasurementDto;
import ro.upb.iotcoreservice.exception.MeasurementNotFoundEx;
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
    public Mono<ResponseEntity<Flux<IotMeasurement>>> getMeasurementsByFilter(@RequestBody MeasurementFilter filter) {
        return Mono.just(filter)
                .flatMapMany(measurementFilterService::filterMeasurements)
                .collectList()
                .map(measurements -> ResponseEntity.ok().body(Flux.fromIterable(measurements)))
                .onErrorResume(MeasurementNotFoundEx.class, ex -> {
                    log.warn("Exception occurred {}.", ex.getMessage());
                    return Mono.just(ResponseEntity.notFound().build());
                });
    }

    @GetMapping("/measurements")
    public Mono<UserMeasurementDto> getUserMeasurements(@RequestParam String userId) {
        log.info("Getting all IotMeasurements for userId {}.", userId);
        return iotMeasurementService.findUserMeasurements(userId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No measurements found for the provided userId", null)))
                .onErrorResume(MeasurementNotFoundEx.class, ex -> {
                    log.warn("Exception occurred {}.", ex.getMessage());
                    return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex));
                });
    }

}
