//package ro.upb.iotcoreservice.aop;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.cache.Cache;
//import org.springframework.cache.CacheManager;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Flux;
//
//@Aspect
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class CacheAspect {
//
//    private final CacheManager cacheManager;
//
//    @Around("@annotation(customCacheable)")
//    public Object around(ProceedingJoinPoint joinPoint, CustomCacheable customCacheable) throws Throwable {
//        String cacheName = customCacheable.cacheName();
//        Cache cache = cacheManager.getCache(cacheName);
//        if (cache == null) {
//            log.warn("Cache '{}' not found. Proceeding with method execution.", cacheName);
//            return joinPoint.proceed();
//        }
//
//        String key = generateKey(joinPoint.getArgs());
//        log.debug("Generated cache key: '{}'.", key);
//        Cache.ValueWrapper cachedValue = cache.get(key);
//        if (cachedValue != null) {
//            log.debug("Cache hit for key '{}'. Returning cached value.", key);
//            return cachedValue.get();
//        }
//
//        log.debug("Cache miss for key '{}'. Executing method and caching result.", key);
//        Object result = joinPoint.proceed();
//
//        if (result instanceof Flux<?>) {
//            return ((Flux<?>) result)
//                    .collectList()
//                    .doOnNext(list -> {
//                        log.debug("Caching result list for key '{}'.", key);
//                        cache.put(key, list);
//                    })
//                    .flatMapMany(Flux::fromIterable);
//        } else {
//            log.debug("Caching result object for key '{}'.", key);
//            cache.put(key, result);
//            return result;
//        }
//    }
//
//    private String generateKey(Object[] args) {
//        StringBuilder keyBuilder = new StringBuilder();
//        for (Object arg : args) {
//            if (arg != null) {
//                keyBuilder.append(arg).append("|");
//            } else {
//                keyBuilder.append("null|");
//            }
//        }
//        return keyBuilder.toString();
//    }
//}
