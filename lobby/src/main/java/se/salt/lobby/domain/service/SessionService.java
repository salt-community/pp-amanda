package se.salt.lobby.domain.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.salt.lobby.domain.Session;
import se.salt.lobby.domain.SessionRepository;
import se.salt.lobby.http.dto.JoinSessionRequest;
import se.salt.lobby.http.exception.NotFoundException;
import se.salt.lobby.integration.SqsPublisher;

import java.time.Instant;
import java.util.Random;

@Slf4j
@Service
@AllArgsConstructor
public class SessionService {

    private final SessionRepository repo;

    private final SqsPublisher sqsPublisher;

    public Session generateSession() {
        String id = generateSessionId();
        log.info("Creating new session with ID: {}", id);

        Instant createdAt = Instant.now();
        long timeToLive = 1800L;
        long expiresAt = createdAt.plusSeconds(timeToLive).getEpochSecond();

        Session createdSession = new Session(
            id,
            createdAt,
            expiresAt);

        repo.save(createdSession);

        sqsPublisher.publishSessionCreatedEvent(createdSession.sessionId());

        return createdSession;
    }

    public Session joinSession(JoinSessionRequest sessionId) {
        return repo.findById(sessionId.sessionId())
            .orElseThrow(() ->
                new NotFoundException("Session with ID: %s not found".formatted(sessionId.sessionId()))
            );
    }

    public Session findById(String sessionId) {
        return repo.findById(sessionId)
            .orElseThrow(() ->
                new NotFoundException("Session with ID: %s not found".formatted(sessionId)));
    }

    private String generateSessionId() {
        Random random = new Random();
        int number = random.nextInt(10_000);
        return String.format("%04d", number);
    }
}
