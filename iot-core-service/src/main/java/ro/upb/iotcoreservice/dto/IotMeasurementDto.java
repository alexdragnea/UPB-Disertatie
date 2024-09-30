package ro.upb.iotcoreservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IotMeasurementDto {
    private String measurement;
    private String userId;
    private Double value;
    private String time;
    private String unit;
}