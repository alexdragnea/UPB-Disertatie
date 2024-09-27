package ro.upb.iotuserservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ro.upb.common.dto.ApiKeyResponse;
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
        log.info("Generating API key for userId: {}", userId);
        return apiKeyRepository.deleteByUserId(userId)
                .doOnSuccess(unused -> log.info("Deleted existing API key for userId: {}", userId))
                .then(Mono.defer(() -> {
                    try {
                        String rawApiKey = ApiKeyGenerator.generateApiKey(userId);
                        log.debug("Generated raw API key for userId: {}", userId);
                        String hashedApiKey = ApiKeyHasher.hashApiKey(rawApiKey);
                        log.debug("Hashed API key for userId: {}", userId);
                        ApiKey apiKeyRecord = createApiKeyRecord(userId, hashedApiKey);
                        log.debug("Created API key record for userId: {}", userId);
                        return apiKeyRepository.save(apiKeyRecord)
                                .doOnSuccess(savedApiKey -> log.info("Saved API key record for userId: {}", userId))
                                .map(savedApiKey -> createApiKeyResponse(savedApiKey, rawApiKey));
                    } catch (Exception e) {
                        log.error("Error generating API key for userId: {}", userId, e);
                        return Mono.error(new RuntimeException("Error generating API key", e));
                    }
                }));
    }

    public Mono<ApiKeyResponse> getApiKeyByUserId(String userId) {
        log.info("Retrieving API key for userId: {}", userId);
        return apiKeyRepository.findByUserId(userId)
                .doOnSuccess(apiKeyRecord -> log.info("Retrieved API key record for userId: {}", userId))
                .map(apiKeyRecord -> createApiKeyResponse(apiKeyRecord, apiKeyRecord.getApiKey()))
                .doOnError(e -> log.error("Error retrieving API key for userId: {}", userId, e));
    }

    private ApiKey createApiKeyRecord(String userId, String hashedApiKey) {
        log.debug("Creating API key record for userId: {}", userId);
        ApiKey apiKeyRecord = new ApiKey();
        apiKeyRecord.setUserId(userId);
        apiKeyRecord.setApiKey(hashedApiKey);
        apiKeyRecord.setTimestamp(System.currentTimeMillis());
        return apiKeyRecord;
    }

    private ApiKeyResponse createApiKeyResponse(ApiKey apiKeyRecord, String apiKey) {
        log.debug("Creating API key response for userId: {}", apiKeyRecord.getUserId());
        LocalDateTime generatedAt = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(apiKeyRecord.getTimestamp()), ZoneOffset.UTC);
        return new ApiKeyResponse(apiKeyRecord.getUserId(), apiKey, generatedAt);
    }
}