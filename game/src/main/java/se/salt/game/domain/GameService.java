package se.salt.game.domain;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import se.salt.game.domain.model.Game;
import se.salt.game.domain.model.Player;
import se.salt.game.http.exception.NotFoundException;

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

        int countdownSeconds = 5;

        // Broadcast countdown event — FE handles timing
        messagingTemplate.convertAndSend(
            "/topic/game/" + gameId + "/countdown",
            Map.of(
                "eventType", "COUNTDOWN_STARTED",
                "seconds", countdownSeconds
            )
        );

        log.info(">>> Broadcasting COUNTDOWN_STARTED for game {} ({} seconds)", gameId, countdownSeconds);

        new Thread(() -> runGameLoop(gameId)).start();
    }


    protected void runGameLoop(String gameId) {
        int totalRounds = 20;
        int minDelay = 900;
        int maxDelay = 1500;
        Random random = new Random();

        log.info("🎮 Starting game loop for game {}", gameId);

        int lastRow = -1, lastCol = -1;

        for (int round = 1; round <= totalRounds; round++) {
            if (Thread.currentThread().isInterrupted()) break;

            int row, col;
            do {
                row = random.nextInt(5);
                col = random.nextInt(5);
            } while (row == lastRow && col == lastCol);
            lastRow = row;
            lastCol = col;

            long startTime = System.currentTimeMillis();

            messagingTemplate.convertAndSend(
                "/topic/game/" + gameId,
                Map.of(
                    "row", row,
                    "col", col,
                    "timestamp", startTime
                )
            );

            log.debug("→ Round {} sent: row={}, col={}, startTime={}", round, row, col, startTime);

            try {
                int delayMs = random.nextInt(maxDelay - minDelay) + minDelay;
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        Game finishedGame = activeGames.remove(gameId);
        if (finishedGame != null) {
            repo.updatePlayers(finishedGame);
            log.info("✅ Persisted final results for game {}", gameId);
        }

        messagingTemplate.convertAndSend(
            "/topic/game/" + gameId + "/over",
            Map.of("event", "GAME_OVER", "gameId", gameId)
        );

        log.info("🏁 Game loop finished for {}", gameId);
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
