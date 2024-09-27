package ro.upb.iotcoreservice.exception;

public class DeviceAlreadyExistsEx extends RuntimeException {
    public DeviceAlreadyExistsEx(String message) {
        super(message);
    }
}
