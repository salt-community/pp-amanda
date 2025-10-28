package se.salt.lambda.game.repository;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.Map;
import java.util.UUID;

public class GameRepository {

    private static final String TABLE_NAME = "Games";

    private final DynamoDbClient dynamoDb;

    public GameRepository(DynamoDbClient dynamoDb) {
        this.dynamoDb = dynamoDb;
    }

    public void saveGameWithSessionId(String sessionId) {
        String id = UUID.randomUUID().toString();
        PutItemRequest request = PutItemRequest.builder()
            .tableName(TABLE_NAME)
            .item(Map.of(
                "id", AttributeValue.fromS(id),
                "sessionId", AttributeValue.fromS(sessionId)
            ))
            .build();

        dynamoDb.putItem(request);
    }
}
