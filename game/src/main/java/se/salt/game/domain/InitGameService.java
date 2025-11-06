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
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class InitGameService {

    private final GameRepository repo;

    public void initGame(String sessionId) {
        Optional<Game> existing = repo.findBySessionId(sessionId);
        if (existing.isPresent()) {
            log.warn("⚠️ Game already initialized for sessionId={} (gameId={})",
                sessionId, existing.get().gameId());
            return;
        }

        Instant now = Instant.now();
        Instant joinDeadline = now.plusSeconds(40);
        Long ttl = now.plusSeconds(3600).getEpochSecond();

        Game game = Game.builder()
            .gameId(UUID.randomUUID().toString())
            .sessionId(sessionId)
            .type(Type.REACTION)
            .joinDeadline(joinDeadline)
            .ttl(ttl)
            .players(new HashMap<>())
            .build();

        repo.saveNewGame(game);
        log.info("✅ Initialized new game for session {} with gameId={} (joinDeadline={}, ttl={})",
            sessionId, game.gameId(), joinDeadline, ttl);
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

    public void initGameByGameId(String gameId) {
        Game game = repo.findByGameId(gameId)
            .orElseThrow(() -> new NotFoundException("GameId %s not found".formatted(gameId)));

        Instant now = Instant.now();
        Game updated = game.toBuilder()
            .type(Type.REACTION)
            .joinDeadline(now.plusSeconds(40))
            .ttl(now.plusSeconds(3600).getEpochSecond())
            .build();

        repo.saveNewGame(updated);
        log.info("✅ Initialized gameId={} (sessionId={})", gameId, updated.sessionId());
    }

}
