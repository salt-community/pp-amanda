package se.salt.game.domain;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.salt.game.domain.model.Game;
import se.salt.game.domain.model.Type;
import se.salt.game.http.exception.DeadlinePassedException;
import se.salt.game.http.exception.NotFoundException;

import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class InitGameService {

    private final GameRepository repo;

    public Game setGameTypeInSession(String sessionId, Type type) {
        Game existing = getGameBySessionId(sessionId);

        Instant now = Instant.now();
        Instant joinDeadline = now.plusSeconds(90);
        Instant endTime = now.plusSeconds(1600);

        Game updated = existing.toBuilder()
            .type(type)
            .startTime(now)
            .joinDeadline(joinDeadline)
            .endTime(endTime)
            .players(existing.players() != null ? existing.players() : new HashMap<>())
            .build();

        Game saved = repo.updateGameDetails(updated);
        log.info("Game in session {} updated: type={}, joinDeadline={}, endTime={}",
            sessionId, type, joinDeadline, endTime);

        return saved;
    }

    public Game gameStatus(String sessionId) {
        return getGameBySessionId(sessionId);
    }

    public Game addPlayer(String sessionId, String name) {
        Game game = getGameBySessionId(sessionId);
        String gameId = game.gameId();

        Instant deadline = game.joinDeadline();
        if (deadline != null && Instant.now().isAfter(deadline)) {
            throw new DeadlinePassedException(sessionId);
        }

        Game updated = repo.addPlayer(gameId, name);
        log.info("Player '{}' joined game with ID: {}", name, gameId);
        return updated;
    }

    private Game getGameBySessionId(String sessionId) {
        return repo.findBySessionId(sessionId)
            .orElseThrow(() ->
                new NotFoundException("Session with ID: %s not found".formatted(sessionId)));
    }

    public void initGame(String sessionId) {
        String gameId = UUID.randomUUID().toString();
        repo.saveFromLambda(gameId, sessionId);
        log.info("Initialized new game for session {} with gameId: {}", sessionId, gameId);
    }
}
