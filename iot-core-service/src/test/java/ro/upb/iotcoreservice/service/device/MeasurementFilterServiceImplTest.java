package ro.upb.iotcoreservice.service.device;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ro.upb.iotcoreservice.domain.MeasurementFilter;
import ro.upb.iotcoreservice.model.IotMeasurement;
import ro.upb.iotcoreservice.service.core.IotMeasurementService;
import ro.upb.iotcoreservice.service.core.MeasurementFilterServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeasurementFilterServiceImplTest {
    @Mock
    private IotMeasurementService iotMeasurementService;

    @InjectMocks
    private MeasurementFilterServiceImpl measurementFilterService;

    @Test
    void testFilterMeasurements_WithStartAndEndTime() {
        MeasurementFilter filter = buildMeasurementFilter("2023-01-01T00:00:00Z", "2023-01-02T00:00:00Z", "user1", "pressure");
        IotMeasurement measurement = buildIotMeasurement("pressure", "user1", 25.0, "HPA");

        when(iotMeasurementService.findMeasurementsByTimestampAndUserId(any(MeasurementFilter.class)))
                .thenReturn(Flux.just(measurement));

        StepVerifier.create(measurementFilterService.filterMeasurements(filter))
                .expectNext(measurement)
                .verifyComplete();
    }

    @Test
    void testFilterMeasurements_WithoutStartAndEndTime() {
        MeasurementFilter filter = buildMeasurementFilter(null, null, "user2", "temperature");
        IotMeasurement measurement = buildIotMeasurement("temperature", "user2", 26.0, "C");

        when(iotMeasurementService.findAllByUserIdAndMeasurement(any(MeasurementFilter.class)))
                .thenReturn(Flux.just(measurement));

        StepVerifier.create(measurementFilterService.filterMeasurements(filter))
                .expectNext(measurement)
                .verifyComplete();
    }

    @Test
    void testFilterMeasurements_NoResults() {
        MeasurementFilter filter = buildMeasurementFilter(null, null, "user1", "temperature");

        when(iotMeasurementService.findAllByUserIdAndMeasurement(any(MeasurementFilter.class)))
                .thenReturn(Flux.empty());

        StepVerifier.create(measurementFilterService.filterMeasurements(filter))
                .verifyComplete();
    }

    private MeasurementFilter buildMeasurementFilter(String startTime, String endTime, String userId, String measurement) {
        MeasurementFilter filter = new MeasurementFilter();
        filter.setStartTime(startTime);
        filter.setEndTime(endTime);
        filter.setUserId(userId);
        filter.setMeasurement(measurement);
        return filter;
    }

    private IotMeasurement buildIotMeasurement(String measurement, String userId, double value, String unit) {
        IotMeasurement iotMeasurement = new IotMeasurement();
        iotMeasurement.setMeasurement(measurement);
        iotMeasurement.setUserId(userId);
        iotMeasurement.setValue(value);
        iotMeasurement.setUnit(unit);
        return iotMeasurement;
    }
}