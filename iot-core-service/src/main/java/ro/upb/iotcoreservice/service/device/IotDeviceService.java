package ro.upb.iotcoreservice.service.device;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ro.upb.common.dto.DeviceRequestDto;
import ro.upb.iotcoreservice.model.IotDevice;

public interface IotDeviceService {
    Mono<IotDevice> addDevice(DeviceRequestDto deviceRequestDto);
    Mono<IotDevice> updateDevice(String id, DeviceRequestDto deviceRequestDto);
    Mono<Void> deleteDevice(String id);
    Flux<IotDevice> listDevices();
    Mono<IotDevice> getDeviceById(String id);
    Flux<IotDevice> findDevicesBySensorNameAndUserId(String sensorName, String userId);
}
