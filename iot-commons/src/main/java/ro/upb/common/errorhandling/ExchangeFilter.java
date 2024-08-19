package ro.upb.common.errorhandling;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

public class ExchangeFilter {

    public static ExchangeFilterFunction exchangeFilterResponseProcessor() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            HttpStatusCode status = response.statusCode();
            if (HttpStatus.UNAUTHORIZED.equals(status) || HttpStatus.FORBIDDEN.equals(status)) {
                return response.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new UnauthorizedException("Unauthorized access or invalid Authorization header " + body)));
            }
            if (status.is5xxServerError()) {
                return response.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new RuntimeException("Server error")));
            }
            return Mono.just(response);
        });
    }
}
