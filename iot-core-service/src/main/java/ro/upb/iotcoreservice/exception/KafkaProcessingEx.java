package ro.upb.iotcoreservice.exception;

public class KafkaProcessingEx extends RuntimeException {
    public KafkaProcessingEx(String message) {
        super(message);
    }
}
