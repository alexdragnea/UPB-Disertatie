package ro.upb.iotcoreservice.service.core;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ro.upb.common.avro.MeasurementMessage;
import ro.upb.iotcoreservice.domain.MeasurementFilter;
import ro.upb.iotcoreservice.domain.UserMeasurementDto;
import ro.upb.iotcoreservice.dto.IotMeasurementDto;
import ro.upb.iotcoreservice.model.IotMeasurement;

public interface IotMeasurementService {
    Mono<Void> persistIotMeasurement(MeasurementMessage message);

    Flux<IotMeasurement> findAllByUserIdAndMeasurement(MeasurementFilter measurementFilter);

    Mono<UserMeasurementDto> findUserMeasurements(String userId);

    Flux<IotMeasurement> findMeasurementsByTimestampAndUserId(MeasurementFilter measurementFilter);

    IotMeasurementDto mapToDto(IotMeasurement iotMeasurement);
}
