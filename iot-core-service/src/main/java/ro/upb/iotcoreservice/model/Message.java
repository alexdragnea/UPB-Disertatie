package ro.upb.iotcoreservice.model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Measurement(name = "Message")
@Data
@Builder
public class Message {

    @Column
    private Double value;

    @Column(timestamp = true)
    private Instant time;
}