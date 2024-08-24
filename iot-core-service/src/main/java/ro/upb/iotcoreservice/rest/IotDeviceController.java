package ro.upb.iotcoreservice.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ro.upb.common.dto.DeviceRequestDto;
import ro.upb.iotcoreservice.exception.DeviceAlreadyExistsEx;
import ro.upb.iotcoreservice.service.IotDeviceService;
import ro.upb.iotcoreservice.service.auth.AuthService;

import static org.apache.hc.core5.http.HttpHeaders.AUTHORIZATION;

@RequestMapping("/v1/iot-core")
@RestController
@RequiredArgsConstructor
@Slf4j
public class IotDeviceController {

    private final IotDeviceService iotDeviceService;
    private final AuthService authService;


    @PostMapping("/device")
    public Mono<ResponseEntity<Void>> registerDevice(@RequestBody DeviceRequestDto deviceRequestDto, @RequestHeader(AUTHORIZATION) String authorizationHeader) {
        log.info("Registering device: {}.", deviceRequestDto);

        return authService.isAuthorized(deviceRequestDto.getUserId(), authorizationHeader)
                .flatMap(isAuthorized -> {
                    if (Boolean.TRUE.equals(isAuthorized)) {
                        return iotDeviceService.registerDevice(deviceRequestDto)
                                .then(Mono.just(ResponseEntity.status(HttpStatus.ACCEPTED).<Void>build()))
                                .onErrorResume(DeviceAlreadyExistsEx.class, ex -> {
                                    log.error("Device registration failed: {}", ex.getMessage());
                                    return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build());
                                });
                    } else {
                        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
                    }
                });
    }


}
