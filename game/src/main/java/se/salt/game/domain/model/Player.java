package se.salt.game.domain.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record Player(
    String gameId,
    String playerName,
    int row,
    int col,
    long activatedAt,
    long reactionTimestamp
) {
}
