package se.salt.lobby.domain;

import java.time.Instant;

public record Session(
    String sessionId,
    Instant createdAt,
    Long expiredAt
) {
}
