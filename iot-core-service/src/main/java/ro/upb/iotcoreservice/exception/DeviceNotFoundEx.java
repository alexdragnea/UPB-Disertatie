package ro.upb.iotcoreservice.exception;

public class DeviceNotFoundEx extends RuntimeException {
    public DeviceNotFoundEx(String message) {
        super(message);
    }
}
