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

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
public class SessionCreatedHandler implements RequestHandler<SQSEvent, Void> {

    private final ObjectMapper mapper = new ObjectMapper();
    private final GameRepository repository;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public SessionCreatedHandler() {
        DynamoDbClient dynamoDb = DynamoDbClient.builder()
            .region(Region.EU_NORTH_1)
            //.endpointOverride(URI.create("http://host.docker.internal:4566"))
            .endpointOverride(URI.create("http://localstack:4566"))
            .build();

        this.repository = new GameRepository(dynamoDb);
    }

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        System.out.println("🚀 Lambda STARTED!");
        event.getRecords().forEach(record -> {
            try {
                System.out.println("📦 Parsing record...");
                SessionCreatedEvent message =
                    mapper.readValue(record.getBody(), SessionCreatedEvent.class);
                String sessionId = message.sessionId();

                log.info("Received session-created event for session {}", sessionId);

                String gameId = repository.createNewGameForSession(sessionId);
                log.info("Created placeholder game with sessionId {}", sessionId);

                //  let DynamoDB GSI sync)
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.warn("Thread sleep interrupted: {}", ie.getMessage());
                }
                // 2️⃣ Trigger Game Service init endpoint
                String body = String.format("{\"sessionId\":\"%s\", \"gameId\":\"%s\"}", sessionId, gameId);


                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://host.docker.internal:8081/game/init"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

                try {
                    HttpResponse<String> response =
                        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    log.info("Called GameService /game/init for session {} (status: {})",
                        sessionId, response.statusCode());
                } catch (Exception httpEx) {
                    log.warn("GameService /game/init not reachable for session {}: {}",
                        sessionId, httpEx.getMessage());
                }

            } catch (Exception e) {
                log.error("Failed to process message: {}", e.getMessage(), e);
                context.getLogger().log("Lambda error: " + e.getMessage());
            }
        });
        System.out.println("🏁 Lambda DONE");
        return null;
    }
}
