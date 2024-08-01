package ro.upb.iotuserservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import ro.upb.iotuserservice.filter.JwtAccessDeniedHandler;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http.csrf().disable()
                .cors().and()
                .authorizeExchange(exchanges -> exchanges
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
                .exceptionHandling()
                .accessDeniedHandler(jwtAccessDeniedHandler)
                .and()
                .headers()
                .frameOptions().disable()
                .and()
                .build();
    }
}