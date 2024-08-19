package ro.upb.iotbridgeservice.exception;

public class KafkaValidationEx extends RuntimeException {
    public KafkaValidationEx(String message) {
        super(message);
    }
}
