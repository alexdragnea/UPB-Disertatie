package ro.upb.iotbridgeservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import ro.upb.common.errorhandling.HttpResponse;
import ro.upb.common.errorhandling.UnauthorizedException;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class BridgeExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> generalExceptionHandler(Exception exception) {
        return createHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    @ExceptionHandler(KafkaValidationEx.class)
    public ResponseEntity<HttpResponse> kafkaValidationEx(KafkaValidationEx e) {
        return createHttpResponse(BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(KafkaProcessingEx.class)
    public ResponseEntity<HttpResponse> kafkaValidationEx(KafkaProcessingEx e) {
        return createHttpResponse(INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<HttpResponse> unauthorizedEx(UnauthorizedException e) {
        return createHttpResponse(UNAUTHORIZED, e.getMessage());
    }

    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(), message), httpStatus);
    }
}
