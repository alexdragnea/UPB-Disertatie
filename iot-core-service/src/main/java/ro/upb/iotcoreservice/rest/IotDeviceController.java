package ro.upb.iotcoreservice.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ro.upb.common.dto.DeviceRequestDto;
import ro.upb.iotcoreservice.model.IotDevice;
import ro.upb.iotcoreservice.service.device.IotDeviceService;

@RequestMapping("/v1/iot-core/device")
@RestController
@RequiredArgsConstructor
@Slf4j
public class IotDeviceController {

    private final IotDeviceService iotDeviceService;

    @PostMapping
    public Mono<ResponseEntity<IotDevice>> addDevice(@RequestBody DeviceRequestDto deviceRequestDto) {
        return iotDeviceService.addDevice(deviceRequestDto).map(savedDevice -> ResponseEntity.status(HttpStatus.CREATED).body(savedDevice));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<IotDevice>> updateDevice(@PathVariable String id, @RequestBody IotDevice device) {
        return iotDeviceService.updateDevice(id, device).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build());
    }

//    @DeleteMapping("/{id}")
//    public Mono<ResponseEntity<Void>> deleteDevice(@PathVariable String id) {
//        return iotDeviceService.deleteDevice(id).then(Mono.just(ResponseEntity.noContent().build())).onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
//    }

    @GetMapping
    public Flux<IotDevice> listDevices() {
        return iotDeviceService.listDevices();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<IotDevice>> getDeviceById(@PathVariable String id) {
        return iotDeviceService.getDeviceById(id).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public Flux<IotDevice> findDevicesBySensorNameAndUserId(@RequestParam String sensorName, @RequestParam String userId) {
        return iotDeviceService.findDevicesBySensorNameAndUserId(sensorName, userId);
    }
}
