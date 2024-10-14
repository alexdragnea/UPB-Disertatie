package ro.upb.iotuserservice.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ro.upb.common.dto.LoggedInDetails;
import ro.upb.iotuserservice.dto.RegisterUserRequest;
import ro.upb.iotuserservice.dto.UpdateUserRequest;
import ro.upb.iotuserservice.dto.UserDto;
import ro.upb.iotuserservice.exception.EmailExistException;
import ro.upb.iotuserservice.exception.EmailNotFoundException;
import ro.upb.iotuserservice.exception.UserNotFoundException;
import ro.upb.iotuserservice.model.User;
import ro.upb.iotuserservice.model.UserPrincipal;
import ro.upb.iotuserservice.repository.UserRepository;
import ro.upb.iotuserservice.util.JWTTokenProvider;

import java.util.List;

import static ro.upb.iotuserservice.constants.SecurityConstant.AUTHORITIES;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements ReactiveUserDetailsService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;
    private final JWTTokenProvider jwtTokenProvider;
    private final ApiKeyService apiKeyService;

    @Override
    public Mono<UserDetails> findByUsername(String email) {
        return userRepository.findUserByEmail(email)
                .doOnNext(this::validateLoginAttempt)
                .map(user -> (UserDetails) new UserPrincipal(user))
                .switchIfEmpty(Mono.error(new EmailNotFoundException("No user found for email: " + email)));
    }

    // Todo: Enable Reactive Transactional
    public Mono<Void> register(RegisterUserRequest request) {
        return validateEmail(request.getEmail())
                .then(Mono.defer(() -> {
                    User newUser = new User();
                    newUser.setFirstName(request.getFirstName());
                    newUser.setLastName(request.getLastName());
                    newUser.setEmail(request.getEmail());
                    newUser.setPassword(passwordEncoder.encode(request.getPassword()));
                    newUser.setAuthorities(new String[]{"ROLE_USER"});
                    return userRepository.save(newUser)
                            .flatMap(user ->
                                    apiKeyService.generateApiKey(user.getId())
                                            .then()
                                            .onErrorResume(e -> {
                                                log.error("Failed to generate API key for user: {}", user.getId(), e);
                                                return userRepository.delete(user).then();
                                            })
                            )
                            .then();
                }));
    }


    public Mono<UserDto> validateToken(String token) {
        DecodedJWT decodedJWT = jwtTokenProvider.decodeToken(token);
        String userId = decodedJWT.getClaim("userId").asString();
        String username = decodedJWT.getClaim("username").asString();
        return Mono.just(new UserDto(userId, username));
    }

    public Mono<User> getUserById(String id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User could not be found by id " + id)));
    }

    public Mono<User> findUserByEmail(String email) {
        return userRepository
                .findUserByEmail(email)
                .switchIfEmpty(Mono.error(new EmailNotFoundException("No user found for email: " + email)));
    }

    public Mono<LoggedInDetails> getLoggedInDetails(String token) {
        DecodedJWT decodedJWT = jwtTokenProvider.decodeToken(token);

        List<String> roles = decodedJWT.getClaim(AUTHORITIES).asList(String.class);
        String userId = decodedJWT.getClaim("userId").asString();
        String firstName = decodedJWT.getClaim("firstName").asString();
        String lastName = decodedJWT.getClaim("lastName").asString();
        String email = decodedJWT.getClaim("email").asString();

        LoggedInDetails loggedInDetails = LoggedInDetails.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .userId(userId)
                .roles(roles)
                .build();

        log.info("LoggedInDetails: {}", loggedInDetails);
        return Mono.just(loggedInDetails);
    }

    public Mono<Void> updateUserProfile(UpdateUserRequest updateUserRequest) {
        return userRepository.findById(updateUserRequest.getUserId())
                .flatMap(user -> {
                    user.setFirstName(updateUserRequest.getFirstName());
                    user.setLastName(updateUserRequest.getLastName());
                    user.setEmail(updateUserRequest.getEmail());
                    return userRepository.save(user);
                })
                .then();
    }


    public Mono<Void> updateUserPassword(UpdateUserRequest updateUserRequest) {
        return userRepository.findById(updateUserRequest.getUserId())
                .flatMap(user -> {
                    user.setPassword(passwordEncoder.encode(updateUserRequest.getPassword()));
                    return userRepository.save(user);
                })
                .then();
    }

    private Mono<Void> validateEmail(String email) {
        return userRepository.findUserByEmail(email)
                .flatMap(existingUser -> Mono.error(new EmailExistException("Email already exists.")))
                .then();
    }

    private void validateLoginAttempt(User user) {
        loginAttemptService.evictUserFromLoginAttemptCache(user.getEmail());
    }
}