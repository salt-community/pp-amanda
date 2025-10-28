package se.salt.lambda.game.model;

public record SessionCreatedEvent(
    String eventType,
    String sessionId,
    String timestamp
) {}
