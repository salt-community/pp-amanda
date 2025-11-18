package se.salt.game.domain;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.salt.game.domain.model.Cell;
import se.salt.game.domain.model.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
@AllArgsConstructor
@Slf4j
public class GameLoopRunner {

    private final GameBroadcaster broadcaster;

    public void run(
        String gameId,
        Supplier<Game> gameSupplier,
        Consumer<Game> gameUpdater,
        Runnable onFinish
    ) {
        Random r = new Random();

        List<Cell> cells = new ArrayList<>();

        long startTime = System.currentTimeMillis();
        long runtime = 35_000;

        while (System.currentTimeMillis() - startTime < runtime) {
            Game game = gameSupplier.get();
            if (game == null) break;

            long now = System.currentTimeMillis();

            // 1) Remove expired cells
            cells.removeIf(c -> now > c.expiresAt());

            // 2) Maybe spawn a new cell (max 2)
            if (cells.size() < 4 && r.nextDouble() < 0.09) {
                int row = r.nextInt(4);
                int col = r.nextInt(4);

                long lifetime = 400 + r.nextInt(1000);
                long expiresAt = now + lifetime;

                cells.add(new Cell(row, col, now, expiresAt));
            }

            broadcaster.sendRound(
                gameId,
                game.players(),
                cells
            );

            gameUpdater.accept(game);

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                break;
            }
        }

        onFinish.run();
        broadcaster.sendGameOver(gameId);
    }

}