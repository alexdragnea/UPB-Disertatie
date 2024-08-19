package ro.upb.iotcoreservice.client;

import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ro.upb.common.dto.LoggedInDetails;
import ro.upb.common.errorhandling.ExchangeFilter;

@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final WebClient.Builder webClientBuilder;

    public Mono<LoggedInDetails> getUser(String authorizationHeader) {
        WebClient webClient = webClientBuilder
                .filters(filters -> filters.add(ExchangeFilter.exchangeFilterResponseProcessor()))
                .build();


        return webClient
                .get()
                .uri("http://iot-user-service:8003/v1/iot-user/logged")
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .retrieve()
                .bodyToMono(LoggedInDetails.class);
    }
}
