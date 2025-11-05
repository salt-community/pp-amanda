package se.salt.game.http.dto;

public record InitRequest(
    String sessionId,
    String gameId
) {
}
