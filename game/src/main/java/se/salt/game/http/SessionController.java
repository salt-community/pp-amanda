package se.salt.game.http;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.salt.game.domain.InitGameService;
import se.salt.game.domain.model.Game;
import se.salt.game.http.dto.GameResponse;
import se.salt.game.http.dto.InitRequest;
import se.salt.game.http.dto.JoinRequest;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/game")
@AllArgsConstructor
public class SessionController {

    private final InitGameService service;

    @PostMapping("/init")
    public ResponseEntity<Void> initGame(@RequestBody InitRequest req) {
        String sessionId = req.sessionId();
        service.initGame(sessionId);

        log.info("SQS received sessionId: {} that triggered lambda to call this endpoint", sessionId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/random-name")
    public ResponseEntity<Map<String, String>> randomName() {
        String generatedName = service.getRandomName();
        return ResponseEntity.ok(Map.of("randomName", generatedName));
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
