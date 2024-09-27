package ro.upb.iotuserservice.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import ro.upb.iotuserservice.exception.TokenNotValidException;
import ro.upb.iotuserservice.model.UserPrincipal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.auth0.jwt.algorithms.Algorithm.HMAC256;
import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static java.util.Arrays.stream;
import static java.util.Objects.isNull;
import static ro.upb.iotuserservice.constants.SecurityConstant.*;

@Component
@Slf4j
public class JWTTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    public String generateAccessToken(UserPrincipal userPrincipal) {
        String[] claims = getClaimsFromUser(userPrincipal);
        return JWT.create()
                .withIssuer(Company_LLC)
                .withAudience(Company_ADMINISTRATION)
                .withIssuedAt(new Date())
                .withSubject(userPrincipal.getUsername())
                .withArrayClaim(AUTHORITIES, claims)
                .withClaim("userId", userPrincipal.getUserId())
                .withClaim("firstName", userPrincipal.getFirstName())
                .withClaim("lastName", userPrincipal.getLastName())
                .withClaim("email", userPrincipal.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(HMAC512(secret.getBytes()));
    }

    public String generateRefreshToken(UserPrincipal userPrincipal) {
        return JWT.create()
                .withIssuer(Company_LLC)
                .withAudience(Company_ADMINISTRATION)
                .withIssuedAt(new Date())
                .withSubject(userPrincipal.getUserId())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXP))
                .sign(HMAC256(secret.getBytes()));
    }

    public List<GrantedAuthority> getAuthorities(DecodedJWT decodedJWT) {
        String[] claims = decodedJWT.getClaim(AUTHORITIES).asArray(String.class);
        return stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public DecodedJWT decodeToken(String value) {
        if (isNull(value)) {
            throw new TokenNotValidException("Token has not been provided");
        }

        DecodedJWT decodedJWT = JWT.decode(value);

        if (isJWTExpired(decodedJWT)) {
            throw new TokenNotValidException("Token has expired");
        }

        log.info("Token decoded successfully");
        return decodedJWT;
    }


    public boolean isJWTExpired(DecodedJWT decodedJWT) {
        Date expiresAt = decodedJWT.getExpiresAt();
        log.info("Token expires at: {}", expiresAt);

        return expiresAt.before(new Date());
    }

    private String[] getClaimsFromUser(UserPrincipal user) {
        List<String> authorities = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : user.getAuthorities()) {
            authorities.add(grantedAuthority.getAuthority());
        }
        return authorities.toArray(new String[0]);
    }
}
