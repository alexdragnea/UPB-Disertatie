package ro.upb.iotcoreservice.service.device;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ro.upb.common.dto.DeviceRequestDto;
import ro.upb.iotcoreservice.model.IotDevice;
import ro.upb.iotcoreservice.repository.IotDeviceRepository;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class IotDeviceServiceImpl implements IotDeviceService {

    private final IotDeviceRepository iotDeviceRepository;

    @Override
    public Mono<IotDevice> addDevice(DeviceRequestDto deviceRequestDto) {
        return iotDeviceRepository.save(mapToIotDevice(deviceRequestDto));
    }

    @Override
    public Mono<IotDevice> updateDevice(String id, DeviceRequestDto deviceRequestDto) {
        return iotDeviceRepository.findById(id).flatMap(existingDevice -> {
            existingDevice.setSensorName(deviceRequestDto.getSensorName());
            existingDevice.setUserId(deviceRequestDto.getUserId());
            existingDevice.setDescription(deviceRequestDto.getDescription());
            existingDevice.setLocation(deviceRequestDto.getLocation());
            existingDevice.setUnit(deviceRequestDto.getUnit());
            existingDevice.setUpdatedAt(Instant.now());
            return iotDeviceRepository.save(existingDevice);
        });
    }

    @Override
    public Mono<Void> deleteDevice(String id) {
        return iotDeviceRepository.deleteById(id);
    }

    @Override
    public Flux<IotDevice> listDevices() {
        return iotDeviceRepository.findAll();
    }

    @Override
    public Mono<IotDevice> getDeviceById(String id) {
        return iotDeviceRepository.findById(id);
    }

    @Override
    public Flux<IotDevice> findDevicesBySensorNameAndUserId(String sensorName, String userId) {
        return iotDeviceRepository.findBySensorNameAndUserId(sensorName, userId);
    }

    private IotDevice mapToIotDevice(DeviceRequestDto deviceRequestDto) {
        return IotDevice.builder()
                .sensorName(deviceRequestDto.getSensorName())
                .userId(deviceRequestDto.getUserId())
                .description(deviceRequestDto.getDescription())
                .location(deviceRequestDto.getLocation())
                .unit(deviceRequestDto.getUnit())
                .build();
    }
}
