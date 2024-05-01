package ro.upb.iotcoreservice.db.service;

import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.reactive.InfluxDBClientReactive;
import com.influxdb.client.reactive.WriteReactiveApi;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import ro.upb.common.dto.SensorRequestDto;
import ro.upb.iotcoreservice.model.SensorMeasurement;

@RequiredArgsConstructor
@Service
@Slf4j
public class InfluxDbService {

    private final InfluxDBClientReactive influxDBClient;

    public void persistSensorData(SensorRequestDto sensorRequestDto) {
        WriteReactiveApi writeApi = influxDBClient.getWriteReactiveApi();

        SensorMeasurement sensorMeasurement = buildSensorMeasurement(sensorRequestDto);
        Flowable<SensorMeasurement> measurements = Flowable.just(sensorMeasurement);
        log.info("Sensor measurement: {}", measurements);

        Publisher<WriteReactiveApi.Success> publisher = writeApi.writeMeasurements(WritePrecision.NS, measurements);

        Disposable subscriber = Flowable.fromPublisher(publisher)
                .subscribe(info -> log.info("Successfully written measurement: {}.", measurements));

        subscriber.dispose();
    }
    private static SensorMeasurement buildSensorMeasurement(SensorRequestDto sensorRequestDto) {
        return SensorMeasurement.builder()
                .userId(sensorRequestDto.getUserId())
                .attributes(sensorRequestDto.getAttributes())
                .createdAt(sensorRequestDto.getCreatedAt())
                .build();
    }
}
