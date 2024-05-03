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
import ro.upb.common.dto.MeasurementDto;
import ro.upb.iotcoreservice.model.IotMeasurement;

@Service
@RequiredArgsConstructor
@Slf4j
public class IotMeasurementServiceImpl implements IotMeasurementService {
    private final InfluxDBClientReactive influxDBClient;

    private static IotMeasurement buildIotMeasurement(MeasurementDto measurementDto) {

        return IotMeasurement.builder().measurement(measurementDto.getMeasurement()).userId(measurementDto.getUserId()).value(measurementDto.getValue()).build();
    }

    @Override
    public void persistIotMeasurement(MeasurementDto measurementDto) {
        WriteReactiveApi writeApi = influxDBClient.getWriteReactiveApi();

        IotMeasurement iotMeasurement = buildIotMeasurement(measurementDto);
        log.info("Converting IotRequestDto: {} to IotMeasurement: {}.", measurementDto, iotMeasurement);

        log.info("Persisting IoTMeasurementPoint.");
        Publisher<WriteReactiveApi.Success> publisher = writeApi.writeMeasurement(WritePrecision.NS, iotMeasurement);

        Disposable subscriber = Flowable.fromPublisher(publisher).subscribe(info -> log.info("Successfully written measurement: {}.", iotMeasurement));

        subscriber.dispose();
    }

    public Flux<IotMeasurement> findAllByUserIdAndMeasurement(int userId, String measurement) {
        String findAllByUserIdQuery = String.format("from(bucket: \"iot-measurement-bucket\") " + "|> range(start: 0) " + "|> filter(fn: (r) => r._measurement == \"%s\" and r._field == \"userId\" and r._value == \"%d\")", measurement, userId);

        QueryReactiveApi queryApi = influxDBClient.getQueryReactiveApi();
        Publisher<IotMeasurement> iotMeasurementPublisher = queryApi.query(findAllByUserIdQuery, IotMeasurement.class);
        return Flux.from(iotMeasurementPublisher);
    }

    @Override
    public Flux<IotMeasurement> findAll() {
        String flux = "from(bucket:\"iot-measurement-bucket\") |> range(start: 0)";

        QueryReactiveApi queryApi = influxDBClient.getQueryReactiveApi();

        Publisher<IotMeasurement> query = queryApi.query(flux, IotMeasurement.class);

        return Flux.from(query);
    }
}
