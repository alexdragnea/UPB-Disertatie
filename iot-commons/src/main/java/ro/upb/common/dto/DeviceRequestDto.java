package ro.upb.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeviceRequestDto {

    private String sensorName;

    private String userId;

    private String description;

    private String location;

    private String rangeValue;
}
