package ro.upb.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class IotRequestDto {

    private int userId;
    private Map<String, String> attributes;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long createdAt = Instant.now().toEpochMilli();
}