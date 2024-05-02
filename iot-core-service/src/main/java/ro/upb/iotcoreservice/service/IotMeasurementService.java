package ro.upb.iotcoreservice.service;

import reactor.core.publisher.Flux;
import ro.upb.common.dto.MeasurementDto;
import ro.upb.iotcoreservice.model.IotMeasurement;

public interface IotMeasurementService {
    void persistIotMeasurement(MeasurementDto measurementDto);

    Flux<IotMeasurement> findAllByUserIdAndMeasurement(int userId, String measurement);
    Flux<IotMeasurement> findAll();
}
