package ro.upb.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IotRequestDto implements Serializable {

    private int userId;
    private Map<String, String> attributes;
    @JsonIgnore
    private Long createdAt = Instant.now().toEpochMilli();
    @JsonIgnore
    private Long updatedAt;
}