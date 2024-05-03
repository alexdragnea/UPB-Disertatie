package ro.upb.iotcoreservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import java.util.Collections;
import java.util.Map;

@RestController
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@Slf4j
public class MeasurementControllerExHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MeasurementNotFoundEx.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, String>> globalExceptionHandler(MeasurementNotFoundEx e) {
        log.info(e.getMessage());
        Map<String, String> message = Collections.singletonMap("message", e.getMessage());
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
}
