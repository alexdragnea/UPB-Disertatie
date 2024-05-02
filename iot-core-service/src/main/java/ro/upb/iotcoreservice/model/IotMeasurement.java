package ro.upb.iotcoreservice.model;

import com.influxdb.annotations.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class IotMeasurement {

    @Column
    private String measurement;

    @Column
    private int userId;

    @Column
    private Double value;

}