package se.salt.game.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import se.salt.game.domain.model.Game;
import se.salt.game.domain.model.Type;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

@Repository
@RequiredArgsConstructor
public class GameRepository {

    private static final String TABLE_NAME = "Games";

    private final DynamoDbClient dynamoDb;

    public void save(Game game) {
        Map<String, AttributeValue> playerMap =
            game.players().isEmpty() ?
                Map.of() : game.players().entrySet()
                .stream()
                .collect(toMap(
                    Map.Entry::getKey,
                    e -> AttributeValue.fromN(e.getValue().toString())
                ));

        PutItemRequest request = PutItemRequest.builder()
            .tableName(TABLE_NAME)
            .item(Map.of(
                "id", AttributeValue.fromS(game.id()),
                "sessionId", AttributeValue.fromS(game.sessionId()),
                "type", AttributeValue.fromS(game.type().toString()),
                "startTime", AttributeValue.fromS(game.startTime().toString()),
                "joinDeadLine", AttributeValue.fromS(game.joinDeadLine().toString()),
                "endTime", AttributeValue.fromS(game.endTime().toString()),
                "players", AttributeValue.fromM(playerMap)
            ))
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

        Map<String, Double> players = Optional.ofNullable(item.get("players"))
            .map(AttributeValue::m)
            .orElse(Map.of())
            .entrySet()
            .stream()
            .collect(toMap(
                Map.Entry::getKey,
                e -> Double.parseDouble(e.getValue().n())
            ));

        Game game = new Game(
            item.get("id").s(),
            item.get("sessionId").s(),
            Type.valueOf(item.get("type").s()),
            Instant.parse(item.get("startTime").s()),
            Instant.parse(item.get("joinDeadLine").s()),
            Instant.parse(item.get("endTime").s()),
            players
        );
        return Optional.of(game);
    }
}
