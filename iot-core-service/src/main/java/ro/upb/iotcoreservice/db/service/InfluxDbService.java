package ro.upb.iotcoreservice.db.service;

import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.reactive.InfluxDBClientReactive;
import com.influxdb.client.reactive.WriteReactiveApi;
import io.reactivex.rxjava3.core.Flowable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ro.upb.iotcoreservice.model.Message;

import java.time.Instant;

@RequiredArgsConstructor
@Service
@Slf4j
public class InfluxDbService {

    private final InfluxDBClientReactive influxDBClient;

    public Mono<Void> writeData(String message) {
        WriteReactiveApi writeApi = influxDBClient.getWriteReactiveApi();

        Flowable<Message> measurements = Flowable.just(createMessage(message));

        return Mono.from(writeApi.writeMeasurements(WritePrecision.NS, measurements))
                .then();
    }

    private Message createMessage(String message) {
        Message msg = new Message();
        msg.setMessage(message);
        msg.setTime(Instant.now());
        return msg;
    }
}
