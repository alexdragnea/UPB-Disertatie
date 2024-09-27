package ro.upb.iotuserservice.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ro.upb.common.constant.WebConstants;
import ro.upb.common.dto.ApiKeyResponse;
import ro.upb.iotuserservice.exception.ApiKeyUnauthorizedException;
import ro.upb.iotuserservice.service.ApiKeyService;
import ro.upb.iotuserservice.util.JWTTokenProvider;

import static org.apache.hc.core5.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/v1/iot-user")
@RequiredArgsConstructor
@Slf4j
public class ApiKeyController {

    private final ApiKeyService apiKeyService;
    private final JWTTokenProvider jwtTokenProvider;

    @GetMapping("/api-key")
    public Mono<ApiKeyResponse> getApiKey(@RequestHeader(AUTHORIZATION) String authHeader) {
        log.info("Received request to get API key");
        String userId = jwtTokenProvider.decodeToken(authHeader.substring(7)).getClaim("userId").asString();
        log.debug("Decoded userId from token");
        return apiKeyService.getApiKeyByUserId(userId).doOnSuccess(apiKeyResponse -> log.info("Successfully retrieved API key")).doOnError(e -> log.error("Error retrieving API key", e)).switchIfEmpty(Mono.error(new ApiKeyUnauthorizedException("No API key found for the user.")));
    }

    @GetMapping("/validate-api-key")
    public Mono<ApiKeyResponse> getApiKey(@RequestHeader(WebConstants.API_KEY_HEADER) String apiKey, @RequestParam String userId) {
        log.info("Received request to validate API key");
        return apiKeyService.getApiKeyByUserId(userId).filter(apiKeyResponse -> {
            boolean isValid = apiKeyResponse.getApiKey().equals(apiKey);
            if (isValid) {
                log.info("API key is valid");
            } else {
                log.warn("API key is invalid");
            }
            return isValid;
        }).doOnError(e -> log.error("Error validating API key", e)).switchIfEmpty(Mono.error(new ApiKeyUnauthorizedException("API key is invalid.")));
    }

    @GetMapping("/refresh-api-key")
    public Mono<ResponseEntity<String>> refreshApiKey(@RequestHeader(AUTHORIZATION) String authHeader) {
        log.info("Received request to refresh API key");

        String userId = jwtTokenProvider.decodeToken(authHeader.substring(7)).getClaim("userId").asString();
        log.debug("Decoded userId from token");

        return apiKeyService.generateApiKey(userId).doOnSuccess(apiKeyResponse -> log.info("Successfully refreshed API key for userId: {}", userId)).doOnError(e -> log.error("Error refreshing API key for userId: {}", userId, e)).map(apiKeyResponse -> ResponseEntity.ok("API key refreshed successfully")) // Return a success message
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error refreshing API key")); // Handle error response
    }


}