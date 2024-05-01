package ro.upb.common.dto;

import java.util.Map;

public class IotRequestDto {

    private int userId;
    private Map<String, String> attributes;

    public IotRequestDto(int userId, Map<String, String> attributes) {
        this.userId = userId;
        this.attributes = attributes;
    }

    public IotRequestDto() {
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
}