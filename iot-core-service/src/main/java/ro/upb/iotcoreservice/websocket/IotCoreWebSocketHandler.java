package ro.upb.iotcoreservice.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import ro.upb.iotcoreservice.dto.WSMessage;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class IotCoreWebSocketHandler {

    private final Sinks.Many<String> sink;
    private final ObjectMapper objectMapper; // Add ObjectMapper for JSON serialization

    @Bean
    public SimpleUrlHandlerMapping simpleUrlHandlerMapping() {
        Map<String, WebSocketHandler> urlMap = new HashMap<>();
        urlMap.put("/ws", this::handle);

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setUrlMap(urlMap);
        handlerMapping.setOrder(1);

        return handlerMapping;
    }

    public Mono<Void> handle(WebSocketSession session) {
        log.info("Websocket connected");

        Flux<WebSocketMessage> outputMessages = sink.asFlux()
                .map(o -> {
                    try {
                        WSMessage message = objectMapper.readValue(o, WSMessage.class);
                        log.info("[Before Send] Message to be sent with timestamp " + message.getTimestamp());
                        String jsonMessage = objectMapper.writeValueAsString(message);
                        return session.textMessage(jsonMessage);
                    } catch (Exception e) {
                        log.error("Error serializing message", e);
                        return session.textMessage("Error serializing message");
                    }
                });

        return session.send(outputMessages)
                .doOnError(e -> log.error("Error sending message", e))
                .then();
    }
}