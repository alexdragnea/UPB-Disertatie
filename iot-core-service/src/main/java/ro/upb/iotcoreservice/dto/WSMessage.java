package ro.upb.iotcoreservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WSMessage {
    private Object message;
    private String timestamp;
}