package se.salt.game.domain.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record Player(
    String gameId,
    String playerName,
    long reactionTimeMs
) {
}
