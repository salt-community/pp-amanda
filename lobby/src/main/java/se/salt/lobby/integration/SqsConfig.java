package se.salt.lobby.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

@Configuration
public class SqsConfig {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.sqs.endpoint:}") // tom som default i prod
    private String sqsEndpoint;

    @Bean
    public SqsClient sqsClient() {
        var builder = SqsClient.builder()
            .region(Region.of(region));

        if (sqsEndpoint != null && !sqsEndpoint.isBlank()) {
            builder.endpointOverride(URI.create(sqsEndpoint));
        }

        return builder.build();
    }
}


