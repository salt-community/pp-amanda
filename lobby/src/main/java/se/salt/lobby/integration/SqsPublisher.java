package se.salt.lobby.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.salt.lobby.integration.event.SessionCreatedEvent;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqsPublisher {

    private final SqsClient sqsClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    public void publishSessionCreatedEvent(String sessionId) {
        try {
            SessionCreatedEvent event = SessionCreatedEvent.builder()
                .eventType("session.created.v1")
                .sessionId(sessionId)
                .timestamp(Instant.now().toString())
                .build();

            String body = objectMapper.writeValueAsString(event);

            sqsClient.sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(body)
                .build());

            log.info("Sent SQS - SessionCreatedEvent: {}", body);
        } catch (Exception e) {
            log.error("Failed to send SQS event", e);
        }
    }
}
