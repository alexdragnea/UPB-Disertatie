package ro.upb.iotuserservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ro.upb.iotuserservice.dto.ApiKeyResponse;
import ro.upb.iotuserservice.model.ApiKey;
import ro.upb.iotuserservice.repository.ApiKeyRepository;
import ro.upb.iotuserservice.util.ApiKeyGenerator;
import ro.upb.iotuserservice.util.ApiKeyHasher;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;

    public Mono<ApiKeyResponse> generateApiKey(String userId) {
        return apiKeyRepository.deleteByUserId(userId)
                .then(Mono.defer(() -> {
                    try {
                        String rawApiKey = ApiKeyGenerator.generateApiKey(userId);
                        String hashedApiKey = ApiKeyHasher.hashApiKey(rawApiKey);
                        ApiKey apiKeyRecord = createApiKeyRecord(userId, hashedApiKey);
                        return apiKeyRepository.save(apiKeyRecord)
                                .map(savedApiKey -> createApiKeyResponse(savedApiKey, rawApiKey));
                    } catch (Exception e) {
                        log.error("Error generating API key for userId: {}", userId, e);
                        return Mono.error(new RuntimeException("Error generating API key", e));
                    }
                }));
    }

    public Mono<ApiKeyResponse> getApiKeyByUserId(String userId) {
        return apiKeyRepository.findByUserId(userId)
                .map(apiKeyRecord -> createApiKeyResponse(apiKeyRecord, apiKeyRecord.getApiKey()))
                .doOnError(e -> log.error("Error retrieving API key for userId: {}", userId, e));
    }

    private ApiKey createApiKeyRecord(String userId, String hashedApiKey) {
        ApiKey apiKeyRecord = new ApiKey();
        apiKeyRecord.setUserId(userId);
        apiKeyRecord.setApiKey(hashedApiKey);
        apiKeyRecord.setTimestamp(System.currentTimeMillis());
        return apiKeyRecord;
    }

    private ApiKeyResponse createApiKeyResponse(ApiKey apiKeyRecord, String apiKey) {
        LocalDateTime generatedAt = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(apiKeyRecord.getTimestamp()), ZoneOffset.UTC);
        return new ApiKeyResponse(apiKeyRecord.getUserId(), apiKey, generatedAt);
    }
}