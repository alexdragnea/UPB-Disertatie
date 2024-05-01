package ro.upb.iotcoreservice.model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

import java.util.Map;

@Measurement(name = "IotEvent")
public class IotMeasurement {

    @Column
    private int userId;

    @Column
    private Map<String, String> attributes;

    @Column
    private Long createdAt;

    public IotMeasurement(int userId, Map<String, String> attributes, Long createdAt) {
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