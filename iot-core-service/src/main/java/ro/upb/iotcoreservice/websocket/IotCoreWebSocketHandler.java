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

        Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();
        sessionSinks.put(sessionId, sink);

        Flux<WebSocketMessage> incomingMessages = session.receive()
                .timeout(Duration.ofMinutes(30))
                .doOnNext(message -> log.info("Received message: " + message.getPayloadAsText()))
                .doOnError(e -> {
                    log.error("Error in WebSocket session: " + sessionId, e);
                    session.close();
                })
                .onErrorResume(e -> Mono.empty())
                .thenMany(Flux.empty());

        Flux<WebSocketMessage> outputMessages = sink.asFlux()
                .parallel()
                .runOn(Schedulers.parallel())
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
                .sequential();

        return session.send(outputMessages)
                .thenMany(incomingMessages)
                .doOnTerminate(() -> {
                    log.info("WebSocket disconnected: " + sessionId);
                    sessionSinks.remove(sessionId);
                })
                .doOnError(e -> {
                    log.error("Error in WebSocket processing: " + sessionId, e);
                    sessionSinks.remove(sessionId);
                })
                .then();
    }

    public Mono<Void> broadcastReactive(String message) {
        return Mono.fromRunnable(() -> sessionSinks.values().forEach(sink -> sink.tryEmitNext(message)));
    }}