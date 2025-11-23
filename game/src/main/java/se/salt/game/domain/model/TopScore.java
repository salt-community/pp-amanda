package se.salt.game.domain.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record TopScore(String playerName, int score) {
}
