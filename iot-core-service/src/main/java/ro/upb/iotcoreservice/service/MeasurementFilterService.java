package ro.upb.iotcoreservice.service;

import reactor.core.publisher.Flux;
import ro.upb.iotcoreservice.domain.MeasurementFilter;
import ro.upb.iotcoreservice.model.IotMeasurement;

public interface MeasurementFilterService {

    Flux<IotMeasurement> filterMeasurements(MeasurementFilter measurementFilter);
}
