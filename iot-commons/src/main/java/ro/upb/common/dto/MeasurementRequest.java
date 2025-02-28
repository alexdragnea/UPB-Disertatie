package ro.upb.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MeasurementRequest {

    private String measurement;
    private String userId;
    private Double value;
    private String unit;
}