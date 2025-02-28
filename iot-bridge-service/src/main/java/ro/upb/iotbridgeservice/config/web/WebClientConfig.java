package ro.upb.iotbridgeservice.config.web;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        ConnectionProvider provider = ConnectionProvider.builder("custom")
                .maxConnections(500)  // 🚀 Increased max connections for high concurrency
                .pendingAcquireMaxCount(200)  // 🚀 Allow 200 pending requests before failing
                .maxIdleTime(Duration.ofSeconds(20))  // 🕒 Close idle connections after 20s
                .maxLifeTime(Duration.ofMinutes(5))  // 🔄 Recycle connections every 5 minutes
                .evictInBackground(Duration.ofSeconds(30))  // 🧹 Background cleanup every 30s
                .build();

        HttpClient httpClient = HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)  // ⏳ Connection timeout (5s)
                .responseTimeout(Duration.ofSeconds(10))  // ⏳ Max response wait time (10s)
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(10))  // ⏳ Read timeout (10s)
                                .addHandlerLast(new WriteTimeoutHandler(10))  // ⏳ Write timeout (10s)
                )
                .compress(true)  // 🗜️ Enable gzip compression
                .keepAlive(true);  // 🔄 Keep connections alive

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader("Accept", "application/json")  // 📋 Default headers
                .build();
    }
}
