package ro.upb.iotuserservice.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ro.upb.common.errorhandling.HttpResponse;
import ro.upb.iotuserservice.exception.ApiKeyUnauthorizedException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> generalExceptionHandler(Exception exception) {
        return createHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<HttpResponse> handleMissingRequestHeaders(MissingRequestHeaderException ex) {
        return createHttpResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ApiKeyUnauthorizedException.class)
    public ResponseEntity<HttpResponse> handleApiKeyUnauthorized(ApiKeyUnauthorizedException e) {
        return createHttpResponse(UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<HttpResponse> handleMissingPathVariable(MissingPathVariableException ex) {
        return createHttpResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<HttpResponse> handleMissingRequestParameter(MissingServletRequestParameterException ex) {
        return createHttpResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HttpResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .reduce((message1, message2) -> message1 + ", " + message2)
                .orElse(ex.getMessage());
        return createHttpResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(), message), httpStatus);
    }
}