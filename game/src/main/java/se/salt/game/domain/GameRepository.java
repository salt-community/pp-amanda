package se.salt.game.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import se.salt.game.domain.model.Game;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GameRepository {

    private static final String TABLE_NAME = "Games";
    private final DynamoDbClient dynamoDb;

    public void updateGameDetails(Game game) {
        Map<String, AttributeValue> item = GameMapper.toItem(game);

        AttributeValue gameIdAttr = item.get("gameId");
        if (gameIdAttr == null) {
            throw new IllegalArgumentException("Cannot update game without gameId — received: " + game);
        }

        Map<String, AttributeValue> key = Map.of("gameId", gameIdAttr);

        Map<String, AttributeValue> values = new HashMap<>();
        Map<String, String> names = new HashMap<>();
        List<String> setParts = new ArrayList<>();

        if (item.get("type") != null) {
            names.put("#type", "type");
            values.put(":t", item.get("type"));
            setParts.add("#type = :t");
        }
        if (item.get("startTime") != null) {
            names.put("#startTime", "startTime");
            values.put(":s", item.get("startTime"));
            setParts.add("#startTime = :s");
        }
        if (item.get("joinDeadline") != null) {
            names.put("#joinDeadline", "joinDeadline");
            values.put(":j", item.get("joinDeadline"));
            setParts.add("#joinDeadline = :j");
        }
        if (item.get("endTime") != null) {
            names.put("#endTime", "endTime");
            values.put(":e", item.get("endTime"));
            setParts.add("#endTime = :e");
        }
        if (item.get("players") != null) {
            names.put("#players", "players");
            values.put(":p", item.get("players"));
            setParts.add("#players = :p");
        }

        String updateExpression = "SET " + String.join(", ", setParts);

        UpdateItemRequest updateRequest = UpdateItemRequest.builder()
            .tableName(TABLE_NAME)
            .key(key)
            .updateExpression(updateExpression)
            .expressionAttributeNames(names)
            .expressionAttributeValues(values)
            .build();

        dynamoDb.updateItem(updateRequest);
    }

    public void addPlayer(String gameId, String playerName) {
        UpdateItemRequest updateRequest = UpdateItemRequest.builder()
            .tableName(TABLE_NAME)
            .key(Map.of("gameId", AttributeValue.fromS(gameId)))
            .updateExpression("SET #players.#playerName = :initial")
            .expressionAttributeNames(GameMapper.playerAttributeNames(playerName))
            .expressionAttributeValues(Map.of(":initial", AttributeValue.fromN("0")))
            .build();

        dynamoDb.updateItem(updateRequest);
    }

    public void updateResponseTime(String gameId, String playerName, double responseTime) {
        UpdateItemRequest updateRequest = UpdateItemRequest.builder()
            .tableName(TABLE_NAME)
            .key(Map.of("gameId", AttributeValue.fromS(gameId)))
            .updateExpression("SET #players.#playerName = :time")
            .expressionAttributeNames(GameMapper.playerAttributeNames(playerName))
            .expressionAttributeValues(Map.of(":time", AttributeValue.fromN(Double.toString(responseTime))))
            .build();

        dynamoDb.updateItem(updateRequest);
    }

    public Optional<Game> findByGameId(String gameId) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName(TABLE_NAME)
            .key(Map.of("gameId", AttributeValue.fromS(gameId)))
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

    public void saveFromLambda(String gameId, String sessionId) {
        Map<String, AttributeValue> item = Map.of(
            "gameId", AttributeValue.fromS(gameId),
            "sessionId", AttributeValue.fromS(sessionId)
        );

        PutItemRequest request = PutItemRequest.builder()
            .tableName(TABLE_NAME)
            .item(item)
            .conditionExpression("attribute_not_exists(gameId)")
            .build();

        dynamoDb.putItem(request);
    }


}
