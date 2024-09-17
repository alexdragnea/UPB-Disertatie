package ro.upb.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DeviceRequestDto {

    private String sensorName;

    private String userId;

    private String description;

    private String location;

    private String unit;
}
