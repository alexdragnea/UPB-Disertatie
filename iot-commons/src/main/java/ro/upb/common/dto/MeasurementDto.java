package ro.upb.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MeasurementDto {

    private String measurement;
    private String userId;
    private Double value;

}