package ro.upb.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Map;

public class IotRequestDto {

    private int userId;
    private Map<String, String> attributes;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long createdAt = Instant.now().toEpochMilli();

    public IotRequestDto(int userId, Map<String, String> attributes, Long createdAt) {
        this.userId = userId;
        this.attributes = attributes;
        this.createdAt = createdAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}