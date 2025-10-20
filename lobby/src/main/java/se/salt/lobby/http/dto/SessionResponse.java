package se.salt.lobby.http.dto;

import se.salt.lobby.domain.Session;

import java.time.Instant;

public record SessionResponse(
    String sessionId,
    String sessionName,
    Instant createdAt,
    long expiredAt
) {

    public static SessionResponse fromSession(Session session) {
        return new SessionResponse(
            session.id(),
            session.name(),
            session.createdAt(),
            session.expiredAt()
        );
    }
}
