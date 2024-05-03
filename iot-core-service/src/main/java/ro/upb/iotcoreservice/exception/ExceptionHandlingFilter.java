package ro.upb.iotcoreservice.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ro.upb.common.dto.ErrorResponse;

import java.io.IOException;
import java.io.PrintWriter;


@Component
@Profile("servlet")
@RequiredArgsConstructor
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ExceptionHandlingFilter extends OncePerRequestFilter {

    private final ServletExceptionHandler exceptionHandler;
    private final ObjectMapper exceptionHandlerObjectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleException(e);
            writeResponseEntity(responseEntity, response);
        }
    }

    private void writeResponseEntity(ResponseEntity<ErrorResponse> responseEntity, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        ErrorResponse error = responseEntity.getBody();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(responseEntity.getStatusCode().value());
        out.print(exceptionHandlerObjectMapper.writeValueAsString(error));
        out.flush();
    }
}
