package se.salt.game.domain;

import org.springframework.stereotype.Service;
import se.salt.game.domain.model.Game;
import se.salt.game.domain.model.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ReactionHandler {

    private final Map<String, Integer> streaks = new ConcurrentHashMap<>();


    public Game applyReaction(Game game, Player reaction) {
        String playerName = reaction.playerName();
        long rt = reaction.reactionTimestamp() - reaction.activatedAt();

        double base = scoreForReaction(rt);
        //TODO tweak scoring
        double bonus = 0;
        if (rt < 600) {
            int streak = streaks.getOrDefault(playerName, 0) + 1;
            streaks.put(playerName, streak);
            bonus = Math.min(streak - 1, 3);
        } else {
            streaks.put(playerName, 0);
        }

        double total = base + bonus;

        Map<String, Double> scores = new HashMap<>(game.players());
        scores.merge(playerName, total, Double::sum);

        return game.toBuilder()
            .players(scores)
            .build();
    }


    private double scoreForReaction(long rt) {
        if (rt < 200) return 1000;
        if (rt < 350) return 400;
        if (rt < 600) return 300;
        if (rt < 900) return 100;
        return 0;
    }
}