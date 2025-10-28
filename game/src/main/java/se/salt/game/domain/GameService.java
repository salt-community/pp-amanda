package se.salt.game.domain;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.salt.game.domain.model.Game;
import se.salt.game.domain.model.Type;
import se.salt.game.http.exception.NotFoundException;

import java.time.Instant;
import java.util.HashMap;

@Slf4j
@Service
@AllArgsConstructor
public class GameService {

    private final GameRepository repo;

    public Game initializeGame(String sessionId, Type type) {
        Game gameInSession = getGame(sessionId);
        Instant startTime = Instant.now();
        Instant joinDeadLine = startTime.plusSeconds(90);
        Instant endTime = startTime.plusSeconds(1600);

        Game updatedGame = new Game(
            gameInSession.id(),
            sessionId,
            type,
            startTime,
            joinDeadLine,
            endTime,
            new HashMap<>()
        );

        repo.updateGameDetails(updatedGame);

        return updatedGame;

    }

    public Game gameStatus(String sessionId) {
        return getGame(sessionId);
    }

    private Game getGame(String sessionId) {
        return repo.findBySessionId(sessionId)
            .orElseThrow(() ->
                new NotFoundException("Session with ID: %s not found".formatted(sessionId)));
    }
}
