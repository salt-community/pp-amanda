package se.salt.lobby.config;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${local.dev.ip:}")
    private String localDevIp;  // tom i PROD

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {

        String[] prodOrigins = new String[]{
            "http://localhost:5173",
            "http://192.168.XXXX.XX:5173" // TODO ADD CORRECT LAN IP
        };

        // Om du kör lokalt och vill tillåta LAN-IP:
        // ex: sätt i .env LOCAL_DEV_IP=192.168.X.XX
        String[] finalOrigins;
        if (localDevIp != null && !localDevIp.isBlank()) {
            finalOrigins = new String[]{
                prodOrigins[0],
                "http://" + localDevIp + ":5173"
            };
        } else {
            finalOrigins = prodOrigins;
        }

        registry.addMapping("/**")
            .allowedOrigins(finalOrigins)
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true);
    }
}

