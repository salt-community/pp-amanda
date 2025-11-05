package se.salt.lambda.game.repository;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameRepository {

    private static final String TABLE_NAME = "Games";
    private final DynamoDbClient dynamoDb;

    public GameRepository(DynamoDbClient dynamoDb) {
        this.dynamoDb = dynamoDb;
    }

    /**
     * Creates a new minimal Game entry when a session is created.
     * Only sets gameId + sessionId — all domain setup happens in Game Service.
     */
    public void createNewGameForSession(String sessionId) {
        String gameId = UUID.randomUUID().toString();

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("gameId", AttributeValue.fromS(gameId));
        item.put("sessionId", AttributeValue.fromS(sessionId));

        PutItemRequest request = PutItemRequest.builder()
            .tableName(TABLE_NAME)
            .item(item)
            .conditionExpression("attribute_not_exists(gameId)")
            .build();

        dynamoDb.putItem(request);
    }

}
