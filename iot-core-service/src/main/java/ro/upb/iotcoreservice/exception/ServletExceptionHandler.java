package ro.upb.iotcoreservice.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ro.upb.common.dto.ErrorResponse;

import java.util.Map;


@Slf4j
@ControllerAdvice
@Profile("servlet")
@RequiredArgsConstructor
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ServletExceptionHandler {
    private final Map<Class<? extends Exception>, HttpStatus> exceptionToStatusCode;
    private final HttpStatus defaultStatus;

    @ExceptionHandler(MeasurementNotFoundEx.class)
    public ResponseEntity<ErrorResponse> handleCustomException(MeasurementNotFoundEx ex) {
        return this.handleException(ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        HttpStatus status = exceptionToStatusCode.getOrDefault(ex.getClass(), defaultStatus);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .code(status.value())
                .build();
        log.error("Exception has been occurred ", ex);
        return new ResponseEntity<>(errorResponse, status);
    }
}