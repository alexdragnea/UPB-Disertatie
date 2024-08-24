package ro.upb.iotcoreservice.model;

import com.influxdb.annotations.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class IotDevice {

    @Column(tag = true)
    private String sensorName;

    @Column(tag = true)
    private String userId;

    @Column(tag = true)
    private String description;

    @Column(tag = true)
    private String location;

    @Column(tag = true)
    private String rangeValue;

    @Column(timestamp = true)
    private Instant lastChecked;
}
