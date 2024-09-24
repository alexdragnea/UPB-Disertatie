package ro.upb.iotuserservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ApiKeyUnauthorizedException extends RuntimeException {
    public ApiKeyUnauthorizedException(String message) {
        super(message);
    }
}
