package ro.upb.iotcoreservice.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
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
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class IotCoreWebSocketHandler {

    private final ObjectMapper objectMapper;

    // A map to store WebSocketSession ID -> Sink
    private final Map<String, Sinks.Many<String>> sessionSinks = new ConcurrentHashMap<>();

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
        String sessionId = session.getId();
        log.info("Websocket connected with sessionId: " + sessionId);

        // Create a new sink for this session
        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
        sessionSinks.put(sessionId, sink);

        // Handle incoming messages (if needed)
        Flux<WebSocketMessage> incomingMessages = session.receive()
                .doOnNext(message -> {
                    log.info("Received message: " + message.getPayloadAsText());
                    // Process the incoming messages if necessary
                })
                .thenMany(Flux.empty()); // If not processing incoming, you can just leave it as empty

        // Handle outgoing messages for this session
        Flux<WebSocketMessage> outputMessages = sink.asFlux()
                .map(msg -> {
                    try {
                        WSMessage wsMessage = objectMapper.readValue(msg, WSMessage.class);
                        log.info("[Before Send] Message to be sent with timestamp " + wsMessage.getTimestamp());
                        String jsonMessage = objectMapper.writeValueAsString(wsMessage);
                        return session.textMessage(jsonMessage);
                    } catch (Exception e) {
                        log.error("Error serializing message", e);
                        return session.textMessage("Error serializing message");
                    }
                });

        // Use thenMany to send the output messages after handling the incoming ones
        return session.send(outputMessages)
                .thenMany(incomingMessages)
                .doOnTerminate(() -> {
                    log.info("WebSocket disconnected: " + sessionId);
                    sessionSinks.remove(sessionId);  // Clean up when session is closed
                })
                .then();  // Return Mono<Void> as WebSocketSession.send() expects it
    }


    // You will need a method to emit messages to specific sessions or all sessions
    public void sendToSession(String sessionId, String message) {
        Sinks.Many<String> sink = sessionSinks.get(sessionId);
        if (sink != null) {
            sink.tryEmitNext(message);
        } else {
            log.warn("Session not found for sessionId: " + sessionId);
        }
    }

    public void broadcast(String message) {
        sessionSinks.values().forEach(sink -> sink.tryEmitNext(message));
    }
}
