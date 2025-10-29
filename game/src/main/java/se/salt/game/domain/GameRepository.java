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

    public void updateGameDetails(Game game) {
        Map<String, AttributeValue> item = GameMapper.toItem(game);
        Map<String, AttributeValue> key = Map.of("id", item.get("id"));

        UpdateItemRequest updateRequest = UpdateItemRequest.builder()
            .tableName(TABLE_NAME)
            .key(key)
            .updateExpression("SET #type = :t, #startTime = :s, #joinDeadline = :j, #endTime = :e, #players = :p")
            .expressionAttributeNames(Map.of(
                "#type", "type",
                "#startTime", "startTime",
                "#joinDeadline", "joinDeadline",
                "#endTime", "endTime",
                "#players", "players"
            ))
            .expressionAttributeValues(Map.of(
                ":t", item.get("type"),
                ":s", item.get("startTime"),
                ":j", item.get("joinDeadline"),
                ":e", item.get("endTime"),
                ":p", item.get("players")
            ))
            .build();

        dynamoDb.updateItem(updateRequest);
    }

    public void addPlayer(String gameId, String playerName) {
        UpdateItemRequest updateRequest = UpdateItemRequest.builder()
            .tableName(TABLE_NAME)
            .key(Map.of("id", AttributeValue.fromS(gameId)))
            .updateExpression("SET #players.#playerName = :initial")
            .expressionAttributeNames(GameMapper.playerAttributeNames(playerName))
            .expressionAttributeValues(Map.of(":initial", AttributeValue.fromN("0")))
            .build();

        dynamoDb.updateItem(updateRequest);
    }

    public void updateResponseTime(String gameId, String playerName, double responseTime) {
        UpdateItemRequest updateRequest = UpdateItemRequest.builder()
            .tableName(TABLE_NAME)
            .key(Map.of("id", AttributeValue.fromS(gameId)))
            .updateExpression("SET #players.#playerName = :time")
            .expressionAttributeNames(GameMapper.playerAttributeNames(playerName))
            .expressionAttributeValues(Map.of(":time", AttributeValue.fromN(Double.toString(responseTime))))
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

        return Optional.of(GameMapper.fromItem(response.items().get(0)));
    }
}
