package ro.upb.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IotResponseDto {

    private int userId;
    private Map<String, String> attributes;
    private String createdAt;
}