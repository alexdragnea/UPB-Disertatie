package ro.upb.iotuserservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class ApiKey {
    @Id
    private String id;
    private String userId;
    private String apiKey;
    private long timestamp;
}