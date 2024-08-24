package ro.upb.iotcoreservice.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ro.upb.common.dto.MeasurementRequestDto;
import ro.upb.iotcoreservice.domain.MeasurementFilter;
import ro.upb.iotcoreservice.domain.UserMeasurementDto;
import ro.upb.iotcoreservice.exception.DeviceNotFoundEx;
import ro.upb.iotcoreservice.model.IotMeasurement;

public interface IotMeasurementService {
    void persistIotMeasurement(MeasurementRequestDto measurementRequestDto) throws DeviceNotFoundEx;

    Flux<IotMeasurement> findAllByUserIdAndMeasurement(MeasurementFilter measurementFilter);

    Mono<UserMeasurementDto> findUserMeasurements(String userId);

    Flux<IotMeasurement> findMeasurementsByTimestampAndUserId(MeasurementFilter measurementFilter);
}
