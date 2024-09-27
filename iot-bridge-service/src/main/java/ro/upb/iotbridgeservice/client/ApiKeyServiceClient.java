package ro.upb.iotbridgeservice.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ro.upb.iotbridgeservice.dto.ApiKeyResponse;

import static ro.upb.common.constant.WebConstants.API_KEY_HEADER;

@RequiredArgsConstructor
@Component
public class ApiKeyServiceClient {

    private final WebClient.Builder webClientBuilder;

    public Mono<ApiKeyResponse> getApiKey(String apiKey, String userId) {
        return webClientBuilder.build().get().uri(uriBuilder -> uriBuilder.path("http://iot-user-service:8003/v1/iot-user/validate-api-key").queryParam("userId", userId).build()).header(API_KEY_HEADER, apiKey).retrieve().bodyToMono(ApiKeyResponse.class);
    }
}
