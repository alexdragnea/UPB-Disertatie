package ro.upb.iotcoreservice.model;

import com.influxdb.annotations.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class IotMeasurement {

    @Column
    private int userId;

    @Column
    private Map<String, String> attributes;

    @Column
    private Long createdAt;

}