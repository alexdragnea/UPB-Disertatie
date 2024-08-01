package ro.upb.iotuserservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ro.upb.common.dto.LoggedInDetails;
import ro.upb.iotuserservice.dto.*;
import ro.upb.iotuserservice.model.UserPrincipal;
import ro.upb.iotuserservice.service.UserService;
import ro.upb.iotuserservice.util.AuthenticationHelper;
import ro.upb.iotuserservice.util.UtilityClass;

import static org.apache.hc.core5.http.HttpHeaders.AUTHORIZATION;
import static ro.upb.iotuserservice.constants.SecurityConstant.TOKEN_PREFIX;

@RestController
@RequestMapping("/v1/iot-user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> register(@RequestBody RegisterUserRequest user) {
        return userService.register(user)
                .then(Mono.just(ResponseEntity.ok("User is registered successfully")));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<LoginResponse>> login(@RequestBody LoginUserRequest user) {
        return authenticationHelper.authenticate(user.getEmail(), user.getPassword())
                .then(Mono.defer(() -> userService.findUserByEmail(user.getEmail())))
                .map(loginUser -> {
                    UserPrincipal userPrincipal = new UserPrincipal(loginUser);
                    LoginResponse loginResponse = authenticationHelper.getLoginResponse(userPrincipal);
                    return ResponseEntity.ok(loginResponse);
                });
    }

    @GetMapping("/token/refresh")
    public Mono<ResponseEntity<RefreshTokenResponse>> refreshToken(@RequestHeader("refresh-token") String refreshToken) {
        return authenticationHelper.validateRefreshToken(refreshToken)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/validateToken")
    public Mono<ResponseEntity<UserDto>> validateToken(@RequestParam String token) {
        return userService.validateToken(token)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{userId}")
    public Mono<ResponseEntity<UserCredential>> getUserById(
            @RequestHeader(AUTHORIZATION) String authorizationHeader, @PathVariable String userId) {
        return userService.getLoggedInDetails(authorizationHeader.substring(TOKEN_PREFIX.length()))
                .flatMap(loggedInDetails -> {
                    if (!UtilityClass.IsAdmin(loggedInDetails.getRoles())) {
                        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
                    }
                    return userService.getUserCredentialsById(userId)
                            .map(ResponseEntity::ok);
                });
    }

    @DeleteMapping("/{userId}")
    public Mono<ResponseEntity<Void>> deleteUser(
            @RequestHeader(AUTHORIZATION) String authorizationHeader, @PathVariable String userId) {
        return userService.getLoggedInDetails(authorizationHeader.substring(TOKEN_PREFIX.length()))
                .flatMap(loggedInDetails -> {
                    if (!UtilityClass.IsAdmin(loggedInDetails.getRoles())) {
                        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
                    }
                    return userService.deleteUserById(userId)
                            .then(Mono.just(ResponseEntity.status(HttpStatus.OK).build()));
                });
    }

    @GetMapping("/logged")
    public Mono<ResponseEntity<LoggedInDetails>> getLoggedInDetails(@RequestHeader(AUTHORIZATION) String authorizationHeader) {
        String token = authorizationHeader.substring(TOKEN_PREFIX.length());
        return userService.getLoggedInDetails(token)
                .map(ResponseEntity::ok);
    }
}
