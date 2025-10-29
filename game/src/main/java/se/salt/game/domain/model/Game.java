package se.salt.game.domain.model;

import lombok.Builder;

import java.time.Instant;
import java.util.Map;

@Builder(toBuilder = true)
public record Game(
    String gameId,
    String sessionId,
    Type type,
    Instant startTime,
    Instant joinDeadline,
    Instant endTime,
    Map<String, Double> players
) {
}
