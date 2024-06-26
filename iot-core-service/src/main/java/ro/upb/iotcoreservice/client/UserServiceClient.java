package ro.upb.iotcoreservice.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ro.upb.common.dto.LoggedInDetails;

@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final WebClient.Builder webClientBuilder;

    public Mono<LoggedInDetails> getUser(){
        return webClientBuilder.build()
                .get()
                .uri("http://iot-user-service/v1/iot-user")
                .retrieve()
                .bodyToMono(LoggedInDetails.class);
    }
}
