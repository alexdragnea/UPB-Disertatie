package ro.upb.iotcoreservice.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ro.upb.common.dto.MeasurementRequestDto;
import ro.upb.iotcoreservice.dto.UserMeasurementDto;
import ro.upb.iotcoreservice.model.IotMeasurement;

public interface IotMeasurementService {
    void persistIotMeasurement(MeasurementRequestDto measurementRequestDto);

    Flux<IotMeasurement> findAllByUserIdAndMeasurement(String userId, String measurement);

    Flux<IotMeasurement> findAll();

    Mono<UserMeasurementDto> findUserMeasurements(String userId);
}
