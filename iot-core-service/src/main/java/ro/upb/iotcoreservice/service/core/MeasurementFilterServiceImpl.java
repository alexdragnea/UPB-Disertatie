package ro.upb.iotcoreservice.service.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ro.upb.iotcoreservice.domain.MeasurementFilter;
import ro.upb.iotcoreservice.model.IotMeasurement;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeasurementFilterServiceImpl implements MeasurementFilterService {

    private final IotMeasurementService iotMeasurementService;

    @Override
    public Flux<IotMeasurement> filterMeasurements(MeasurementFilter measurementFilter) {
        log.info("Filtering data according to filter: {}.", measurementFilter);
        if (measurementFilter.getStartTime() != null && measurementFilter.getEndTime() != null) {
            return iotMeasurementService.findMeasurementsByTimestampAndUserId(measurementFilter);
        } else {
            return iotMeasurementService.findAllByUserIdAndMeasurement(measurementFilter);
        }
    }
}
