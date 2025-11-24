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
            "https://frontend-alb-1896419215.eu-north-1.elb.amazonaws.com",
            "http://frontend-alb-1896419215.eu-north-1.elb.amazonaws.com",
            "http://localhost:5173"
        };

        // Om du kör lokalt och vill tillåta LAN-IP:
        // ex: sätt i .env LOCAL_DEV_IP=192.168.X.XX
        String[] finalOrigins;
        if (localDevIp != null && !localDevIp.isBlank()) {
            finalOrigins = new String[]{
                prodOrigins[0],
                prodOrigins[1],
                prodOrigins[2],
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

