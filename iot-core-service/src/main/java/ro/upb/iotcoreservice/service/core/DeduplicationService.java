package ro.upb.iotcoreservice.service.core;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class DeduplicationService {

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Value("${redis.deduplicationExpiry}")
    private long deduplicationExpiry;

    public Mono<Boolean> isMessageDuplicate(String messageId) {
        return reactiveRedisTemplate.hasKey(messageId);
    }

    public Mono<Void> markMessageAsProcessed(String messageId) {
        // Set the key-value pair (messageId -> "processed")
        return reactiveRedisTemplate.opsForValue()
                .set(messageId, "processed", Duration.ofSeconds(deduplicationExpiry))
                .then();
    }
}
