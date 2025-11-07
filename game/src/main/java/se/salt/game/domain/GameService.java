package se.salt.game.domain;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import se.salt.game.domain.model.Game;
import se.salt.game.domain.model.Player;
import se.salt.game.http.exception.NotFoundException;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@AllArgsConstructor
public class GameService {

    private final GameRepository repo;

    private final SimpMessagingTemplate messagingTemplate;

    private final Map<String, Game> activeGames = new ConcurrentHashMap<>();


    public void startGame(String gameId) {
        if (activeGames.containsKey(gameId)) {
            log.warn("Game {} already started – skipping duplicate start", gameId);
            return;
        }
        Game game = getGameByGameId(gameId);
        activeGames.put(gameId, game);

        Instant startTime = game.joinDeadline().plusSeconds(5);

        messagingTemplate.convertAndSend(
            "/topic/game/" + gameId + "/countdown",
            Map.of(
                "eventType", "COUNTDOWN_STARTED",
                "startTime", startTime.toString()
            )
        );

        log.info(">>> Broadcasting COUNTDOWN_STARTED for game {} at {}", gameId, startTime);

        log.info("Countdown started for game {} at {}", gameId, startTime);

        new Thread(() -> {
            try {
                waitUntil(startTime);
                runGameLoop(gameId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Countdown thread interrupted for game {}", gameId);
            }
        }).start();
    }

    /**
     * Helper that pauses until a specific instant in time
     */
    protected void waitUntil(Instant startAt) throws InterruptedException {
        long delay = Duration.between(Instant.now(), startAt).toMillis();
        if (delay > 0) {
            log.info("Waiting {} ms until game starts at {}", delay, startAt);
            Thread.sleep(delay);
        }
    }

    protected void runGameLoop(String gameId) {
        int rounds = 20;
        int delayMs = 1200;
        Random random = new Random();

        for (int i = 0; i < rounds; i++) {
            int row = random.nextInt(3);
            int col = random.nextInt(3);

            messagingTemplate.convertAndSend(
                "/topic/game/" + gameId,
                Map.of("row", row, "col", col)
            );

            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        Game finishedGame = activeGames.remove(gameId);
        if (finishedGame != null) {
            repo.updatePlayers(finishedGame);
            log.info("Persisted final results for game {}", gameId);
        }


        messagingTemplate.convertAndSend("/topic/game/" + gameId + "/over", "Game over!");
    }

    public void handleReaction(Player reaction) {
        Game game = activeGames.get(reaction.gameId());
        if (game == null) {
            log.warn("Reaction for unknown or expired game {}", reaction.gameId());

        }
        String player = reaction.playerName();

        // If new player somehow sneaks in, initialize them
        assert game != null;
        Map<String, Double> updatedPlayers = new HashMap<>(game.players());
        double newScore = updatedPlayers.getOrDefault(player, 0.0) + 1;
        updatedPlayers.put(player, newScore);

        log.info("Player {} reacted (score now {}) in game {}", player, newScore, reaction.gameId());

        Game updated = game.toBuilder().players(updatedPlayers).build();
        activeGames.put(reaction.gameId(), updated);

        messagingTemplate.convertAndSend(
            "/topic/game/" + reaction.gameId() + "/results",
            updated
        );
    }

    private Game getGameByGameId(String gameId) {
        return repo.findByGameId(gameId)
            .orElseThrow(() ->
                new NotFoundException("Game with ID: %s not found".formatted(gameId)));
    }
}
