package ro.upb.iotcoreservice.service.core;

import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.reactive.InfluxDBClientReactive;
import com.influxdb.client.reactive.QueryReactiveApi;
import com.influxdb.client.reactive.WriteReactiveApi;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ro.upb.common.constant.ExMessageConstants;
import ro.upb.common.dto.MeasurementRequestDto;
import ro.upb.iotcoreservice.aop.CustomCacheable;
import ro.upb.iotcoreservice.domain.MeasurementFilter;
import ro.upb.iotcoreservice.domain.UserMeasurementDto;
import ro.upb.iotcoreservice.dto.IotMeasurementDto;
import ro.upb.iotcoreservice.exception.MeasurementNotFoundEx;
import ro.upb.iotcoreservice.model.IotMeasurement;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class IotMeasurementServiceImpl implements IotMeasurementService {

    private final InfluxDBClientReactive influxDBClient;

    private static IotMeasurement buildIotMeasurement(MeasurementRequestDto measurementRequestDto) {

        return IotMeasurement.builder().measurement(measurementRequestDto.getMeasurement())
                .userId(measurementRequestDto.getUserId())
                .value(measurementRequestDto.getValue())
                .unit(measurementRequestDto.getUnit()).build();
    }

    private static UserMeasurementDto buildUserMeasurementDto(String userId, List<String> measurements) {
        return UserMeasurementDto.builder().userId(userId).measurements(measurements).build();
    }

    @Override
    public void persistIotMeasurement(MeasurementRequestDto measurementRequestDto) {
        WriteReactiveApi writeApi = influxDBClient.getWriteReactiveApi();

        IotMeasurement iotMeasurement = buildIotMeasurement(measurementRequestDto);
        log.info("Converting IotRequestDto: {} to IotMeasurement: {}.", measurementRequestDto, iotMeasurement);

        log.info("Persisting IoTMeasurementPoint.");
        Publisher<WriteReactiveApi.Success> publisher = writeApi.writeMeasurement(WritePrecision.NS, iotMeasurement);

        Disposable subscriber = Flowable.fromPublisher(publisher).subscribe(info -> log.info("Successfully persisted measurement: {}.", iotMeasurement));

        subscriber.dispose();
    }

    @Override
    @CustomCacheable(cacheName = "findAllByUserIdAndMeasurementCache")
    public Flux<IotMeasurement> findAllByUserIdAndMeasurement(MeasurementFilter measurement) {
        String findAllByUserIdQuery = String.format(
                "from(bucket: \"iot-measurement-bucket\") " +
                        "|> range(start: 0) " +
                        "|> filter(fn: (r) => r._measurement == \"%s\" and r.userId == \"%s\")",
                measurement.getMeasurement(),
                measurement.getUserId());

        QueryReactiveApi queryApi = influxDBClient.getQueryReactiveApi();
        Publisher<IotMeasurement> measurementPublisher = queryApi.query(findAllByUserIdQuery, IotMeasurement.class);

        String errorMessage = String.format(ExMessageConstants.MEASUREMENT_NOT_FOUND_EX, measurement);

        return Flux.from(measurementPublisher)
                .switchIfEmpty(Mono.error(new MeasurementNotFoundEx(errorMessage)));
    }

    @Override
    @CustomCacheable(cacheName = "findUserMeasurementsCache")
    public Mono<UserMeasurementDto> findUserMeasurements(String userId) {
        String findUserMeasurementsQuery = String.format("from(bucket: \"iot-measurement-bucket\") " +
                "|> range(start: 0) " +
                "|> filter(fn: (r) => r.userId == \"%s\") " +
                "|> distinct(column: \"_measurement\") " +
                "|> keep(columns: [\"_measurement\"])", userId);

        QueryReactiveApi queryApi = influxDBClient.getQueryReactiveApi();

        return Flux.from(queryApi.query(findUserMeasurementsQuery))
                .map(result -> Objects.requireNonNull(result.getValueByKey("_measurement")).toString())
                .collectList()
                .flatMap(measurements -> {
                    if (measurements.isEmpty()) {
                        return Mono.error(new MeasurementNotFoundEx(
                                String.format(ExMessageConstants.MEASUREMENT_NOT_FOUND_EX_FOR_USERID, userId)));
                    } else {
                        return Mono.just(buildUserMeasurementDto(userId, measurements));
                    }
                });
    }

    @Override
    @CustomCacheable(cacheName = "findMeasurementsByTimestampAndUserIdCache")
    public Flux<IotMeasurement> findMeasurementsByTimestampAndUserId(MeasurementFilter measurementFilter) {
        String queryByTimestamp = String.format("from(bucket: \"iot-measurement-bucket\") " +
                        "|> range(start: %s, stop: %s) " +
                        "|> filter(fn: (r) => r._measurement == \"%s\" and r.userId == \"%s\")",
                measurementFilter.getStartTime(), measurementFilter.getEndTime(),
                measurementFilter.getMeasurement(), measurementFilter.getUserId());
        QueryReactiveApi queryApi = influxDBClient.getQueryReactiveApi();

        Publisher<IotMeasurement> measurementPublisher = queryApi.query(queryByTimestamp, IotMeasurement.class);

        return Flux.from(measurementPublisher).switchIfEmpty(Mono.error(new MeasurementNotFoundEx(String.format(ExMessageConstants.MEASUREMENT_NOT_FOUND_EX, measurementFilter))));
    }

    @Override
    public IotMeasurementDto mapToDto(IotMeasurement iotMeasurement) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC);
        return new IotMeasurementDto(
                iotMeasurement.getMeasurement(),
                iotMeasurement.getUserId(),
                iotMeasurement.getValue(),
                formatter.format(iotMeasurement.getTime()),
                iotMeasurement.getUnit()
        );
    }
}
