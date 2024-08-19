package ro.upb.iotbridgeservice.exception;

public class KafkaProcessingEx extends RuntimeException {
    public KafkaProcessingEx(String message) {
        super(message);
    }
}
