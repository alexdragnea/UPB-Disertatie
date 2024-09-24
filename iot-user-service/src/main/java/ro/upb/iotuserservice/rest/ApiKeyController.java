package ro.upb.iotuserservice.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ro.upb.iotuserservice.dto.ApiKeyResponse;
import ro.upb.iotuserservice.exception.ApiKeyUnauthorizedException;
import ro.upb.iotuserservice.service.ApiKeyService;
import ro.upb.iotuserservice.util.JWTTokenProvider;

@RestController
@RequestMapping("/v1/iot-user")
@RequiredArgsConstructor
@Slf4j
public class ApiKeyController {

    private final ApiKeyService apiKeyService;
    private final JWTTokenProvider jwtTokenProvider;

    @GetMapping("/api-key")
    public Mono<ApiKeyResponse> getApiKey(@RequestHeader("Authorization") String authHeader) {
        String userId = jwtTokenProvider.decodeToken(authHeader.substring(7)).getClaim("userId").asString();
        return apiKeyService.getApiKeyByUserId(userId)
                .switchIfEmpty(Mono.error(new ApiKeyUnauthorizedException("No API key found for the user.")));
    }

    @PostMapping("/refresh-api-key")
    public Mono<ApiKeyResponse> refreshApiKey(@RequestHeader("Authorization") String authHeader) {
        String userId = jwtTokenProvider.decodeToken(authHeader.substring(7)).getClaim("userId").asString();
        return apiKeyService.generateApiKey(userId);
    }
}