package se.salt.game.http;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.salt.game.domain.GameService;
import se.salt.game.domain.model.Player;

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


}
