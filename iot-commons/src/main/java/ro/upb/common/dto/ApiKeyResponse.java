package ro.upb.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiKeyResponse {

    private String userId;
    private String apiKey;
    private LocalDateTime generatedAt;
}
