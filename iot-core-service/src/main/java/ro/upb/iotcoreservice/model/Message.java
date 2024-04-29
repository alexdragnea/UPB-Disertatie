package ro.upb.iotcoreservice.model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.Data;

import java.time.Instant;

@Measurement(name = "Message")
@Data
public class Message {

    @Column
    private String message;

    @Column(timestamp = true)
    private Instant time;
}