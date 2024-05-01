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

        IotMeasurement iotMeasurement = buildIotMeasurement(iotRequestDto);
        log.info("Converting IotRequestDto: {} to IotMeasurement: {}.", iotRequestDto, iotMeasurement);
        Flowable<IotMeasurement> measurements = Flowable.just(iotMeasurement);
        log.info("Persisting IoT measurement.");

        Publisher<WriteReactiveApi.Success> publisher = writeApi.writeMeasurements(WritePrecision.NS, measurements);

        Disposable subscriber = Flowable.fromPublisher(publisher).subscribe(info -> log.info("Successfully written measurement: {}.", measurements));

        subscriber.dispose();
    }

    public Flux<IotResponseDto> findAllByUserId(int userId) {
        String findAllByUserIdQuery = String.format("from(bucket: \"iot-event-bucket\") " +
                "|> range(start: 0) " +
                "|> filter(fn: (r) => r._measurement == \"IotEvent\" and r._field == \"userId\" and r._value == %d)", userId);

        QueryReactiveApi queryApi = influxDBClient.getQueryReactiveApi();
        Publisher<IotMeasurement> iotMeasurementPublisher = queryApi.query(findAllByUserIdQuery, IotMeasurement.class);
        return Flux.from(iotMeasurementPublisher)
                .map(this::mapToIotResponseDto);
    }

    @Override
    public Flux<IotResponseDto> findAll() {
       String findAllQuery = "from(bucket:\"iot-event-bucket\") |> range(start: 0) |> filter(fn: (r) => r._measurement == \"IotEvent\")";

        QueryReactiveApi queryApi = influxDBClient.getQueryReactiveApi();
        Flowable.fromPublisher(queryApi.query(findAllQuery))
                .subscribe(fluxRecord -> {
                    //
                    // The callback to consume a FluxRecord.
                    //
                    System.out.println(fluxRecord.getTime() + ": " + fluxRecord.getValueByKey("_attributes"));
                    System.out.println(fluxRecord.getMeasurement());
                });
        return Flux.from(queryApi.query(findAllQuery, IotMeasurement.class))
                .map(this::mapToIotResponseDto);
    }

    private static IotMeasurement buildIotMeasurement(IotRequestDto iotRequestDto) {
        return new IotMeasurement(iotRequestDto.getUserId(), iotRequestDto.getAttributes(), Instant.now().toEpochMilli());
    }

    private IotResponseDto mapToIotResponseDto(IotMeasurement measurement) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC);

        log.info("Converting IotMeasurement: {} to ResponseDto.", measurement);
        return IotResponseDto.builder()
                .userId(measurement.getUserId())
                .attributes(measurement.getAttributes())
                .build();
    }
}
