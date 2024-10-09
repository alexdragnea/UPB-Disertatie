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
import reactor.core.scheduler.Schedulers;
import ro.upb.iotcoreservice.dto.WSMessage;

import java.time.Duration;
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
        log.info("WebSocket connected with sessionId: " + sessionId);

        // Use multicast sink for better scalability with multiple consumers
        Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();
        sessionSinks.put(sessionId, sink);

        // Timeout after 30 minutes of inactivity
        Flux<WebSocketMessage> incomingMessages = session.receive()
                .timeout(Duration.ofMinutes(30))
                .doOnNext(message -> log.info("Received message: " + message.getPayloadAsText()))
                .doOnError(e -> {
                    log.error("Error in WebSocket session: " + sessionId, e);
                    session.close();  // Graceful error handling - close session on error
                })
                .onErrorResume(e -> Mono.empty())  // Handle errors without breaking the stream
                .thenMany(Flux.empty());  // Complete without emitting any values

        // Handle outgoing messages for this session (with pre-serialization and parallel processing)
        Flux<WebSocketMessage> outputMessages = sink.asFlux()
                .parallel()  // Enable parallel processing
                .runOn(Schedulers.parallel())  // Use parallel scheduler for processing
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
                })
                .sequential();  // Convert back to sequential after parallel processing

        // Send outgoing messages and clean up when WebSocket is disconnected
        return session.send(outputMessages)
                .thenMany(incomingMessages)
                .doOnTerminate(() -> {
                    log.info("WebSocket disconnected: " + sessionId);
                    sessionSinks.remove(sessionId);  // Clean up when session is closed
                })
                .doOnError(e -> {
                    log.error("Error in WebSocket processing: " + sessionId, e);
                    sessionSinks.remove(sessionId);  // Ensure clean up even on error
                })
                .then();
    }

    // Overloaded method to broadcast a pre-serialized String message
    public void broadcast(String message) {
        sessionSinks.values().forEach(sink -> sink.tryEmitNext(message));
    }
}
