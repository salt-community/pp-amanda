package se.salt.game.http.dto;

import se.salt.game.domain.model.Game;
import se.salt.game.domain.model.Type;

import java.time.Instant;

public record GameResponse(
    String gameId,
    String sessionId,
    Type type,
    Instant startTime,
    Instant joinTimeLimit,
    Instant endTime
) {

    public static GameResponse fromGame(Game game) {
        return new GameResponse(
            game.gameId(),
            game.sessionId(),
            game.type(),
            game.startTime(),
            game.joinDeadline(),
            game.endTime());
    }
}
