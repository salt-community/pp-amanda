package se.salt.game.http.dto;

import se.salt.game.domain.model.Game;
import se.salt.game.domain.model.Type;

import java.time.Instant;

public record GameResponse(
    String gameId,
    String sessionId,
    Type type,
    Instant joinTimeLimit,
    long ttl
) {

    public static GameResponse fromGame(Game game) {
        return new GameResponse(
            game.gameId(),
            game.sessionId(),
            game.type(),
            game.joinDeadline(),
            game.ttl());
    }
}
