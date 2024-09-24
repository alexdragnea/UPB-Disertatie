package ro.upb.iotuserservice.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ro.upb.iotuserservice.model.ApiKey;

@Repository
public interface ApiKeyRepository extends ReactiveMongoRepository<ApiKey, String> {
    Mono<ApiKey> findByUserId(String userId);

    Mono<Void> deleteByUserId(String userId);
}