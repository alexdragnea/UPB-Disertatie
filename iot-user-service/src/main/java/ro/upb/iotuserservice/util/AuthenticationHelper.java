package ro.upb.iotuserservice.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ro.upb.iotuserservice.dto.LoginResponse;
import ro.upb.iotuserservice.dto.RefreshTokenResponse;
import ro.upb.iotuserservice.exception.TokenNotValidException;
import ro.upb.iotuserservice.model.UserPrincipal;
import ro.upb.iotuserservice.service.UserService;

import static ro.upb.iotuserservice.constants.SecurityConstant.Company_LLC;
import static ro.upb.iotuserservice.constants.SecurityConstant.TOKEN_PREFIX;

@Component
@RequiredArgsConstructor
public class AuthenticationHelper {

    private final JWTTokenProvider jwtTokenProvider;
    private final ReactiveAuthenticationManager authenticationManager;
    private final UserService userService;

    @Value("${jwt.secret}")
    private String secret;

    public LoginResponse getLoginResponse(UserPrincipal user) {
        return new LoginResponse(
                jwtTokenProvider.generateAccessToken(user),
                jwtTokenProvider.generateRefreshToken(user));
    }

    public Mono<Void> authenticate(String email, String password) {
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password))
                .then();
    }

    public Mono<RefreshTokenResponse> validateRefreshToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            try {
                String refresh_token = authorizationHeader.substring(TOKEN_PREFIX.length());
                Algorithm algorithm = Algorithm.HMAC256(secret);
                JWTVerifier verifier = JWT.require(algorithm).withIssuer(Company_LLC).build();
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                String userId = decodedJWT.getSubject();

                return userService.getUserById(userId)
                        .map(user -> {
                            UserPrincipal userPrincipal = new UserPrincipal(user);
                            String accessToken = jwtTokenProvider.generateAccessToken(userPrincipal);
                            return new RefreshTokenResponse(accessToken, refresh_token);
                        }).onErrorResume(e -> Mono.error(new TokenNotValidException("Token validation failed")));
            } catch (Exception exception) {
                return Mono.error(new TokenNotValidException("Token validation failed"));
            }
        } else {
            return Mono.error(new TokenNotValidException("Refresh token is missing"));
        }
    }

    public String getUserIdFromToken(String token) {
        DecodedJWT decodedJWT = jwtTokenProvider.decodeToken(token);
        return decodedJWT.getClaim("userId").asString();
    }
}
