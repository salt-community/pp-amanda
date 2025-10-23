package se.salt.lobby.http.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SessionRequest(

    @NotBlank(message = "Session name cannot be blank")
    @Size(min = 3, max = 20, message = "Session name must be between 3 and 20 characters")
    @Pattern(
        regexp = "^[A-Za-z0-9_-]+$",
        message = "Session name may only contain letters, numbers, underscores, or hyphens"
    )
    String sessionName
) {
}
