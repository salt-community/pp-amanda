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

    @Value("${aws.sqs.endpoint}")
    private String endpoint;

    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
            .endpointOverride(URI.create(endpoint))
            .region(Region.of(region))
            .build();
    }
}
