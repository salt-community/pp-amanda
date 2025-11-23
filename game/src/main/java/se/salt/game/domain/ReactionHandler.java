package se.salt.game.domain;

import org.springframework.stereotype.Service;
import se.salt.game.domain.model.Game;
import se.salt.game.domain.model.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles scoring for reaction events:
 * - Calculates base score from reaction time
 * - Tracks streaks of fast reactions
 * - Applies streak bonus (+10 per streak, max 100)
 * - Updates the player's total score in the Game
 */
@Service
public class ReactionHandler {

    // Current streak count per player (consecutive fast reactions)
    private final Map<String, Integer> streaks = new ConcurrentHashMap<>();

    /**
     * Applies scoring for a single player reaction.
     *
     * @param game     current game state
     * @param reaction player reaction timestamps
     * @return updated Game with recalculated player scores
     */
    public Game applyReaction(Game game, Player reaction) {
        String playerName = reaction.playerName();
        long reactionTime = reaction.reactionTimestamp() - reaction.activatedAt();

        double baseScore = scoreForReaction(reactionTime);
        double bonus = 0;

        // Fast reactions (< 850 ms) increase streak → gives bonus
        if (reactionTime < 850) {
            int streak = streaks.getOrDefault(playerName, 0) + 1;
            streaks.put(playerName, streak);

            // +10 points per streak step, max streak counted = 10
            bonus = Math.min(streak, 10) * 10;
        } else {
            // Slow reaction resets streak
            streaks.put(playerName, 0);
        }

        double total = baseScore + bonus;

        Map<String, Double> scores = new HashMap<>(game.players());
        scores.merge(playerName, total, Double::sum);

        return game.toBuilder()
            .players(scores)
            .build();
    }

    /**
     * Base score based on reaction time.
     */
    private double scoreForReaction(long rt) {
        if (rt < 200) return 1000;
        if (rt < 350) return 400;
        if (rt < 600) return 300;
        if (rt < 900) return 100;
        return 0;
    }
}