package ro.upb.iotcoreservice.service;

import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.reactive.InfluxDBClientReactive;
import com.influxdb.client.reactive.QueryReactiveApi;
import com.influxdb.client.reactive.WriteReactiveApi;
import com.influxdb.client.write.Point;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ro.upb.common.dto.MeasurementDto;
import ro.upb.iotcoreservice.model.IotMeasurement;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class IotMeasurementServiceImpl implements IotMeasurementService {
    private final InfluxDBClientReactive influxDBClient;

    private static Point buildIotMeasurementPoint(MeasurementDto measurementDto) {

        return Point.measurement(measurementDto.getMeasurement())
                .addTag("userId", measurementDto.getUserId())
                .addField("value", measurementDto.getValue())
                .time(Instant.now().toEpochMilli(), WritePrecision.NS);
    }

    @Override
    public void persistIotMeasurement(MeasurementDto measurementDto) {
        WriteReactiveApi writeApi = influxDBClient.getWriteReactiveApi();

        Point iotMeasurementPoint = buildIotMeasurementPoint(measurementDto);
        log.info("Converting IotRequestDto: {} to IotMeasurement: {}.", measurementDto, iotMeasurementPoint);

        log.info("Persisting IoTMeasurementPoint.");
        Publisher<WriteReactiveApi.Success> publisher = writeApi.writePoint(WritePrecision.NS, iotMeasurementPoint);

        Disposable subscriber = Flowable.fromPublisher(publisher).subscribe(info -> log.info("Successfully written measurement: {}.", iotMeasurementPoint));

        subscriber.dispose();
    }

    public Flux<IotMeasurement> findAllByUserIdAndMeasurement(int userId, String measurement) {
        String findAllByUserIdQuery = String.format("from(bucket: \"iot-measurement-bucket\") " +
                "|> range(start: 0) " +
                "|> filter(fn: (r) => r._measurement == \"%s\" and r._field == \"userId\" and r._value == \"%d\")", measurement, userId);

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
