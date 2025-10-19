package se.salt.lobby.domain;

import java.time.Instant;

public record Session(
    String id,
    String name,
    Instant createdAt,
    Long expiredAt
) {
}
