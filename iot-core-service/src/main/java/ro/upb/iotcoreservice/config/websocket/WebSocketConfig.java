package ro.upb.iotcoreservice.config.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import reactor.core.publisher.Sinks;

@Configuration
@EnableWebSocket
public class WebSocketConfig {

    @Bean
    public Sinks.Many<String> sink() {
        return Sinks.many().multicast().onBackpressureBuffer();
    }
}