package se.salt.game.domain;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.salt.game.domain.model.Game;

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

            int row = r.nextInt(5);
            int col = r.nextInt(5);

            broadcaster.sendRound(
                gameId,
                game.players(),
                row,
                col
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