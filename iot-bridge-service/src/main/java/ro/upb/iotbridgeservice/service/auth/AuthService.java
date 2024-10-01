package ro.upb.iotbridgeservice.service.auth;

import com.github.benmanes.caffeine.cache.AsyncCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ro.upb.iotbridgeservice.client.ApiKeyServiceClient;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final ApiKeyServiceClient apiKeyServiceClient;
    private final AsyncCache<String, Boolean> apiKeyCache;

    public Mono<Boolean> isAuthorizedWithApiKey(String userId, String apiKey) {
        String cacheKey = userId + ":" + apiKey;
        CompletableFuture<Boolean> cachedResult = apiKeyCache.getIfPresent(cacheKey);

        if (cachedResult != null) {
            log.info("Cache hit for API key validation: {}", cacheKey);
            return Mono.fromFuture(cachedResult);
        }

        log.info("Cache miss for API key validation: {}", cacheKey);
        return apiKeyServiceClient.getApiKey(apiKey, userId)
                .doOnSubscribe(subscription -> log.info("Subscription started to validate API key"))
                .doOnSuccess(apiKeyResponse -> log.info("Validated API key: {}", apiKeyResponse))
                .doOnError(error -> log.error("Error validating API key: {}", error.getMessage()))
                .map(apiKeyResponse -> {
                    boolean isAuthorized = apiKeyResponse.getUserId().equals(userId);
                    log.info("API key authorization check result: {}", isAuthorized);
                    return isAuthorized;
                })
                .switchIfEmpty(Mono.just(false))
                .doOnNext(isAuthorized -> {
                    log.info("Caching API key validation result: {}", cacheKey);
                    apiKeyCache.put(cacheKey, CompletableFuture.completedFuture(isAuthorized));
                });
    }
}