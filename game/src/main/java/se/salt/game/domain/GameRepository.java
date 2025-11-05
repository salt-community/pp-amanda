package se.salt.game.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import se.salt.game.domain.model.Game;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GameRepository {

    private static final String TABLE_NAME = "Games";

    private final DynamoDbClient dynamoDb;

    /**
     * Creates a Game item sent through SQS and consumed by Lambda
     * Sets all default values it gets from service method.
     */
    public void saveNewGame(Game game) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("gameId", AttributeValue.fromS(game.gameId()));
        item.put("sessionId", AttributeValue.fromS(game.sessionId()));
        item.put("type", AttributeValue.fromS(game.type().name()));
        item.put("joinDeadline", AttributeValue.fromS(game.joinDeadline().toString()));
        item.put("ttl", AttributeValue.fromN(game.ttl().toString()));

        PutItemRequest request = PutItemRequest.builder()
            .tableName(TABLE_NAME)
            .item(item)
            .conditionExpression("attribute_not_exists(gameId)")
            .build();

        dynamoDb.putItem(request);
    }

    /**
     * Adds a player to the players map (idempotent — won't overwrite existing player).
     * 1) creates empty map is it doesn't exist
     * 2) adds the player name and sets the players score to 0.
     * Returns the updated Game object with the info about the player(s)
     */
    public Game addPlayer(String gameId, String playerName) {
        Map<String, AttributeValue> key = Map.of("gameId", AttributeValue.fromS(gameId));

        UpdateItemRequest ensureMap = UpdateItemRequest.builder()
            .tableName(TABLE_NAME)
            .key(key)
            .updateExpression("SET #players = if_not_exists(#players, :emptyMap)")
            .expressionAttributeNames(Map.of("#players", "players"))
            .expressionAttributeValues(Map.of(":emptyMap", AttributeValue.fromM(Map.of())))
            .build();
        dynamoDb.updateItem(ensureMap);

        Map<String, String> names = Map.of(
            "#players", "players",
            "#name", playerName
        );

        UpdateItemRequest addPlayer = UpdateItemRequest.builder()
            .tableName(TABLE_NAME)
            .key(key)
            .updateExpression("SET #players.#name = if_not_exists(#players.#name, :initial)")
            .expressionAttributeNames(names)
            .expressionAttributeValues(Map.of(":initial", AttributeValue.fromN("0")))
            .returnValues(ReturnValue.ALL_NEW)
            .build();

        Map<String, AttributeValue> updatedItem = dynamoDb.updateItem(addPlayer).attributes();
        return GameMapper.fromItem(updatedItem);
    }


    /**
     * Updates a specific player's response time and overwrites previous default value
     */
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

    /**
     * Finds a Game by its gameId (primary key).
     */
    public Optional<Game> findByGameId(String gameId) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName(TABLE_NAME)
            .key(Map.of("gameId", AttributeValue.fromS(gameId)))
            .build();

        Map<String, AttributeValue> item = dynamoDb.getItem(request).item();
        if (item == null || item.isEmpty()) return Optional.empty();
        return Optional.of(GameMapper.fromItem(item));
    }

    /**
     * Finds a Game by its sessionId (via GSI SessionIndex).
     */
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
