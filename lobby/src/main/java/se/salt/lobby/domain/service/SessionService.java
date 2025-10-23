package se.salt.lobby.domain.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.salt.lobby.domain.Session;
import se.salt.lobby.domain.SessionRepository;
import se.salt.lobby.http.dto.JoinSessionRequest;
import se.salt.lobby.http.dto.SessionRequest;
import se.salt.lobby.http.exception.NotFoundException;

import java.time.Instant;
import java.util.Random;

@Slf4j
@Service
@AllArgsConstructor
public class SessionService {

    private final SessionRepository repo;

    public Session createSession(SessionRequest req) {
        String id = generateRandomId();
        log.info("Creating new session '{}' with ID: {}", req.sessionName(), id);

        Instant createdAt = Instant.now();
        long timeToLive = 1800L;
        long expiresAt = createdAt.plusSeconds(timeToLive).getEpochSecond();

        Session createdSession = new Session(
            id,
            req.sessionName(),
            createdAt,
            expiresAt);

        repo.save(createdSession);

        return createdSession;
    }

    public Session joinSession(JoinSessionRequest sessionId) {
        return repo
            .findById(sessionId.sessionId())
            .orElseThrow(() ->
                new NotFoundException(
                    "Session with ID: %s not found".formatted(sessionId)
                )
            );
    }

    private String generateRandomId() {
        String characters = "0123456789";
        StringBuilder id = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 4; i++) {
            int index = random.nextInt(characters.length());
            id.append(characters.charAt(index));
        }

        return id.toString();
    }
}
