package ro.upb.iotcoreservice.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ro.upb.iotcoreservice.client.UserServiceClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserServiceClient userServiceClient;

    public Mono<Boolean> isAuthorized(String userId) {
        return userServiceClient.getUser()
                .map(loggedInUser -> {
                    if (loggedInUser != null && loggedInUser.getRoles() != null) {
                        return loggedInUser.getUserId().equals(userId) && loggedInUser.getRoles().contains("USER");
                    }
                    return false;
                });
    }
}
