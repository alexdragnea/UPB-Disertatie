package ro.upb.iotuserservice.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ro.upb.common.errorhandling.HttpResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class JwtAccessDeniedHandler implements ServerAccessDeniedHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        return Mono.defer(() -> {
            HttpResponse httpResponse = new HttpResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    HttpStatus.UNAUTHORIZED,
                    HttpStatus.UNAUTHORIZED.getReasonPhrase().toUpperCase(),
                    "Access denied"
            );

            ObjectMapper mapper = new ObjectMapper();
            byte[] bytes;
            try {
                bytes = mapper.writeValueAsBytes(httpResponse);
            } catch (Exception e) {
                return Mono.error(e);
            }

            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().setContentType(APPLICATION_JSON);
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
        });
    }
}
