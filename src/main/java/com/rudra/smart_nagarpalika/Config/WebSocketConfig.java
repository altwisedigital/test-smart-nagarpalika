package com.rudra.smart_nagarpalika.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rudra.smart_nagarpalika.Handler.AlertWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@Slf4j
public class WebSocketConfig implements WebSocketConfigurer {

    private final AlertWebSocketHandler alertWebSocketHandler;

    public WebSocketConfig(AlertWebSocketHandler alertWebSocketHandler) {
        this.alertWebSocketHandler = alertWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(alertWebSocketHandler, "/ws/alerts")
                .setAllowedOriginPatterns("*");// Configure properly for production
                // Optional: Adds fallback options for older browsers

        log.info("WebSocket handler registered at /ws/alerts");
    }

    /**
     * Configure ObjectMapper for JSON serialization
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
        return mapper;
    }

    /**
     * Optional: WebSocket authentication interceptor
     * Uncomment if you need to authenticate WebSocket connections
     */
    /*
    @Bean
    public WebSocketAuthInterceptor webSocketAuthInterceptor() {
        return new WebSocketAuthInterceptor();
    }

    // Then add to registry:
    // .addInterceptors(webSocketAuthInterceptor())
    */
}

/**
 * Optional: Authentication interceptor for WebSocket connections
 */
/*
@Component
@Slf4j
class WebSocketAuthInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {

        // Extract token from query params or headers
        String token = extractTokenFromRequest(request);

        if (token != null && validateToken(token)) {
            // Store user info in WebSocket session attributes
            attributes.put("userId", getUserIdFromToken(token));
            attributes.put("userRole", getUserRoleFromToken(token));
            log.info("WebSocket connection authorized for user: {}", getUserIdFromToken(token));
            return true;
        }

        log.warn("WebSocket connection denied - invalid or missing token");
        return false; // Reject connection
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
        // Post-handshake logic if needed
    }

    private String extractTokenFromRequest(ServerHttpRequest request) {
        // Extract from query parameter: /ws/alerts?token=xyz
        String query = request.getURI().getQuery();
        if (query != null && query.contains("token=")) {
            return query.split("token=")[1].split("&")[0];
        }
        return null;
    }

    private boolean validateToken(String token) {
        // Add your JWT validation logic here
        return true; // Placeholder
    }

    private String getUserIdFromToken(String token) {
        // Extract user ID from JWT
        return "user123"; // Placeholder
    }

    private String getUserRoleFromToken(String token) {
        // Extract user role from JWT
        return "CITIZEN"; // Placeholder
    }
}
*/