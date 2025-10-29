package se.salt.game.domain;

import se.salt.game.domain.model.Game;
import se.salt.game.domain.model.Type;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
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
            "joinDeadline", AttributeValue.fromS(game.joinDeadline().toString()),
            "endTime", AttributeValue.fromS(game.endTime().toString()),
            "players", AttributeValue.fromM(playerMap)
        );
    }

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

        Instant joinDeadLine = Optional.ofNullable(item.get("joinDeadline"))
            .map(AttributeValue::s)
            .map(Instant::parse)
            .orElse(null);

        Instant endTime = Optional.ofNullable(item.get("endTime"))
            .map(AttributeValue::s)
            .map(Instant::parse)
            .orElse(null);

        return new Game(
            item.get("id").s(),
            item.get("sessionId").s(),
            type,
            startTime,
            joinDeadLine,
            endTime,
            players
        );
    }

    public static AttributeValue doubleToAttribute(double value) {
        return AttributeValue.fromN(Double.toString(value));
    }

    public static AttributeValue stringToAttribute(String value) {
        return AttributeValue.fromS(value);
    }

    public static Map<String, String> playerAttributeNames(String playerName) {
        return Map.of(
            "#players", "players",
            "#playerName", playerName
        );
    }
}
