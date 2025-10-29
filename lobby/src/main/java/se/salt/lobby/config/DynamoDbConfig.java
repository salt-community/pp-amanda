package se.salt.lobby.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;
import java.util.Optional;

/**
 * Configuration class for setting up a DynamoDB client.
 * <p>
 * This bean creates a single, reusable DynamoDbClient instance
 * that the application can inject wherever database access is needed.
 * <p>
 * The AWS region is read from the environment variable "AWS_REGION"
 * so the same code works both locally and in AWS ECS — it tells
 * the SDK which regional DynamoDB endpoint to connect to.
 * <p>
 * DefaultCredentialsProvider automatically finds credentials from:
 * - Local AWS CLI configuration when running locally
 * - The ECS task role when deployed on AWS
 */

@Configuration
public class DynamoDbConfig {

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.dynamodb.endpoint:}")
    private Optional<String> endpoint;

    @Bean
    public DynamoDbClient dynamoDbClient() {
        var builder = DynamoDbClient.builder()
            .region(Region.of(awsRegion))
            .credentialsProvider(DefaultCredentialsProvider.create());

        endpoint.filter(e -> !e.isBlank())
            .ifPresent(e -> builder.endpointOverride(URI.create(e)));

        return builder.build();
    }
}
