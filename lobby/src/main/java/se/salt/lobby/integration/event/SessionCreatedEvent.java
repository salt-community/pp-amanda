package se.salt.lobby.integration.event;

import lombok.Builder;

@Builder
public record SessionCreatedEvent(
    String eventType,
    String sessionId,
    String timestamp
) {

}
