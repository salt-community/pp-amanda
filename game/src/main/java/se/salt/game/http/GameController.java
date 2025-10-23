package se.salt.game.http;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.salt.game.domain.GameService;
import se.salt.game.domain.model.Game;
import se.salt.game.http.dto.GameRequest;
import se.salt.game.http.dto.GameResponse;

@Slf4j
@RestController
@RequestMapping("/game")
@AllArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping("/{sessionId}/initialize")
    public ResponseEntity<GameResponse> initializeGame(
        @PathVariable String sessionId,
        @RequestBody GameRequest req
    ) {
        Game game = gameService.initializeGame(sessionId, req.gameType());
        GameResponse response = GameResponse.fromGame(game);
        return ResponseEntity.ok(response);
    }
}
