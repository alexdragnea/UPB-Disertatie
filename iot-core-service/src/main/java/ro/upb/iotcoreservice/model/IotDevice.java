package ro.upb.iotcoreservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class IotDevice {

    @Id
    private String id;
    private String sensorName;
    private String userId;
    private String description;
    private String location;
    private String rangeValue;
    private Instant addedAt;
}
