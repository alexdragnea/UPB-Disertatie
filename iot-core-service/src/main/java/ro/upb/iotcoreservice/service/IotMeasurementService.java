package ro.upb.iotcoreservice.service;

import reactor.core.publisher.Flux;
import ro.upb.common.dto.IotRequestDto;
import ro.upb.common.dto.IotResponseDto;

public interface IotMeasurementService {
    void persistIotMeasurement(IotRequestDto iotRequestDto);

    Flux<IotResponseDto> findAllByUserId(int userId);
    Flux<IotResponseDto> findAll();
}
