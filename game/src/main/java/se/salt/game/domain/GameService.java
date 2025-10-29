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
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class GameService {

    private final GameRepository repo;

    public Game setGameTypeInSession(String sessionId, Type type) {
        Game existing = getGame(sessionId);

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

        repo.updateGameDetails(updated);
        log.info("Game in session {} updated: type={}, joinDeadline={}, endTime={}",
            sessionId, type, joinDeadline, endTime);

        return updated;

    }

    public Game gameStatus(String sessionId) {
        return getGame(sessionId);
    }

    public Game addPlayer(String sessionId, String name) {
        Game game = getGame(sessionId);

        if (Instant.now().isAfter(game.joinDeadline())) {
            throw new DeadlinePassedException(sessionId);
        }

        Map<String, Double> updatedPlayers = new HashMap<>(game.players());
        updatedPlayers.putIfAbsent(name, 0.0);

        Game updated = game.toBuilder().players(updatedPlayers).build();
        repo.addPlayer(updated.gameId(), name);

        log.info("Player '{}' joined game for session {}", name, sessionId);
        return updated;
    }

    private Game getGame(String sessionId) {
        return repo.findBySessionId(sessionId)
            .orElseThrow(() ->
                new NotFoundException("Session with ID: %s not found".formatted(sessionId)));
    }
}
