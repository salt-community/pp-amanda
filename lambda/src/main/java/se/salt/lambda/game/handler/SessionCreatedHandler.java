package se.salt.lambda.game.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import se.salt.lambda.game.model.SessionCreatedEvent;
import se.salt.lambda.game.repository.GameRepository;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Slf4j
public class SessionCreatedHandler implements RequestHandler<SQSEvent, Void> {

    private final ObjectMapper mapper = new ObjectMapper();
    private final GameRepository repository;

    public SessionCreatedHandler() {
        DynamoDbClient dynamoDb = DynamoDbClient.builder()
            .region(Region.EU_NORTH_1)
            .build();
        this.repository = new GameRepository(dynamoDb);
    }

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        event.getRecords().forEach(record -> {
            try {
                // Deserialize message from queue
                SessionCreatedEvent message =
                    mapper.readValue(record.getBody(), SessionCreatedEvent.class);

                repository.saveGameWithSessionId(message.sessionId());

                context.getLogger().log("Created Game skeleton for session: " + message.sessionId());
            } catch (Exception e) {
                context.getLogger().log("Failed to process message: " + e.getMessage());
            }
        });
        return null;
    }
}
