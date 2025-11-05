package se.salt.game.http;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.salt.game.domain.InitGameService;
import se.salt.game.domain.model.Game;
import se.salt.game.http.dto.GameResponse;
import se.salt.game.http.dto.JoinRequest;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/game")
@AllArgsConstructor
public class SessionController {

    private final InitGameService service;

    @PostMapping("/init")
    public ResponseEntity<Void> initGame(@RequestBody Map<String, String> req) {
        String sessionId = req.get("sessionId");
        service.initGame(sessionId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{sessionId}/status")
    public ResponseEntity<GameResponse> initializeGameStatus(
        @PathVariable String sessionId
    ) {
        log.info("Check status of initialization of game type for sessionId: {}", sessionId);
        Game game = service.gameStatus(sessionId);
        GameResponse response = GameResponse.fromGame(game);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{sessionId}/join")
    public ResponseEntity<GameResponse> joinGame(
        @PathVariable String sessionId,
        @RequestBody JoinRequest req
    ) {
        Game updated = service.addPlayer(sessionId, req.playerName());
        log.info("In session: {} player: {} joined", sessionId, req.playerName());
        return ResponseEntity.ok(GameResponse.fromGame(updated));
    }

}
