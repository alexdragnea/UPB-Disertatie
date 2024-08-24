package ro.upb.iotcoreservice.service;

import reactor.core.publisher.Mono;
import ro.upb.common.dto.DeviceRequestDto;

public interface IotDeviceService {
    Mono<Void> registerDevice(DeviceRequestDto deviceRequestDto);
}
