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
import ro.upb.iotcoreservice.model.Message;

import java.time.Instant;

@RequiredArgsConstructor
@Service
@Slf4j
public class InfluxDbService {

    private final InfluxDBClientReactive influxDBClient;

    public void writeData() {
        WriteReactiveApi writeApi = influxDBClient.getWriteReactiveApi();

        Message message = Message.builder().value(10d).time(Instant.now()).build();
        Flowable<Message> measurements = Flowable.just(message);
        log.info("Measurement: {}", measurements);

        Publisher<WriteReactiveApi.Success> publisher = writeApi.writeMeasurements(WritePrecision.NS, measurements);

        Disposable subscriber = Flowable.fromPublisher(publisher)
                .subscribe(info -> log.info("Successfully written measurement: {}.", measurements));

        subscriber.dispose();
    }
}
