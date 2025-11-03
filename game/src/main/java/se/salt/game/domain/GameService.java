package se.salt.game.domain;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.salt.game.domain.model.Game;
import se.salt.game.http.exception.NotFoundException;

@Slf4j
@Service
@AllArgsConstructor
public class GameService {

    private final GameRepository repo;

    public void startGame(String gameId) {
        Game game = getGameByGameId(gameId);
    }

    private Game getGameByGameId(String gameId) {
        return repo.findByGameId(gameId)
            .orElseThrow(() ->
                new NotFoundException("Game with ID: %s not found".formatted(gameId)));
    }
}
