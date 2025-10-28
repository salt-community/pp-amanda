package se.salt.game.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import se.salt.game.domain.model.Game;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public void updateGameDetails(Game game) {
        UpdateItemRequest updateRequest = UpdateItemRequest.builder()
            .tableName(TABLE_NAME)
            .key(Map.of("id", AttributeValue.fromS(game.id())))
            .updateExpression("SET #type = :t, #startTime = :s, #joinDeadLine = :j, #endTime = :e, #players = :p")
            .expressionAttributeNames(Map.of(
                "#type", "type",
                "#startTime", "startTime",
                "#joinDeadLine", "joinDeadLine",
                "#endTime", "endTime",
                "#players", "players"
            ))
            .expressionAttributeValues(Map.of(
                ":t", AttributeValue.fromS(game.type().toString()),
                ":s", AttributeValue.fromS(game.startTime().toString()),
                ":j", AttributeValue.fromS(game.joinDeadLine().toString()),
                ":e", AttributeValue.fromS(game.endTime().toString()),
                ":p", AttributeValue.fromM(
                    game.players().entrySet().stream()
                        .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> AttributeValue.fromN(e.getValue().toString())
                        ))
                )
            ))
            .build();

        dynamoDb.updateItem(updateRequest);
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
