package ro.upb.iotuserservice.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ro.upb.common.dto.LoggedInDetails;
import ro.upb.iotuserservice.dto.*;
import ro.upb.iotuserservice.model.UserPrincipal;
import ro.upb.iotuserservice.service.UserService;
import ro.upb.iotuserservice.util.AuthenticationHelper;

import static org.apache.hc.core5.http.HttpHeaders.AUTHORIZATION;
import static ro.upb.iotuserservice.constants.SecurityConstant.TOKEN_PREFIX;

@RestController
@RequestMapping("/v1/iot-user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> register(@RequestBody RegisterUserRequest user) {
        log.info("User registration attempt for email: {}", user.getEmail());
        return userService.register(user)
                .doOnSuccess(unused -> log.info("User registered successfully: {}", user.getEmail()))
                .then(Mono.just(ResponseEntity.ok("User is registered successfully")));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<LoginResponse>> login(@RequestBody LoginUserRequest user) {
        log.info("Login attempt for email: {}", user.getEmail());
        return authenticationHelper.authenticate(user.getEmail(), user.getPassword())
                .then(Mono.defer(() -> userService.findUserByEmail(user.getEmail())))
                .map(loginUser -> {
                    UserPrincipal userPrincipal = new UserPrincipal(loginUser);
                    LoginResponse loginResponse = authenticationHelper.getLoginResponse(userPrincipal);
                    log.info("User logged in successfully: {}", user.getEmail());
                    return ResponseEntity.ok(loginResponse);
                })
                .doOnError(e -> log.error("Login failed for email: {}. Error: {}", user.getEmail(), e.getMessage()));
    }

    @GetMapping("/token/refresh")
    public Mono<ResponseEntity<RefreshTokenResponse>> refreshToken(@RequestHeader("refresh-token") String refreshToken) {
        log.info("Refreshing token for refresh token: {}", refreshToken);
        return authenticationHelper.validateRefreshToken(refreshToken)
                .doOnSuccess(unused -> log.info("Refresh token validated successfully"))
                .map(ResponseEntity::ok)
                .doOnError(e -> log.error("Failed to refresh token. Error: {}", e.getMessage()));
    }

    @PostMapping("/validateToken")
    public Mono<ResponseEntity<UserDto>> validateToken(@RequestParam String token) {
        log.info("Validating token: {}", token);
        return userService.validateToken(token)
                .doOnSuccess(unused -> log.info("Token validated successfully"))
                .map(ResponseEntity::ok)
                .doOnError(e -> log.error("Token validation failed. Error: {}", e.getMessage()));
    }

    @GetMapping("/logged")
    public Mono<ResponseEntity<LoggedInDetails>> getLoggedInDetails(@RequestHeader(value = AUTHORIZATION) String authorizationHeader) {
        String token = authorizationHeader.substring(TOKEN_PREFIX.length());
        log.info("Fetching logged in details for token: {}", token);
        return userService.getLoggedInDetails(token)
                .map(ResponseEntity::ok)
                .doOnError(e -> log.info("Error fetching logged in detail. Error: {}", e.getMessage()));
    }
}
