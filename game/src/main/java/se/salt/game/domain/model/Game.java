package se.salt.game.domain.model;

import java.time.Instant;
import java.util.Map;

public record Game(
    String id,
    String sessionId,
    Type type,
    Instant startTime,
    Instant joinDeadLine,
    Instant endTime,
    Map<String, Double> players
) {
}
