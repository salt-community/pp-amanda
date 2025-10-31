package se.salt.game.http;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.salt.game.domain.GameService;
import se.salt.game.domain.model.Game;
import se.salt.game.http.dto.GameRequest;
import se.salt.game.http.dto.GameResponse;
import se.salt.game.http.dto.JoinRequest;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/game")
@AllArgsConstructor
public class SessionController {

    private final GameService gameService;

    @PostMapping("/init")
    public ResponseEntity<Void> initGame(@RequestBody Map<String, String> req) {
        String sessionId = req.get("sessionId");
        gameService.initGame(sessionId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{sessionId}/type")
    public ResponseEntity<GameResponse> setGameType(
        @PathVariable String sessionId,
        @RequestBody GameRequest req
    ) {
        Game updatedGame = gameService.setGameTypeInSession(sessionId, req.gameType());
        log.info("Received request to update to this: {}  type of Game in Session with ID: {}", req.gameType(), sessionId);
        GameResponse response = GameResponse.fromGame(updatedGame);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{sessionId}/status")
    public ResponseEntity<GameResponse> initializeGameStatus(
        @PathVariable String sessionId
    ) {
        log.info("Check status of initialization of game type for sessionId: {}", sessionId);
        Game game = gameService.gameStatus(sessionId);
        GameResponse response = GameResponse.fromGame(game);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{sessionId}/join")
    public ResponseEntity<GameResponse> joinGame(
        @PathVariable String sessionId,
        @RequestBody JoinRequest req
    ) {
        Game updated = gameService.addPlayer(sessionId, req.playerName());
        log.info("In session: {} player: {} joined", sessionId, req.playerName());
        return ResponseEntity.ok(GameResponse.fromGame(updated));
    }

}
