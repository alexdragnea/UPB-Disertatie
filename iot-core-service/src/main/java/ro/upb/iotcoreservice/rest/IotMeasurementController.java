package ro.upb.iotcoreservice.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ro.upb.iotcoreservice.service.auth.AuthService;
import ro.upb.iotcoreservice.domain.UserMeasurementDto;
import ro.upb.iotcoreservice.exception.MeasurementNotFoundEx;
import ro.upb.iotcoreservice.domain.MeasurementFilter;
import ro.upb.iotcoreservice.model.IotMeasurement;
import ro.upb.iotcoreservice.service.IotMeasurementService;
import ro.upb.iotcoreservice.service.MeasurementFilterService;

import static org.apache.hc.core5.http.HttpHeaders.AUTHORIZATION;

@RequestMapping("/v1/iot-core")
@RestController
@RequiredArgsConstructor
@Slf4j
public class IotMeasurementController {

    private final IotMeasurementService iotMeasurementService;
    private final MeasurementFilterService measurementFilterService;
    private final AuthService authService;

    @GetMapping("/measurements-by-filter")
    public Mono<Flux<IotMeasurement>> getMeasurementsByFilter(@RequestBody MeasurementFilter filter, @RequestHeader(AUTHORIZATION) String authorizationHeader) {
        return authService.isAuthorized(filter.getUserId(), authorizationHeader)
                .flatMap(isAuthorized -> {
                    if (Boolean.TRUE.equals(isAuthorized)) {
                        return Mono.just(measurementFilterService.filterMeasurements(filter)
                                .onErrorResume(MeasurementNotFoundEx.class, ex -> {
                                    log.warn("Exception occurred: {}.", ex.getMessage());
                                    return Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex));
                                }));
                    } else {
                        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
                    }
                });
    }

    @GetMapping("/measurements")
    public Mono<UserMeasurementDto> getUserMeasurements(@RequestParam String userId) {
        log.info("Getting all IotMeasurements for userId {}.", userId);
        return iotMeasurementService.findUserMeasurements(userId)
                .onErrorResume(MeasurementNotFoundEx.class, ex -> {
                    log.warn("Exception occurred: {}.", ex.getMessage());
                    return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex));
                });
    }

}
