package se.salt.lobby.http.dto;

import se.salt.lobby.domain.Session;

import java.time.Instant;

public record SessionResponse(
    String sessionId,
    Instant createdAt,
    long expiredAt
) {

    public static SessionResponse fromSession(Session session) {
        return new SessionResponse(
            session.sessionId(),
            session.createdAt(),
            session.expiredAt()
        );
    }
}
