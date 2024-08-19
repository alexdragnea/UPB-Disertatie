package ro.upb.iotbridgeservice.client;

import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import ro.upb.common.dto.LoggedInDetails;
import ro.upb.common.errorhandling.UnauthorizedException;

@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final WebClient.Builder webClientBuilder;

    public Mono<LoggedInDetails> getUser(String authorizationHeader) {
        return webClientBuilder.build()
                .get()
                .uri("http://iot-user-service:8003/v1/iot-user/logged")
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    if (clientResponse.statusCode() == HttpStatus.UNAUTHORIZED) {
                        return Mono.error(new UnauthorizedException("Unauthorized: Invalid or missing authorization header"));
                    }
                    return Mono.error(new RuntimeException("Client error: " + clientResponse.statusCode()));
                })
                .bodyToMono(LoggedInDetails.class)
                .onErrorResume(WebClientResponseException.class, e ->
                        Mono.error(new RuntimeException("Failed to fetch user details", e))
                );
    }

}
