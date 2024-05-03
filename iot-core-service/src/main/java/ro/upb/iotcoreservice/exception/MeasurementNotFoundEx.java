package ro.upb.iotcoreservice.exception;

public class MeasurementNotFoundEx extends RuntimeException {
    public MeasurementNotFoundEx(String message) {
        super(message);
    }
}
