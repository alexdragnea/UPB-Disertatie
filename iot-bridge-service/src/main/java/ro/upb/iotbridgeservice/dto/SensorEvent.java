package ro.upb.iotbridgeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorEvent {

    private int userId;
    private Map<String, String> attributes;
    private String createdAt = Instant.now().toEpochMilli();
    private String updatedAt;
}