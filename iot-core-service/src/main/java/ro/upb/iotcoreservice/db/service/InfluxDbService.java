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
import ro.upb.common.dto.IotRequestDto;
import ro.upb.iotcoreservice.model.IotMeasurement;

@RequiredArgsConstructor
@Service
@Slf4j
public class InfluxDbService {

    private final InfluxDBClientReactive influxDBClient;

    public void persistIotEvent(IotRequestDto iotRequestDto) {
        WriteReactiveApi writeApi = influxDBClient.getWriteReactiveApi();

        IotMeasurement iotMeasurement = buildSensorMeasurement(iotRequestDto);
        Flowable<IotMeasurement> measurements = Flowable.just(iotMeasurement);
        log.info("IoT measurement: {}", measurements);

        Publisher<WriteReactiveApi.Success> publisher = writeApi.writeMeasurements(WritePrecision.NS, measurements);

        Disposable subscriber = Flowable.fromPublisher(publisher)
                .subscribe(info -> log.info("Successfully written measurement: {}.", measurements));

        subscriber.dispose();
    }
    private static IotMeasurement buildSensorMeasurement(IotRequestDto iotRequestDto) {
        return IotMeasurement.builder()
                .userId(iotRequestDto.getUserId())
                .attributes(iotRequestDto.getAttributes())
                .createdAt(iotRequestDto.getCreatedAt())
                .build();
    }
}
