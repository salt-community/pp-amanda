package se.salt.lobby.http.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record JoinSessionRequest(
    @NotNull
    @Pattern(
        regexp = "^[A-Z0-9]{4}$",
        message = "Session ID must be 4 uppercase letters or digits"
    )
    String sessionId
) {
}

