package ro.upb.iotcoreservice.service.device;

import com.influxdb.client.reactive.InfluxDBClientReactive;
import com.influxdb.client.reactive.QueryReactiveApi;
import com.influxdb.client.reactive.WriteReactiveApi;
import com.influxdb.query.FluxRecord;
import io.reactivex.rxjava3.core.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.InfluxDBContainer;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ro.upb.common.dto.MeasurementRequestDto;
import ro.upb.iotcoreservice.config.InfluxDBTestContainer;
import ro.upb.iotcoreservice.domain.MeasurementFilter;
import ro.upb.iotcoreservice.exception.MeasurementNotFoundEx;
import ro.upb.iotcoreservice.model.IotMeasurement;
import ro.upb.iotcoreservice.service.core.IotMeasurementServiceImpl;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class IotMeasurementServiceImplTest {

    private static final InfluxDBContainer<?> influxDBContainer = InfluxDBTestContainer.getInfluxDBContainer();

    @Mock
    private InfluxDBClientReactive influxDBClient;

    @InjectMocks
    private IotMeasurementServiceImpl iotMeasurementService;

    @BeforeAll
    static void setUp() {
        log.info("InfluxDB running at {} ", influxDBContainer.getUrl());
    }

    @Test
    void testPersistIotMeasurement_Success() {
        WriteReactiveApi writeApi = mock(WriteReactiveApi.class);
        when(influxDBClient.getWriteReactiveApi()).thenReturn(writeApi);
        when(writeApi.writeMeasurement(any(), any(IotMeasurement.class))).thenReturn(Flowable.just(new WriteReactiveApi.Success()));

        MeasurementRequestDto requestDto = new MeasurementRequestDto();
        requestDto.setMeasurement("temperature");
        requestDto.setUserId("user1");
        requestDto.setValue(25.0);
        requestDto.setUnit("C");

        iotMeasurementService.persistIotMeasurement(requestDto);

        verify(writeApi, times(1)).writeMeasurement(any(), any(IotMeasurement.class));
    }

    @Test
    void testFindAllByUserIdAndMeasurement_Success() {
        QueryReactiveApi queryApi = mock(QueryReactiveApi.class);
        when(influxDBClient.getQueryReactiveApi()).thenReturn(queryApi);

        MeasurementFilter measurementFilter = new MeasurementFilter();
        measurementFilter.setMeasurement("temperature");
        measurementFilter.setUserId("user1");

        IotMeasurement measurement = new IotMeasurement();
        measurement.setMeasurement("temperature");
        measurement.setUserId("user1");
        measurement.setValue(25.0);
        measurement.setUnit("C");

        when(queryApi.query(any(String.class), eq(IotMeasurement.class))).thenReturn(Flux.just(measurement));

        StepVerifier.create(iotMeasurementService.findAllByUserIdAndMeasurement(measurementFilter)).expectNext(measurement).verifyComplete();
    }

    @Test
    void testFindAllByUserIdAndMeasurement_NotFound() {
        QueryReactiveApi queryApi = mock(QueryReactiveApi.class);
        when(influxDBClient.getQueryReactiveApi()).thenReturn(queryApi);

        MeasurementFilter measurementFilter = new MeasurementFilter();
        measurementFilter.setMeasurement("temperature");
        measurementFilter.setUserId("user1");

        when(queryApi.query(any(String.class), eq(IotMeasurement.class))).thenReturn(Flux.empty());

        StepVerifier.create(iotMeasurementService.findAllByUserIdAndMeasurement(measurementFilter)).expectError(MeasurementNotFoundEx.class).verify();
    }

    @Test
    void testFindUserMeasurements_Success() {
        QueryReactiveApi queryApi = mock(QueryReactiveApi.class);
        when(influxDBClient.getQueryReactiveApi()).thenReturn(queryApi);

        String userId = "user1";
        String findUserMeasurementsQuery = String.format(
                "from(bucket: \"iot-measurement-bucket\") " +
                        "|> range(start: 0) " +
                        "|> filter(fn: (r) => r.userId == \"%s\") " +
                        "|> distinct(column: \"_measurement\") " +
                        "|> keep(columns: [\"_measurement\"])", userId);

        FluxRecord fluxRecord = createMockFluxRecord();

        when(queryApi.query(findUserMeasurementsQuery)).thenReturn(Flux.just(fluxRecord));

        StepVerifier.create(iotMeasurementService.findUserMeasurements(userId))
                .expectNextMatches(userMeasurementDto ->
                        userMeasurementDto.getUserId().equals(userId) &&
                                userMeasurementDto.getMeasurements().contains("temperature"))
                .verifyComplete();
    }
    @Test
    void testFindUserMeasurements_NotFound() {
        QueryReactiveApi queryApi = mock(QueryReactiveApi.class);
        when(influxDBClient.getQueryReactiveApi()).thenReturn(queryApi);

        String userId = "user1";
        String findUserMeasurementsQuery = String.format("from(bucket: \"iot-measurement-bucket\") " + "|> range(start: 0) " + "|> filter(fn: (r) => r.userId == \"%s\") " + "|> distinct(column: \"_measurement\") " + "|> keep(columns: [\"_measurement\"])", userId);

        when(queryApi.query(findUserMeasurementsQuery)).thenReturn(Flux.empty());

        StepVerifier.create(iotMeasurementService.findUserMeasurements(userId)).expectError(MeasurementNotFoundEx.class).verify();
    }

    @Test
    void testFindMeasurementsByTimestampAndUserId_Success() {
        QueryReactiveApi queryApi = mock(QueryReactiveApi.class);
        when(influxDBClient.getQueryReactiveApi()).thenReturn(queryApi);

        MeasurementFilter measurementFilter = new MeasurementFilter();
        measurementFilter.setMeasurement("temperature");
        measurementFilter.setUserId("user1");
        measurementFilter.setStartTime("2023-01-01T00:00:00Z");
        measurementFilter.setEndTime("2023-01-02T00:00:00Z");

        IotMeasurement measurement = new IotMeasurement();
        measurement.setMeasurement("temperature");
        measurement.setUserId("user1");
        measurement.setValue(25.0);
        measurement.setUnit("C");

        when(queryApi.query(any(String.class), eq(IotMeasurement.class))).thenReturn(Flux.just(measurement));

        StepVerifier.create(iotMeasurementService.findMeasurementsByTimestampAndUserId(measurementFilter)).expectNext(measurement).verifyComplete();
    }

    @Test
    void testFindMeasurementsByTimestampAndUserId_NotFound() {
        QueryReactiveApi queryApi = mock(QueryReactiveApi.class);
        when(influxDBClient.getQueryReactiveApi()).thenReturn(queryApi);

        MeasurementFilter measurementFilter = new MeasurementFilter();
        measurementFilter.setMeasurement("temperature");
        measurementFilter.setUserId("user1");
        measurementFilter.setStartTime("2023-01-01T00:00:00Z");
        measurementFilter.setEndTime("2023-01-02T00:00:00Z");

        when(queryApi.query(any(String.class), eq(IotMeasurement.class))).thenReturn(Flux.empty());

        StepVerifier.create(iotMeasurementService.findMeasurementsByTimestampAndUserId(measurementFilter)).expectError(MeasurementNotFoundEx.class).verify();
    }

    private FluxRecord createMockFluxRecord() {
        FluxRecord fluxRecord = mock(FluxRecord.class);

        lenient().when(fluxRecord.getValueByKey("_measurement")).thenReturn("temperature");
        lenient().when(fluxRecord.getValueByKey("userId")).thenReturn("1");
        lenient().when(fluxRecord.getValueByKey("value")).thenReturn("25");
        lenient().when(fluxRecord.getValueByKey("unit")).thenReturn("C");
        lenient().when(fluxRecord.getValueByKey("_time")).thenReturn(Instant.now());

        return fluxRecord;
    }
}