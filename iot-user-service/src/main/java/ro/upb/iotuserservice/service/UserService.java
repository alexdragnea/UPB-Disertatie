package ro.upb.iotuserservice.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ro.upb.common.dto.LoggedInDetails;
import ro.upb.iotuserservice.dto.RegisterUserRequest;
import ro.upb.iotuserservice.dto.UserCredential;
import ro.upb.iotuserservice.dto.UserDto;
import ro.upb.iotuserservice.exception.EmailExistException;
import ro.upb.iotuserservice.exception.EmailNotFoundException;
import ro.upb.iotuserservice.model.User;
import ro.upb.iotuserservice.model.UserPrincipal;
import ro.upb.iotuserservice.repository.UserRepository;
import ro.upb.iotuserservice.util.JWTTokenProvider;

import java.util.List;

import static ro.upb.iotuserservice.constants.SecurityConstant.AUTHORITIES;
import static ro.upb.iotuserservice.enums.Role.ROLE_USER;

@Service
@RequiredArgsConstructor
public class UserService implements ReactiveUserDetailsService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;
    private final JWTTokenProvider jwtTokenProvider;

    @Override
    public Mono<UserDetails> findByUsername(String email) {
        return userRepository.findUserByEmail(email)
                .doOnNext(this::validateLoginAttempt)
                .map(user -> (UserDetails) new UserPrincipal(user))
                .switchIfEmpty(Mono.error(new EmailNotFoundException("No user found for email: " + email)));
    }

    public Mono<Void> register(RegisterUserRequest request) {
        return validateEmail(request.getEmail())
                .then(Mono.just(new User()))
                .doOnNext(newUser -> {
                    newUser.setFirstName(request.getFirstName());
                    newUser.setLastName(request.getLastName());
                    newUser.setEmail(request.getEmail());
                    newUser.setPassword(passwordEncoder.encode(request.getPassword()));
                    newUser.setRole(ROLE_USER.name());
                    newUser.setAuthorities(ROLE_USER.getAuthorities());
                })
                .flatMap(userRepository::save)
                .then();
    }

    public Mono<UserDto> validateToken(String token) {
        DecodedJWT decodedJWT = jwtTokenProvider.decodeToken(token);
        String userId = decodedJWT.getClaim("userId").asString();
        String username = decodedJWT.getClaim("firstName").asString() + " " + decodedJWT.getClaim("lastName").asString();
        List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(decodedJWT);
        return Mono.just(new UserDto(userId, authorities, username));
    }

    public Mono<User> getUserById(String id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("User could not be found by id " + id)));
    }

    public Mono<User> findUserByEmail(String email) {
        return userRepository
                .findUserByEmail(email)
                .switchIfEmpty(Mono.error(new EmailNotFoundException("No user found for email: " + email)));
    }

    public Mono<UserCredential> getUserCredentialsById(String id) {
        return userRepository.findById(id)
                .map(user -> new UserCredential(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail()));
    }

    public Mono<LoggedInDetails> getLoggedInDetails(String token) {
        DecodedJWT decodedJWT = jwtTokenProvider.decodeToken(token);

        List<String> roles = decodedJWT.getClaim(AUTHORITIES).asList(String.class);
        String userId = decodedJWT.getClaim("userId").asString();
        String firstName = decodedJWT.getClaim("firstName").asString();
        String lastName = decodedJWT.getClaim("lastName").asString();
        String email = decodedJWT.getClaim("email").asString();

        return Mono.just(LoggedInDetails.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .userId(userId)
                .roles(roles)
                .build());
    }

    @Transactional
    public Mono<Void> deleteUserById(String id) {
        return userRepository.deleteById(id);
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
