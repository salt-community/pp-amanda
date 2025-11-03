package se.salt.game.http;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.salt.game.domain.GameService;

@Slf4j
@RestController
@RequestMapping("/game")
@AllArgsConstructor
public class GameController {

    private final GameService service;

    @PostMapping("/{gameId}/start")
    public ResponseEntity<Void> startGame(@PathVariable String gameId) {
        log.info("Starting game {}", gameId);
        service.startGame(gameId);
        return ResponseEntity.ok().build();
    }


}
