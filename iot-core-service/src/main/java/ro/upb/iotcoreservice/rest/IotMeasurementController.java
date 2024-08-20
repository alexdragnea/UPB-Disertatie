package ro.upb.iotcoreservice.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ro.upb.common.constant.ExMessageConstants;
import ro.upb.common.errorhandling.UnauthorizedException;
import ro.upb.iotcoreservice.domain.MeasurementFilter;
import ro.upb.iotcoreservice.domain.UserMeasurementDto;
import ro.upb.iotcoreservice.exception.MeasurementNotFoundEx;
import ro.upb.iotcoreservice.model.IotMeasurement;
import ro.upb.iotcoreservice.service.IotMeasurementService;
import ro.upb.iotcoreservice.service.MeasurementFilterService;
import ro.upb.iotcoreservice.service.auth.AuthService;

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
    public Flux<IotMeasurement> getMeasurementsByFilter(@RequestBody MeasurementFilter filter, @RequestHeader(AUTHORIZATION) String authorizationHeader) {
        return authService.isAuthorized(filter.getUserId(), authorizationHeader)
                .flatMapMany(isAuthorized -> {
                    if (Boolean.TRUE.equals(isAuthorized)) {
                        return measurementFilterService.filterMeasurements(filter)
                                .onErrorResume(MeasurementNotFoundEx.class, ex -> {
                                    log.warn("Exception occurred: {}.", ex.getMessage());
                                    return Flux.error(new MeasurementNotFoundEx(String.format(ExMessageConstants.MEASUREMENT_NOT_FOUND_EX, filter)));
                                });
                    } else {
                        return Flux.error(new UnauthorizedException("User is not authorized."));
                    }
                });

    }

    @GetMapping("/measurements")
    public Mono<UserMeasurementDto> getUserMeasurements(@RequestParam String userId) {
        log.info("Getting all IotMeasurements for userId {}.", userId);
        return iotMeasurementService.findUserMeasurements(userId)
                .onErrorResume(MeasurementNotFoundEx.class, ex -> {
                    log.warn("Exception occurred: {}.", ex.getMessage());
                    return Mono.error(new MeasurementNotFoundEx(String.format(ExMessageConstants.MEASUREMENT_NOT_FOUND_EX_FOR_USERID, userId)));
                });
    }

}
