package se.salt.game.domain;

import se.salt.game.domain.model.Game;
import se.salt.game.domain.model.Type;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class GameMapper {

    /**
     * Converts a Game domain object to a DynamoDB item map.
     * <p>
     * Summary:
     * Takes a Game object from your domain and converts it into a DynamoDB-compatible
     * Map<String, AttributeValue> structure, ready to be stored in DynamoDB.
     * <p>
     * Logic:
     * 1. Adds required fields (gameId, sessionId, type).
     * 2. Adds optional timestamps (startTime, joinDeadline, endTime) if present.
     * 3. Converts the players map (Map<String, Double>) into a DynamoDB map with numeric values.
     * 4. Ensures an empty players map is stored if none exist.
     * 5. Returns the fully constructed item map for PutItem or UpdateItem.
     */
    public static Map<String, AttributeValue> toItem(Game game) {
        Map<String, AttributeValue> item = new HashMap<>();
        
        item.put("gameId", AttributeValue.fromS(game.gameId()));
        item.put("sessionId", AttributeValue.fromS(game.sessionId()));
        item.put("type", AttributeValue.fromS(game.type().name()));

        if (game.startTime() != null) {
            item.put("startTime", AttributeValue.fromS(game.startTime().toString()));
        }
        if (game.joinDeadline() != null) {
            item.put("joinDeadline", AttributeValue.fromS(game.joinDeadline().toString()));
        }
        if (game.endTime() != null) {
            item.put("endTime", AttributeValue.fromS(game.endTime().toString()));
        }

        // Players map (converted to DynamoDB Map<String, AttributeValue>)
        if (game.players() != null && !game.players().isEmpty()) {
            item.put("players", AttributeValue.fromM(
                game.players().entrySet().stream()
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> AttributeValue.fromN(e.getValue().toString())
                    ))
            ));
        } else {
            item.put("players", AttributeValue.fromM(Map.of()));
        }

        return item;
    }

    /**
     * Converts a DynamoDB item map back into a Game domain object.
     */
    public static Game fromItem(Map<String, AttributeValue> item) {
        Map<String, Double> players = item.containsKey("players") && item.get("players").m() != null
            ? item.get("players").m().entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> Double.parseDouble(e.getValue().n())
            ))
            : Map.of();

        Type type = Optional.ofNullable(item.get("type"))
            .map(AttributeValue::s)
            .map(Type::valueOf)
            .orElse(Type.NA);

        Instant startTime = Optional.ofNullable(item.get("startTime"))
            .map(AttributeValue::s)
            .map(Instant::parse)
            .orElse(null);

        Instant joinDeadline = Optional.ofNullable(item.get("joinDeadline"))
            .map(AttributeValue::s)
            .map(Instant::parse)
            .orElse(null);

        Instant endTime = Optional.ofNullable(item.get("endTime"))
            .map(AttributeValue::s)
            .map(Instant::parse)
            .orElse(null);

        return new Game(
            item.get("gameId").s(),
            item.get("sessionId").s(),
            type,
            startTime,
            joinDeadline,
            endTime,
            players
        );
    }

    /**
     * Utility for building nested player map update expressions.
     */
    public static Map<String, String> playerAttributeNames(String playerName) {
        return Map.of(
            "#players", "players",
            "#playerName", playerName
        );
    }
}
