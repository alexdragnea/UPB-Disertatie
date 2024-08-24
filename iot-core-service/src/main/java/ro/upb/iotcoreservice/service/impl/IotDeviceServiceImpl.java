package ro.upb.iotcoreservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ro.upb.common.dto.DeviceRequestDto;
import ro.upb.iotcoreservice.exception.DeviceAlreadyExistsEx;
import ro.upb.iotcoreservice.model.IotDevice;
import ro.upb.iotcoreservice.repository.IotDeviceRepository;
import ro.upb.iotcoreservice.service.IotDeviceService;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class IotDeviceServiceImpl implements IotDeviceService {

    private final IotDeviceRepository iotDeviceRepository;

    private static IotDevice buildIotDevice(DeviceRequestDto deviceRequestDto) {
        return IotDevice.builder()
                .sensorName(deviceRequestDto.getSensorName())
                .userId(deviceRequestDto.getUserId())
                .description(deviceRequestDto.getDescription())
                .location(deviceRequestDto.getLocation())
                .rangeValue(deviceRequestDto.getRangeValue())
                .addedAt(Instant.now())
                .build();
    }

    @Override
    public Mono<Void> registerDevice(DeviceRequestDto deviceRequestDto) {
        return iotDeviceRepository.findBySensorNameAndUserId(deviceRequestDto.getSensorName(), deviceRequestDto.getUserId())
                .collectList()
                .flatMap(devices -> {
                    if (!devices.isEmpty()) {
                        return Mono.error(new DeviceAlreadyExistsEx("Device already exists for userId: " + deviceRequestDto.getUserId() + " and sensorName: " + deviceRequestDto.getSensorName()));
                    } else {
                        IotDevice iotDevice = buildIotDevice(deviceRequestDto);
                        log.info("Building device with sensorName: {} and userId: {}.", iotDevice.getSensorName(), iotDevice.getUserId());

                        return iotDeviceRepository.save(iotDevice)
                                .doOnSuccess(savedDevice -> log.info("Successfully persisted iotDevice: {}.", savedDevice))
                                .then();
                    }
                });
    }
}