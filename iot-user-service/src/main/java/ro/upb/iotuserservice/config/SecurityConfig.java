package ro.upb.iotuserservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import ro.upb.iotuserservice.filter.JwtAccessDeniedHandler;
import ro.upb.iotuserservice.filter.JwtAuthorizationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public SecurityConfig(
            AuthenticationConfiguration authenticationConfiguration,
            JwtAccessDeniedHandler jwtAccessDeniedHandler) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector)
            throws Exception {

        MvcRequestMatcher.Builder builder = new MvcRequestMatcher.Builder(introspector);

        return http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .addFilterBefore(new JwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(
                        req ->
                                req.requestMatchers(builder.pattern(HttpMethod.POST, "v1/iot-user/register"))
                                        .permitAll()
                                        .requestMatchers(builder.pattern(HttpMethod.POST, "v1/iot-user/login"))
                                        .permitAll()
                                        .requestMatchers(builder.pattern(HttpMethod.GET, "v1/iot-user/token/refresh"))
                                        .permitAll()
                                        .requestMatchers(builder.pattern(HttpMethod.POST, "v1/iot-user/validateToken"))
                                        .permitAll()
                                        .requestMatchers(builder.pattern(HttpMethod.GET, "v1/iot-user/logged"))
                                        .permitAll()
                                        .requestMatchers(builder.pattern(HttpMethod.PUT, "v1/iot-user/updatePassword"))
                                        .permitAll()
                                        .requestMatchers(builder.pattern(HttpMethod.GET, "v1/iot-user/find/{email}"))
                                        .permitAll()
                                        .requestMatchers("/actuator/health/**")
                                        .permitAll()
                                        .requestMatchers("/actuator/**")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated())
                .exceptionHandling(e -> e.accessDeniedHandler(jwtAccessDeniedHandler))
                .headers(headers -> headers.frameOptions().disable())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
}
