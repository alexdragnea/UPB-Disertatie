package ro.upb.iotuserservice.repository;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ro.upb.iotuserservice.model.User;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {

    Mono<User> findUserByEmail(String email);
}
