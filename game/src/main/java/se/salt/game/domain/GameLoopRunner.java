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

        for (int i = 0; i < 20; i++) {
            Game game = gameSupplier.get();
            if (game == null) break;

            long now = System.currentTimeMillis();

            List<Cell> cells = new ArrayList<>();
            for (int k = 0; k < 2; k++) {
                int row = r.nextInt(4);
                int col = r.nextInt(4);
                cells.add(new Cell(row, col, now));
            }

            broadcaster.sendRound(
                gameId,
                game.players(),
                cells
            );

            gameUpdater.accept(game);

            try {
                Thread.sleep(r.nextInt(600) + 900);
            } catch (InterruptedException e) {
                break;
            }
        }

        onFinish.run();
        broadcaster.sendGameOver(gameId);
    }

}