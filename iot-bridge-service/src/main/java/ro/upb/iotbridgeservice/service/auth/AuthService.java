package ro.upb.iotbridgeservice.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ro.upb.iotbridgeservice.client.UserServiceClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserServiceClient userServiceClient;

    public Mono<Boolean> isAuthorized(String userId, String authorizationHeader) {
        return userServiceClient.getUser(authorizationHeader)
                .doOnSubscribe(subscription -> log.debug("Subscription started to fetch user details"))
                .doOnSuccess(loggedInUser -> log.debug("Fetched user details: {}", loggedInUser))
                .doOnError(error -> log.error("Error fetching user details: {}", error.getMessage()))
                .map(loggedInUser -> {
                    if (loggedInUser != null) {
                        log.info("User details retrieved: {}", loggedInUser);
                        if (loggedInUser.getRoles() != null) {
                            boolean isAuthorized = loggedInUser.getUserId().equals(userId) && loggedInUser.getRoles().contains("ROLE_USER");
                            log.info("Authorization check result: {}", isAuthorized);
                            return isAuthorized;
                        } else {
                            log.warn("No roles found for user: {}", loggedInUser.getUserId());
                            return false;
                        }
                    } else {
                        log.warn("No user details found for authorizationHeader: {}", authorizationHeader);
                        return false;
                    }
                });
    }
}
