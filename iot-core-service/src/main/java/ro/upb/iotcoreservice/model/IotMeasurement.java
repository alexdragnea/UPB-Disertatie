package ro.upb.iotcoreservice.model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Measurement(name = "IotEvent")
@Data
@Builder
public class IotMeasurement {

    @Column
    private int userId;

    @Column
    private Map<String, String> attributes;

    @Column
    private Long createdAt;

    public IotMeasurement(int userId, Map<String, String> attributes, Long createdAt) {
        this.userId = userId;
        this.attributes = attributes;
        this.createdAt = createdAt;
    }

    public IotMeasurement() {}
}