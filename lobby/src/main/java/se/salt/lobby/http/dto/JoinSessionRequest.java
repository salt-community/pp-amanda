package se.salt.lobby.http.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record JoinSessionRequest(
    @NotNull @Min(4) @Max(4) String sessionId
) {
}

// TODO validation add some kind of regEx?
