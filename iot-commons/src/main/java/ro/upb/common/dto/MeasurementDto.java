package ro.upb.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MeasurementDto {

    private String measurement;
    private int userId;
    private Double value;

}