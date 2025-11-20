package se.salt.game.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import se.salt.game.domain.model.TopScore;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TopScoreRepository {

    private static final String TABLE_NAME = "TopScores";

    private final DynamoDbClient dynamoDb;

    public void save(TopScore score) {
        Map<String, AttributeValue> item = Map.of(
            "pk", AttributeValue.fromS("TOP"),
            "sk", AttributeValue.fromS(UUID.randomUUID().toString()),
            "playerName", AttributeValue.fromS(score.playerName()),
            "score", AttributeValue.fromN(Integer.toString(score.score()))
        );

        PutItemRequest request = PutItemRequest.builder()
            .tableName(TABLE_NAME)
            .item(item)
            .build();

        dynamoDb.putItem(request);
        log.info("💾 Saved TopScore: {} -> {}", score.playerName(), score.score());
    }

    public List<TopScore> findAll() {
        ScanRequest scan = ScanRequest.builder()
            .tableName(TABLE_NAME)
            .build();

        ScanResponse response = dynamoDb.scan(scan);

        return response.items().stream()
            .map(item -> new TopScore(
                item.get("playerName").s(),
                Integer.parseInt(item.get("score").n())
            ))
            .collect(Collectors.toList());
    }
}
