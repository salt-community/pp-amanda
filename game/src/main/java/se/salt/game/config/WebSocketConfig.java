package se.salt.game.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${local.dev.ip:}")
    private String localDevIp;  // tom i PROD

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // PROD origins – statiska = stabilt
        String[] prodOrigins = new String[]{
            "http://localhost:5173",
            "http://frontend:80",
            "http://192.168.XXXX.XX:5173" // TODO ADD CORRECT LAN IP
        };

        // Om du kör lokalt och vill tillåta LAN-IP:
        // ex: sätt i .env LOCAL_DEV_IP=192.168.X.XX
        String[] finalOrigins;
        if (localDevIp != null && !localDevIp.isBlank()) {
            finalOrigins = new String[]{
                prodOrigins[0],
                prodOrigins[1],
                "http://" + localDevIp + ":5173"
            };
        } else {
            finalOrigins = prodOrigins;
        }

        registry.addEndpoint("/ws-game")
            .setAllowedOrigins(finalOrigins);
        // .withSockJS();  // (om du vill aktivera senare)
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }
}
