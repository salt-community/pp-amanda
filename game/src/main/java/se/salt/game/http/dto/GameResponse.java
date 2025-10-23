package se.salt.game.http.dto;

import se.salt.game.domain.model.Game;
import se.salt.game.domain.model.Type;

import java.time.Instant;

public record GameResponse(
    String id,
    String sessionId,
    Type type,
    Instant startTime,
    Instant joinTimeLimit,
    Instant endTime
) {

    public static GameResponse fromGame(Game game) {
        return new GameResponse(
            game.id(),
            game.sessionId(),
            game.type(),
            game.startTime(),
            game.joinDeadLine(),
            game.endTime());
    }
}
