package ro.upb.iotcoreservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WSMessage {
    private String measurement;
    private double value;
    private String timestamp;
}