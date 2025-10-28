package se.salt.game.domain;

import se.salt.game.domain.model.Game;
import se.salt.game.domain.model.Type;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

public class GameMapper {

    public static Map<String, AttributeValue> toItem(Game game) {
        Map<String, AttributeValue> playerMap = game.players().isEmpty()
            ? Map.of()
            : game.players().entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> AttributeValue.fromN(e.getValue().toString())
            ));

        return Map.of(
            "id", AttributeValue.fromS(game.id()),
            "sessionId", AttributeValue.fromS(game.sessionId()),
            "type", AttributeValue.fromS(game.type().toString()),
            "startTime", AttributeValue.fromS(game.startTime().toString()),
            "joinDeadLine", AttributeValue.fromS(game.joinDeadLine().toString()),
            "endTime", AttributeValue.fromS(game.endTime().toString()),
            "players", AttributeValue.fromM(playerMap)
        );
    }

    public static Game fromItem(Map<String, AttributeValue> item) {
        Map<String, Double> players = item.containsKey("players")
            ? item.get("players").m().entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> Double.parseDouble(e.getValue().n())
            ))
            : Map.of();

        return new Game(
            item.get("id").s(),
            item.get("sessionId").s(),
            Type.valueOf(item.get("type").s()),
            Instant.parse(item.get("startTime").s()),
            Instant.parse(item.get("joinDeadLine").s()),
            Instant.parse(item.get("endTime").s()),
            players
        );
    }
}
