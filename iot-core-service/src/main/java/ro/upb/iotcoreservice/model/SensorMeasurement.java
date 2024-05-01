package ro.upb.iotcoreservice.model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Measurement(name = "Sensor")
@Data
@Builder
public class SensorMeasurement {

    @Column
    private int userId;

    @Column
    private Map<String, String> attributes;

    @Column
    private Long createdAt;

    @Column
    private Long updatedAt;
}