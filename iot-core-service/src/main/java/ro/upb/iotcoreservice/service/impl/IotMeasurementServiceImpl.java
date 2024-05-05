package ro.upb.iotcoreservice.service.impl;

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
import ro.upb.iotcoreservice.dto.UserMeasurementDto;
import ro.upb.iotcoreservice.exception.MeasurementNotFoundEx;
import ro.upb.iotcoreservice.model.IotMeasurement;
import ro.upb.iotcoreservice.service.IotMeasurementService;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class IotMeasurementServiceImpl implements IotMeasurementService {
    private final InfluxDBClientReactive influxDBClient;

    private static IotMeasurement buildIotMeasurement(MeasurementRequestDto measurementRequestDto) {

        return IotMeasurement.builder().measurement(measurementRequestDto.getMeasurement()).userId(measurementRequestDto.getUserId()).value(measurementRequestDto.getValue()).build();
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

    public Flux<IotMeasurement> findAllByUserIdAndMeasurement(String userId, String measurement) {
        String findAllByUserIdQuery = String.format("from(bucket: \"iot-measurement-bucket\") " + "|> range(start: 0) " + "|> filter(fn: (r) => r._measurement == \"%s\" and r.userId == \"%s\")", measurement, userId); // Use %s for String argument

        QueryReactiveApi queryApi = influxDBClient.getQueryReactiveApi();
        Publisher<IotMeasurement> measurementPublisher = queryApi.query(findAllByUserIdQuery, IotMeasurement.class);

        return Flux.from(measurementPublisher).switchIfEmpty(Mono.error(new MeasurementNotFoundEx(String.format(ExMessageConstants.MEASUREMENT_NOT_FOUND_EX, userId))));
    }

    @Override
    public Mono<UserMeasurementDto> findUserMeasurements(String userId) {
        String findUserMeasurementsQuery = String.format("from(bucket: \"iot-measurement-bucket\") " + "|> range(start: 0) " + "|> filter(fn: (r) => r.userId == \"%s\") " + "|> distinct(column: \"_measurement\") " + "|> keep(columns: [\"_measurement\"])", userId);

        QueryReactiveApi queryApi = influxDBClient.getQueryReactiveApi();


        return Flux.from(queryApi.query(findUserMeasurementsQuery)).map(result -> Objects.requireNonNull(result.getValueByKey("_measurement")).toString()).collectList().handle((measurements, sink) -> {
            if (measurements.isEmpty()) {
                sink.error(new MeasurementNotFoundEx(String.format(ExMessageConstants.MEASUREMENT_NOT_FOUND_EX, userId)));
                return;
            }
            sink.next(buildUserMeasurementDto(userId, measurements));
        });
    }

    @Override
    public Flux<IotMeasurement> findMeasurementsByTimestampAndUserId(String measurement, String userId, String startTime, String endTime) {
        String queryByTimestamp = String.format("from(bucket: \"iot-measurement-bucket\") " + "|> range(start: %s, stop: %s) " + "|> filter(fn: (r) => r._measurement == \"%s\" and r.userId == \"%s\" and r._time >= %s and r._time <= %s)", startTime, endTime, measurement, userId, startTime, endTime);

        QueryReactiveApi queryApi = influxDBClient.getQueryReactiveApi();

        Publisher<IotMeasurement> measurementPublisher = queryApi.query(queryByTimestamp, IotMeasurement.class);

        return Flux.from(measurementPublisher).switchIfEmpty(Mono.error(new MeasurementNotFoundEx(String.format(ExMessageConstants.MEASUREMENT_NOT_FOUND_EX, userId))));
    }
}
