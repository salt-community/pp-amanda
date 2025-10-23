package se.salt.game.http.dto;

import se.salt.game.domain.model.Type;

public record GameRequest(
    Type gameType
) {
}
