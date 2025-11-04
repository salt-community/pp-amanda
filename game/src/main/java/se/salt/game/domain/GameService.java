package se.salt.game.domain;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import se.salt.game.domain.model.Game;
import se.salt.game.http.exception.NotFoundException;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class GameService {

    /// topic/game/{gameId}/countdown      → pre-game countdown
    /// topic/game/{gameId}/state          → ongoing game state updates (scores, clicks, etc.)
    /// topic/game/{gameId}/finished       → send when the game ends


    private final GameRepository repo;

    private final SimpMessagingTemplate messagingTemplate;

    public void startGame(String gameId) {
        Game game = getGameByGameId(gameId);

        Instant startTime = game.joinDeadline().plusSeconds(120);

        messagingTemplate.convertAndSend(
            "/topic/game/" + gameId + "/countdown",
            Map.of("eventType", "COUNTDOWN_STARTED",
                "startTime", startTime.toString())
        );

        log.info("Countdown started for game {} at {}", gameId, startTime);
    }

//    public void startGame(String gameId) {
//        Game game = getGameByGameId(gameId);
//        Instant startTime = game.joinDeadline().plusSeconds(120);
//
//        new Thread(() -> {
//            try {
//                Thread.sleep(2000);
//                messagingTemplate.convertAndSend(
//                    "/topic/game/" + gameId + "/countdown",
//                    Map.of("eventType", "COUNTDOWN_STARTED",
//                        "startTime", startTime.toString())
//                );
//                log.info("Countdown started for game {} at {}", gameId, startTime);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        }).start();
//    }


    private Game getGameByGameId(String gameId) {
        return repo.findByGameId(gameId)
            .orElseThrow(() ->
                new NotFoundException("Game with ID: %s not found".formatted(gameId)));
    }
}
