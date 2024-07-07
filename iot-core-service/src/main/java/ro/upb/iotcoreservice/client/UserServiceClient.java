package ro.upb.iotcoreservice.client;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import ro.upb.common.dto.LoggedInDetails;

@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final WebClient.Builder webClientBuilder;
    private final ReactorLoadBalancerExchangeFilterFunction lbFunction;

    //    public Mono<LoggedInDetails> getUser(){
//        return webClientBuilder.build()
//                .get()
//                .uri("http://iot-user-service/v1/iot-user/logged")
//                .retrieve()
//                .bodyToMono(LoggedInDetails.class);
//    }
//
    public Mono<LoggedInDetails> getUser() {
        return webClientBuilder.baseUrl("http://iot-user-service")
                .filter(lbFunction)
                .build()
                .get()
                .uri("/v1/iot-user/logged")
                .retrieve()
                .bodyToMono(LoggedInDetails.class)
                .onErrorMap(
                        WebClientRequestException.class,
                        this::handleWebClientRequestException
                );
    }

    private Throwable handleWebClientRequestException(WebClientRequestException ex) {
        // Handle specific WebClientRequestException scenarios here
        return new RuntimeException("Failed to retrieve user details from iot-user-service", ex);
    }
}
