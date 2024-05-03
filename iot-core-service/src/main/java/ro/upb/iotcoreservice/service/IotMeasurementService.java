package ro.upb.iotcoreservice.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ro.upb.common.dto.MeasurementRequestDto;
import ro.upb.iotcoreservice.dto.UserMeasurementDto;
import ro.upb.iotcoreservice.model.IotMeasurement;

import java.time.Instant;

public interface IotMeasurementService {
    void persistIotMeasurement(MeasurementRequestDto measurementRequestDto);

    Flux<IotMeasurement> findAllByUserIdAndMeasurement(String userId, String measurement);
    Mono<UserMeasurementDto> findUserMeasurements(String userId);

    Flux<IotMeasurement> findMeasurementsByTimestampAndUserId(String measurement, String userId, Instant startTime, Instant endTime);
}
