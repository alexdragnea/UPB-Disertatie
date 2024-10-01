package ro.upb.iotcoreservice.exception;

public class InfluxDbFailedOperationEx extends RuntimeException {
    public InfluxDbFailedOperationEx(String message, Throwable cause) {
        super(message, cause);
    }
}
