//package ro.upb.iotcoreservice.config.web;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.reactive.config.CorsRegistry;
//import org.springframework.web.reactive.config.WebFluxConfigurer;
//
//@Configuration
//public class WebFluxConfig implements WebFluxConfigurer {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("http://localhost:3333")
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .allowedHeaders("Authorization", "Content-Type")
//                .allowCredentials(true);
//    }
//}
