package se.salt.game.http;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.salt.game.domain.GameService;
import se.salt.game.domain.model.Player;
import se.salt.game.http.dto.ResultResponse;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/game")
@AllArgsConstructor
public class GameController {

    private final GameService service;

    @MessageMapping("/reaction")
    public void handleReaction(Player reaction, @Header("playerName") String playerName) {
        log.info("Reaction received from player header: {}", playerName);
        // fallback if payload missing name
        if (reaction.playerName() == null) {
            reaction = reaction.toBuilder().playerName(playerName).build();
        }
        service.handleReaction(reaction);
    }

    @GetMapping("/{gameId}/result")
    public ResponseEntity<ResultResponse> gameResult(@PathVariable String gameId) {
        Map<String, Double> scoreBoard = service.getResult(gameId);
        log.info("Game result for game with ID {}", gameId);
        return ResponseEntity.ok(ResultResponse.from(scoreBoard));
    }


}
