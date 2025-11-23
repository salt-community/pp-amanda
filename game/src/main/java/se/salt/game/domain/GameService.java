package se.salt.game.domain;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.salt.game.domain.model.Game;
import se.salt.game.domain.model.Player;
import se.salt.game.domain.model.TopScore;
import se.salt.game.http.exception.NotFoundException;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class GameService {

    private final GameRepository gameRepo;

    private final TopScoreRepository topScoreRepo;

    private final GameLoopRunner loopRunner;

    private final ReactionHandler reactionHandler;

    private final GameBroadcaster broadcaster;

    private final Map<String, Game> activeGames = new ConcurrentHashMap<>();

    private final ExecutorService gameExecutor = Executors.newCachedThreadPool();

    @PostConstruct
    public void init() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down game executor...");
            gameExecutor.shutdownNow();
        }));
    }

    public void startGame(String gameId) {
        if (activeGames.containsKey(gameId)) {
            log.warn("Game {} already started – skipping duplicate start", gameId);
            return;
        }

        Game game = getGameByGameId(gameId);
        activeGames.put(gameId, game);

        // broadcast countdown
        broadcaster.sendCountdown(gameId, 5);

        // --- Extracted callbacks (clean!) ---
        Supplier<Game> fetchGame = () -> activeGames.get(gameId);

        Consumer<Game> saveGame = updated -> activeGames.put(gameId, updated);

        Runnable onFinish = () -> {
            Game finished = activeGames.remove(gameId);
            if (finished != null) {
                gameRepo.updatePlayers(finished);

                finished.players().forEach((playerName, score) -> {
                    topScoreRepo.save(new TopScore(playerName, score.intValue()));
                    log.info("Saved toplist entry: {} -> {}", playerName, score);
                });
            }
        };

        // --- Submit to executor instead of new Thread ---
        gameExecutor.submit(() ->
            loopRunner.run(gameId, fetchGame, saveGame, onFinish)
        );
    }


    public void handleReaction(Player reaction) {
        Game game = activeGames.get(reaction.gameId());
        if (game == null) return;

        Game updated = reactionHandler.applyReaction(game, reaction);

        activeGames.put(reaction.gameId(), updated);

        broadcaster.sendResults(reaction.gameId(), updated); // live scoreboard
    }

    private Game getGameByGameId(String gameId) {
        return gameRepo.findByGameId(gameId)
            .orElseThrow(() ->
                new NotFoundException("Game with ID: %s not found".formatted(gameId)));
    }

    public Map<String, Double> getResult(String gameId) {
        Game game = getGameByGameId(gameId);

        return game.players().entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (a, b) -> a,
                LinkedHashMap::new
            ));
    }

    public List<TopScore> getTop10() {
        List<TopScore> all = topScoreRepo.findAll();
        return all.stream()
            .sorted(Comparator.comparingInt(TopScore::score).reversed())
            .limit(10)
            .toList();
    }
}
