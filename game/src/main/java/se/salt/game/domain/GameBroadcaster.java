package se.salt.game.domain;

import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import se.salt.game.domain.model.Game;

import java.util.Map;

@Service
@AllArgsConstructor
public class GameBroadcaster {

    private final SimpMessagingTemplate template;

    public void sendCountdown(String gameId, int seconds) {
        template.convertAndSend("/topic/game/" + gameId + "/countdown",
            Map.of("eventType", "COUNTDOWN_STARTED", "seconds", seconds));
    }

    public void sendRound(String gameId, Map<String, Double> scores, int row, int col) {
        template.convertAndSend("/topic/game/" + gameId,
            Map.of(
                "row", row,
                "col", col,
                "scores", scores
            )
        );
    }


    public void sendResults(String gameId, Game game) {
        template.convertAndSend("/topic/game/" + gameId + "/results", game);
    }

    public void sendGameOver(String gameId) {
        template.convertAndSend("/topic/game/" + gameId + "/over",
            Map.of("event", "GAME_OVER", "gameId", gameId));
    }
}

