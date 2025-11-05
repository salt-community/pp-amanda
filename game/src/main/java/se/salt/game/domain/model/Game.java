package se.salt.game.domain.model;

import lombok.Builder;

import java.time.Instant;
import java.util.Map;

@Builder(toBuilder = true)
public record Game(
    String gameId,
    String sessionId,
    Type type,
    Instant joinDeadline,
    Long ttl,
    Map<String, Double> players
) {
}
