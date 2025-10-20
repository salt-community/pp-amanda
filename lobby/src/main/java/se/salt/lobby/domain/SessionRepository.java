package se.salt.lobby.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

/**
 * Repository class for managing Session data in DynamoDB.
 * <p>
 * This class handles basic operations for the "Sessions" table,
 * such as saving a new session and retrieving one by its ID.
 * <p>
 * The DynamoDbClient is injected and used directly to build and execute
 * low-level PutItem and GetItem requests.
 * <p>
 * Each session record is stored with a string ID, name, creation timestamp,
 * and an expiration time (used for TTL cleanup in DynamoDB).
 */

@Repository
@RequiredArgsConstructor
public class SessionRepository {

    private final DynamoDbClient dynamoDb;
    private static final String TABLE_NAME = "Sessions";

    public void save(Session session) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(TABLE_NAME)
            .item(Map.of(
                "id", AttributeValue.fromS(session.id()),
                "name", AttributeValue.fromS(session.name()),
                "createdAt", AttributeValue.fromS(session.createdAt().toString()),
                "expiredAt", AttributeValue.fromN(String.valueOf(session.expiredAt()))
            ))
            .build();
        dynamoDb.putItem(request);
    }

    public Optional<Session> findById(String id) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName(TABLE_NAME)
            .key(Map.of("id", AttributeValue.fromS(id)))
            .build();

        Map<String, AttributeValue> item = dynamoDb.getItem(request).item();
        if (item == null || item.isEmpty()) return Optional.empty();

        Session session = new Session(
            item.get("id").s(),
            item.get("name").s(),
            Instant.parse(item.get("createdAt").s()),
            Long.parseLong(item.get("expiredAt").n())
        );
        return Optional.of(session);
    }
}

