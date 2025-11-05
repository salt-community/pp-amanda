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
     */
    public static Map<String, AttributeValue> toItem(Game game) {
        Map<String, AttributeValue> item = new HashMap<>();

        item.put("gameId", AttributeValue.fromS(game.gameId()));
        item.put("sessionId", AttributeValue.fromS(game.sessionId()));
        item.put("type", AttributeValue.fromS(game.type().name()));

        if (game.joinDeadline() != null) {
            item.put("joinDeadline", AttributeValue.fromS(game.joinDeadline().toString()));
        }
        if (game.ttl() != null) {
            item.put("ttl", AttributeValue.fromN(game.ttl().toString()));
        }

        // Convert players map (if any)
        if (game.players() != null && !game.players().isEmpty()) {
            item.put("players", AttributeValue.fromM(
                game.players().entrySet().stream()
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> AttributeValue.fromN(e.getValue().toString())
                    ))
            ));
        }

        return item;
    }

    /**
     * Converts a DynamoDB item map back into a Game domain object.
     */
    public static Game fromItem(Map<String, AttributeValue> item) {
        Map<String, Double> players = Optional.ofNullable(item.get("players"))
            .map(AttributeValue::m)
            .orElse(Map.of())
            .entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> Double.parseDouble(e.getValue().n())
            ));

        Type type = Optional.ofNullable(item.get("type"))
            .map(AttributeValue::s)
            .map(Type::valueOf)
            .orElse(Type.REACTION);

        Instant joinDeadline = Optional.ofNullable(item.get("joinDeadline"))
            .map(AttributeValue::s)
            .map(Instant::parse)
            .orElse(null);

        Long ttl = Optional.ofNullable(item.get("ttl"))
            .map(AttributeValue::n)
            .map(Long::parseLong)
            .orElse(null);

        return Game.builder()
            .gameId(item.get("gameId").s())
            .sessionId(item.get("sessionId").s())
            .type(type)
            .joinDeadline(joinDeadline)
            .ttl(ttl)
            .players(players)
            .build();
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
