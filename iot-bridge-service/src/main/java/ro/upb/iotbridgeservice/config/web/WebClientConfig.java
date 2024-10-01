package ro.upb.iotbridgeservice.config.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        ConnectionProvider provider = ConnectionProvider.builder("custom").maxConnections(100).pendingAcquireMaxCount(100).build();
        HttpClient httpClient = HttpClient.create(provider);

        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }
}