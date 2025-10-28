package se.salt.game.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import se.salt.game.domain.model.Game;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GameRepository {

    private static final String TABLE_NAME = "Games";

    private final DynamoDbClient dynamoDb;

    public void save(Game game) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(TABLE_NAME)
            .item(GameMapper.toItem(game))
            .build();
        dynamoDb.putItem(request);
    }

    public Optional<Game> findById(String id) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName(TABLE_NAME)
            .key(Map.of("id", AttributeValue.fromS(id)))
            .build();

        Map<String, AttributeValue> item = dynamoDb.getItem(request).item();
        if (item == null || item.isEmpty()) return Optional.empty();

        return Optional.of(GameMapper.fromItem(item));
    }

    public Optional<Game> findBySessionId(String sessionId) {
        QueryRequest query = QueryRequest.builder()
            .tableName(TABLE_NAME)
            .indexName("SessionIndex")
            .keyConditionExpression("sessionId = :sid")
            .expressionAttributeValues(Map.of(":sid", AttributeValue.fromS(sessionId)))
            .build();

        QueryResponse response = dynamoDb.query(query);

        if (response.count() == 0) return Optional.empty();

        Map<String, AttributeValue> item = response.items().get(0);
        return Optional.of(GameMapper.fromItem(item));
    }
}
