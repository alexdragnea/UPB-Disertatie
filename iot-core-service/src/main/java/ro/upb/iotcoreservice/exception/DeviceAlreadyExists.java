package ro.upb.iotcoreservice.exception;

public class DeviceAlreadyExists extends RuntimeException {
    public DeviceAlreadyExists(String message) {
        super(message);
    }
}
