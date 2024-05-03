package ro.upb.iotcoreservice.model;

import com.influxdb.annotations.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class IotMeasurement {

    @Column(measurement = true)
    private String measurement;

    @Column(tag = true)
    private String userId;

    @Column
    private Double value;

}