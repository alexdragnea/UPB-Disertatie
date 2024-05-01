package ro.upb.iotcoreservice.service;

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
import ro.upb.common.dto.IotRequestDto;
import ro.upb.common.dto.IotResponseDto;
import ro.upb.iotcoreservice.model.IotMeasurement;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class IotMeasurementServiceImpl implements IotMeasurementService {
    private final InfluxDBClientReactive influxDBClient;

    @Override
    public void persistIotMeasurement(IotRequestDto iotRequestDto) {
        WriteReactiveApi writeApi = influxDBClient.getWriteReactiveApi();

        IotMeasurement iotMeasurement = buildSensorMeasurement(iotRequestDto);
        Flowable<IotMeasurement> measurements = Flowable.just(iotMeasurement);
        log.info("Persisting IoT measurement: {}.", measurements);

        Publisher<WriteReactiveApi.Success> publisher = writeApi.writeMeasurements(WritePrecision.NS, measurements);

        Disposable subscriber = Flowable.fromPublisher(publisher).subscribe(info -> log.info("Successfully written measurement: {}.", measurements));

        subscriber.dispose();
    }

    public Flux<IotResponseDto> findAllByUserId(int userId) {
        String findAllByUserIdQuery = String.format("from(bucket: \"iot-event-bucket\") " +
                "|> range(start: 0) " +
                "|> filter(fn: (r) => r._measurement == \"IotEvent\" and r.userId == \"%d\")", userId);

        QueryReactiveApi queryApi = influxDBClient.getQueryReactiveApi();
        Publisher<IotMeasurement> iotMeasurementPublisher = queryApi.query(findAllByUserIdQuery, IotMeasurement.class);
        return Flux.from(iotMeasurementPublisher)
                .map(this::mapToIotResponseDto);
    }

    @Override
    public Flux<IotResponseDto> findAll() {
       String findAllQuery = "from(bucket: \"iot-event-bucket\") |> range(start: 0) |> filter(fn: (r) => r._measurement == \"IotEvent\")";

        QueryReactiveApi queryApi = influxDBClient.getQueryReactiveApi();
        return Flux.from(queryApi.query(findAllQuery, IotMeasurement.class))
                .map(this::mapToIotResponseDto);
    }

    private static IotMeasurement buildSensorMeasurement(IotRequestDto iotRequestDto) {
        return IotMeasurement.builder().userId(iotRequestDto.getUserId()).attributes(iotRequestDto.getAttributes()).createdAt(iotRequestDto.getCreatedAt()).build();
    }

    private IotResponseDto mapToIotResponseDto(IotMeasurement measurement) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC);
        String createdAtUtc = Instant.ofEpochMilli(measurement.getCreatedAt()).atOffset(ZoneOffset.UTC).format(formatter);
        String updatedAtUtc = Instant.ofEpochMilli(measurement.getUpdatedAt()).atOffset(ZoneOffset.UTC).format(formatter);

        log.info("Converting IotMeasurement: {} to ResponseDto.", measurement);
        return IotResponseDto.builder()
                .userId(measurement.getUserId())
                .attributes(measurement.getAttributes())
                .createdAt(createdAtUtc)
                .updatedAt(updatedAtUtc)
                .build();
    }
}
