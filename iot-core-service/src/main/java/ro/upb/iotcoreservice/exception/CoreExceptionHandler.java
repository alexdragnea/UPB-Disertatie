package ro.upb.iotcoreservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import ro.upb.common.errorhandling.HttpResponse;
import ro.upb.common.errorhandling.UnauthorizedException;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class CoreExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> generalExceptionHandler(Exception exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MeasurementNotFoundEx.class)
    public ResponseEntity<HttpResponse> measurementNotFoundEx(MeasurementNotFoundEx e) {
        return createHttpResponse(NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(DeviceNotFoundEx.class)
    public ResponseEntity<HttpResponse> deviceNotFoundEx(DeviceNotFoundEx e) {
        return createHttpResponse(NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(DeviceAlreadyExistsEx.class)
    public ResponseEntity<HttpResponse> deviceAlreadyExists(DeviceAlreadyExistsEx e) {
        return createHttpResponse(CONFLICT, e.getMessage());
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
