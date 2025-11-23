package se.salt.game.domain;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.salt.game.domain.model.Game;
import se.salt.game.domain.model.Type;
import se.salt.game.http.exception.DeadlinePassedException;
import se.salt.game.http.exception.NotFoundException;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@AllArgsConstructor
public class InitGameService {

    private final GameRepository repo;

    private final GameService gameService;

    private final Random random = new Random();

    private static final List<String> ADJECTIVES = List.of(
        "Brave", "Curious", "Gentle", "Mysterious",
        "Sleepy", "Glamorous", "Fiery", "Calm",

        "Bright", "Quiet", "Swift", "Shy",
        "Silver", "Bold", "Lucky", "Tiny",
        "Golden", "Kind", "Joyful", "Soft",
        "Clever", "Playful", "Happy", "Warm"
    );

    private static final List<String> NOUNS = List.of(
        "Lion", "Butterfly", "Forest", "Ocean",
        "Snake", "Wolf", "Mountain", "Cloud",

        "Sparrow", "Meadow", "River", "Shadow",
        "Willow", "Pebble", "Falcon", "Breeze",
        "Harbor", "Echo", "Sprout", "Ridge",

        "Fox", "Bear", "Otter", "Deer",
        "Owl", "Rabbit", "Tiger", "Dolphin"
    );

    public void initGame(String sessionId) {
        Optional<Game> existing = repo.findBySessionId(sessionId);

        if (existing.isPresent() && existing.get().joinDeadline() != null) {
            log.warn("⚠️ Game already initialized for sessionId={} (gameId={})",
                sessionId, existing.get().gameId());
            return;
        }

        Game base = existing.orElseThrow(() ->
            new NotFoundException("No base game found for session " + sessionId));

        Instant now = Instant.now();
        //TODO tweak deadline for joining session/game
        Instant joinDeadline = now.plusSeconds(40);
        Long ttl = now.plusSeconds(3600).getEpochSecond();

        Game updated = base.toBuilder()
            .type(Type.REACTION)
            .joinDeadline(joinDeadline)
            .ttl(ttl)
            .build();

        repo.saveNewGame(updated);
        log.info("✅ Initialized gameId={} (sessionId={})", updated.gameId(), sessionId);
        new Thread(() -> {
            try {
                long delayMs = Duration.between(Instant.now(), joinDeadline).toMillis();
                Thread.sleep(delayMs);

                log.info("⌛ Join window closed for game {}", updated.gameId());

                Game readyGame = repo.findByGameId(updated.gameId()).orElse(null);
                if (readyGame == null) return;

                if (readyGame.players().isEmpty()) {
                    log.info("⛔ No players joined – canceling game {}", updated.gameId());
                } else {
                    log.info("▶️ Starting game {} with {} players",
                        updated.gameId(), readyGame.players().size());
                    gameService.startGame(updated.gameId());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("⛔ Start scheduler interrupted for game {}", updated.gameId());
            }
        }).start();
    }


    public Game addPlayer(String sessionId, String name) {
        Game game = getGameBySessionId(sessionId);
        String gameId = game.gameId();

        Instant deadline = game.joinDeadline();
        if (deadline != null && Instant.now().isAfter(deadline)) {
            throw new DeadlinePassedException(sessionId);
        }

        Game updated = repo.addPlayer(gameId, name);
        log.info("Player '{}' joined game with ID: {}", name, gameId);
        return updated;
    }

    private Game getGameBySessionId(String sessionId) {
        return repo.findBySessionId(sessionId)
            .orElseThrow(() ->
                new NotFoundException("Session with ID: %s not found".formatted(sessionId)));
    }

    public String getRandomName() {
        String adjective = ADJECTIVES.get(random.nextInt(ADJECTIVES.size()));
        String noun = NOUNS.get(random.nextInt(NOUNS.size()));
        return adjective + "-" + noun;
    }
}
