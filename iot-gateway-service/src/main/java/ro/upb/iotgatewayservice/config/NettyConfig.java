package ro.upb.iotgatewayservice.config;

import io.netty.handler.logging.LogLevel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import java.io.InputStream;
import java.security.KeyStore;
import java.time.Duration;

@Configuration
public class NettyConfig {

    @Bean
    public WebServerFactoryCustomizer<NettyReactiveWebServerFactory> nettyServerCustomizer() {
        return factory -> factory.addServerCustomizers(httpServer -> {
            // Configure SSL
            SslContext sslContext;
            try {
                sslContext = SslContextBuilder.forServer(createKeyManagerFactory()).build();
            } catch (SSLException e) {
                throw new RuntimeException("Failed to create SSL context", e);
            }

            return httpServer
                    .secure(sslContextSpec -> sslContextSpec
                            .sslContext(sslContext)
                            .handshakeTimeout(Duration.ofSeconds(30)) // Set SSL handshake timeout
                    )
                    .wiretap("reactor.netty.http.server.HttpServer", LogLevel.DEBUG); // Optional logging
        });
    }

    private KeyManagerFactory createKeyManagerFactory() {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            try (InputStream keyStoreStream = getClass().getClassLoader().getResourceAsStream("gateway.p12")) {
                keyStore.load(keyStoreStream, "changeit".toCharArray());
            }

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, "changeit".toCharArray());

            return keyManagerFactory;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create KeyManagerFactory", e);
        }
    }
}
