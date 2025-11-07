package se.salt.game.domain;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.salt.game.domain.model.Game;
import se.salt.game.domain.model.Type;
import se.salt.game.http.exception.DeadlinePassedException;
import se.salt.game.http.exception.NotFoundException;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class InitGameService {

    private final GameRepository repo;

    public void initGame(String sessionId) {
        Optional<Game> existing = repo.findBySessionId(sessionId);

        if (existing.isPresent() && existing.get().joinDeadline() != null) {
            log.warn("⚠️ Game already initialized for sessionId={} (gameId={})",
                sessionId, existing.get().gameId());
            return;
        }

        Game base = existing.orElseThrow(() ->
            new NotFoundException("No base game found for session " + sessionId));

        Instant now = Instant.now();
        Instant joinDeadline = now.plusSeconds(40);
        Long ttl = now.plusSeconds(3600).getEpochSecond();

        Game updated = base.toBuilder()
            .type(Type.REACTION)
            .joinDeadline(joinDeadline)
            .ttl(ttl)
            .build();

        repo.saveNewGame(updated);
        log.info("✅ Initialized gameId={} (sessionId={})", updated.gameId(), sessionId);
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
