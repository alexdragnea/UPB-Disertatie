package ro.upb.iotbridgeservice.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ro.upb.iotbridgeservice.client.ApiKeyServiceClient;
import ro.upb.iotbridgeservice.client.UserServiceClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final ApiKeyServiceClient apiKeyServiceClient;
    public Mono<Boolean> isAuthorizedWithApiKey(String userId, String apiKey) {
        return apiKeyServiceClient.getApiKey(apiKey, userId)
                .doOnSubscribe(subscription -> log.debug("Subscription started to validate API key"))
                .doOnSuccess(apiKeyResponse -> log.debug("Validated API key: {}", apiKeyResponse))
                .doOnError(error -> log.error("Error validating API key: {}", error.getMessage()))
                .map(apiKeyResponse -> {
                    boolean isAuthorized = apiKeyResponse.getUserId().equals(userId);
                    log.info("API key authorization check result: {}", isAuthorized);
                    return isAuthorized;
                })
                .switchIfEmpty(Mono.just(false));
    }
}
