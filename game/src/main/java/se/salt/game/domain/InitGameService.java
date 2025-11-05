package se.salt.game.domain;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.salt.game.domain.model.Game;
import se.salt.game.domain.model.Type;
import se.salt.game.http.exception.DeadlinePassedException;
import se.salt.game.http.exception.NotFoundException;

import java.time.Instant;

@Slf4j
@Service
@AllArgsConstructor
public class InitGameService {

    private final GameRepository repo;

    public void initGame(String sessionId) {
        Game game = getGameBySessionId(sessionId);

        if (game.joinDeadline() != null && game.ttl() != null) {
            log.info("Game for session {} is already initialized", sessionId);
            return;
        }

        Instant now = Instant.now();
        Instant joinDeadline = now.plusSeconds(40);
        Long ttl = now.plusSeconds(3600).getEpochSecond();

        Game updated = game.toBuilder()
            .type(Type.REACTION)
            .joinDeadline(joinDeadline)
            .ttl(ttl)
            .build();

        repo.saveNewGame(updated);

        log.info("Initialized new game for session {} with gameId={} (joinDeadline={}, ttl={})",
            sessionId, updated.gameId(), joinDeadline, ttl);

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

}
