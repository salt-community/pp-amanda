package se.salt.game.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import se.salt.game.domain.model.Game;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.Map;

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

}
