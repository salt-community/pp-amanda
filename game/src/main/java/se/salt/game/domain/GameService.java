package se.salt.game.domain;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.salt.game.domain.model.Game;
import se.salt.game.domain.model.Type;
import se.salt.game.http.exception.NotFoundException;

import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class GameService {

    private final GameRepository repo;

    public Game initializeGame(String sessionId, Type type) {
        String gameId = UUID.randomUUID().toString();

        Instant startTime = Instant.now();
        Instant joinDeadLine = startTime.plusSeconds(90);
        Instant endTime = startTime.plusSeconds(1600);

        Game createdGame = new Game(
            gameId,
            sessionId,
            type,
            startTime,
            joinDeadLine,
            endTime,
            new HashMap<>()
        );

        repo.save(createdGame);

        return createdGame;

    }

    public Game gameStatus(String sessionId) {
        return repo.findBySessionId(sessionId)
            .orElseThrow(() ->
                new NotFoundException("Session with ID: %s not found".formatted(sessionId)));
    }
}
