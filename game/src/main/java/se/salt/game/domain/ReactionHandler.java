package se.salt.game.domain;

import org.springframework.stereotype.Service;
import se.salt.game.domain.model.Game;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReactionHandler {

    public Game applyReaction(Game game, String playerName) {
        Map<String, Double> scores = new HashMap<>(game.players());
        scores.merge(playerName, 1.0, Double::sum);

        return game.toBuilder()
            .players(scores)
            .build();
    }
}