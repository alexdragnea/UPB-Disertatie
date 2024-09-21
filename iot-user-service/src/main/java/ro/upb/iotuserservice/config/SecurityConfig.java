package ro.upb.iotuserservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import ro.upb.iotuserservice.filter.JwtAccessDeniedHandler;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable) // Use CORS config here
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/v1/iot-user/register").permitAll()
                        .pathMatchers(HttpMethod.POST, "/v1/iot-user/login").permitAll()
                        .pathMatchers(HttpMethod.GET, "/v1/iot-user/token/refresh").permitAll()
                        .pathMatchers(HttpMethod.POST, "/v1/iot-user/validateToken").permitAll()
                        .pathMatchers(HttpMethod.GET, "/v1/iot-user/logged").permitAll()
                        .pathMatchers(HttpMethod.PUT, "/v1/iot-user/updatePassword").permitAll()
                        .pathMatchers(HttpMethod.GET, "/v1/iot-user/find/{email}").permitAll()
                        .pathMatchers("/actuator/health/**").permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        .anyExchange().authenticated())
                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                .headers(headersSpec -> headersSpec
                        .frameOptions(ServerHttpSecurity.HeaderSpec.FrameOptionsSpec::disable))
                .build();
    }
}
