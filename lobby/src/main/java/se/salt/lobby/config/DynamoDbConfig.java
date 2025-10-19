package se.salt.lobby.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

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

    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
            .region(Region.of(System.getenv("AWS_REGION")))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();
    }
}
