package se.salt.game.domain.model;

public record Cell(
    int row,
    int col,
    long activatedAt,
    long expiresAt
) {
}