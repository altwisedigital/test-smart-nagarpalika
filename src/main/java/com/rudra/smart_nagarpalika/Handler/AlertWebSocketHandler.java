        package com.rudra.smart_nagarpalika.Handler;

        import com.fasterxml.jackson.databind.ObjectMapper;
        import com.rudra.smart_nagarpalika.DTO.AlertResponseDTO;
        import com.rudra.smart_nagarpalika.Model.AlertsModel;
        import lombok.extern.slf4j.Slf4j;
        import org.springframework.stereotype.Component;
        import org.springframework.web.socket.CloseStatus;
        import org.springframework.web.socket.TextMessage;
        import org.springframework.web.socket.WebSocketSession;
        import org.springframework.web.socket.handler.TextWebSocketHandler;

        import java.time.LocalDateTime;
        import java.util.Iterator;
        import java.util.Set;
        import java.util.concurrent.ConcurrentHashMap;

        @Component
        @Slf4j
        public class AlertWebSocketHandler extends TextWebSocketHandler {

            private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
            private final ObjectMapper objectMapper = new ObjectMapper();

            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                sessions.add(session);
                log.info("WebSocket connection established. Session ID: {}", session.getId());
                log.info("Total active sessions: {}", sessions.size());
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
                sessions.remove(session);
                log.info("WebSocket connection closed. Session ID: {}, Status: {}", session.getId(), status);
                log.info("Total active sessions: {}", sessions.size());
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                log.error("Transport error for session {}: {}", session.getId(), exception.getMessage(), exception);
                sessions.remove(session);
            }

            /**
             * Broadcast new alert to all connected clients
             */
            public void broadcastNewAlert(AlertsModel alert) {
                if (sessions.isEmpty()) {
                    log.info("No active WebSocket sessions to broadcast to");
                    return;
                }

                try {
                    // Create structured message
                    WebSocketMessage message = new WebSocketMessage();
                    message.setType("NEW_ALERT");
                    message.setTimestamp(LocalDateTime.now());
                    message.setData(alert);

                    String jsonMessage = objectMapper.writeValueAsString(message);

                    log.info("Broadcasting alert to {} sessions: {}", sessions.size(), alert.getTitle());

                    // Broadcast to all sessions with error handling
                    Iterator<WebSocketSession> iterator = sessions.iterator();
                    while (iterator.hasNext()) {
                        WebSocketSession session = iterator.next();
                        try {
                            if (session.isOpen()) {
                                session.sendMessage(new TextMessage(jsonMessage));
                                log.debug("Message sent to session: {}", session.getId());
                            } else {
                                iterator.remove();
                                log.warn("Removed closed session: {}", session.getId());
                            }
                        } catch (Exception e) {
                            log.error("Failed to send message to session {}: {}", session.getId(), e.getMessage());
                            iterator.remove();
                        }
                    }

                } catch (Exception e) {
                    log.error("Failed to broadcast alert: {}", e.getMessage(), e);
                }
            }

            /**
             * Get count of active sessions
             */
            public int getActiveSessionCount() {
                return sessions.size();
            }

            /**
             * Inner class for structured WebSocket messages
             */
            public static class WebSocketMessage {
                private String type;
                private LocalDateTime timestamp;
                private Object data;

                // Getters and setters
                public String getType() { return type; }
                public void setType(String type) { this.type = type; }

                public LocalDateTime getTimestamp() { return timestamp; }
                public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

                public Object getData() { return data; }
                public void setData(Object data) { this.data = data; }
            }
        }