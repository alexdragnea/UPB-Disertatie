package ro.upb.iotbridgeservice.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ro.upb.common.dto.ApiKeyResponse;

import static ro.upb.common.constant.WebConstants.API_KEY_HEADER;

@Component
@RequiredArgsConstructor
public class ApiKeyServiceClient {

    private final WebClient webClient;

    public Mono<ApiKeyResponse> getApiKey(String apiKey, String userId) {
        return webClient
                .get()
                .uri("http://iot-user-service:8003/v1/iot-user/validate-api-key?userId=" + userId)
                .header(API_KEY_HEADER, apiKey)
                .retrieve()
                .bodyToMono(ApiKeyResponse.class)
                .publishOn(Schedulers.boundedElastic());
    }
}