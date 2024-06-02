package ro.upb.iotuserservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.upb.common.dto.LoggedInDetails;
import ro.upb.iotuserservice.dto.*;
import ro.upb.iotuserservice.model.User;
import ro.upb.iotuserservice.model.UserPrincipal;
import ro.upb.iotuserservice.service.UserService;
import ro.upb.iotuserservice.util.AuthenticationHelper;
import ro.upb.iotuserservice.util.UtilityClass;

import java.io.IOException;
import java.util.UUID;

import static org.apache.hc.core5.http.HttpHeaders.AUTHORIZATION;
import static ro.upb.iotuserservice.constants.SecurityConstant.TOKEN_PREFIX;

@RestController
@RequestMapping("/v1/iot-user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterUserRequest user) {
        userService.register(user);
        return ResponseEntity.ok("User is registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginUserRequest user) {
        authenticationHelper.authenticate(user.getEmail(), user.getPassword());
        User loginUser = userService.findUserByEmail(user.getEmail());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        LoginResponse loginResponse = authenticationHelper.getLoginResponse(userPrincipal);
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/token/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(
            HttpServletRequest request, HttpServletResponse response) throws IOException {

        String authorizationHeader = request.getHeader("refresh-token");
        return ResponseEntity.ok(
                authenticationHelper.validateRefreshToken(authorizationHeader, response));
    }

    @PostMapping("/validateToken")
    public ResponseEntity<UserDto> validateToken(@RequestParam String token) {
        return ResponseEntity.ok(userService.validateToken(token));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserCredential> getUserById(
            @RequestHeader(AUTHORIZATION) String authorizationHeader, @PathVariable UUID userId) {
        LoggedInDetails loggedInDetails = userService.getLoggedInDetails(authorizationHeader.substring(TOKEN_PREFIX.length()));
        if (!UtilityClass.IsAdmin(loggedInDetails.getRoles())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(userService.getUserCredentialsById(userId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @RequestHeader(AUTHORIZATION) String authorizationHeader, @PathVariable UUID userId) {
        LoggedInDetails loggedInDetails = userService.getLoggedInDetails(authorizationHeader.substring(TOKEN_PREFIX.length()));
        if (!UtilityClass.IsAdmin(loggedInDetails.getRoles())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userService.deleteUserById(userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping
    public ResponseEntity<LoggedInDetails> getLoggedInDetails(@RequestHeader(AUTHORIZATION) String authorizationHeader) {
        String token = authorizationHeader.substring(TOKEN_PREFIX.length());
        return ResponseEntity.ok(userService.getLoggedInDetails(token));
    }
}
