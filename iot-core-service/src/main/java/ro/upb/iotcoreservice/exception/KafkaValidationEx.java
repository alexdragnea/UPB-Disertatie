package ro.upb.iotcoreservice.exception;

public class KafkaValidationEx extends RuntimeException {
    public KafkaValidationEx(String message) {
        super(message);
    }
}
