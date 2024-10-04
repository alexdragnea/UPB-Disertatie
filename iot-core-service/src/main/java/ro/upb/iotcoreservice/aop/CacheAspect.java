package ro.upb.iotcoreservice.aop;

import com.github.benmanes.caffeine.cache.AsyncCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class CacheAspect {

    private final AsyncCache<Object, Object> asyncCache;

    @Around("@annotation(customCacheable)")
    public Object around(ProceedingJoinPoint joinPoint, CustomCacheable customCacheable) throws Throwable {
        String key = generateKey(joinPoint.getArgs());
        log.info("Generated cache key: '{}'.", key);

        CompletableFuture<Object> cachedValueFuture = asyncCache.getIfPresent(key);
        if (cachedValueFuture != null) {
            log.info("Cache hit for key '{}'. Returning cached value.", key);
            Object cachedValue = cachedValueFuture.get();
            if (cachedValue instanceof List<?>) {
                return Flux.fromIterable((List<?>) cachedValue);
            } else {
                return cachedValue;
            }
        }

        log.info("Cache miss for key '{}'. Executing method and caching result.", key);
        Object result = joinPoint.proceed();

        if (result instanceof Flux<?>) {
            return ((Flux<?>) result).collectList().doOnNext(list -> {
                log.info("Caching result list for key '{}'.", key);
                asyncCache.put(key, CompletableFuture.completedFuture(list));
            }).flatMapMany(Flux::fromIterable);
        } else {
            log.info("Caching result object for key '{}'.", key);
            asyncCache.put(key, CompletableFuture.completedFuture(result));
            return result;
        }
    }

    private String generateKey(Object[] args) {
        StringBuilder keyBuilder = new StringBuilder();
        for (Object arg : args) {
            if (arg != null) {
                keyBuilder.append(arg).append("|");
            } else {
                keyBuilder.append("null|");
            }
        }
        return keyBuilder.toString();
    }
}