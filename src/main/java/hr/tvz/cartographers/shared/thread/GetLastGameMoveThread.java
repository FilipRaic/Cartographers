package hr.tvz.cartographers.shared.thread;

import hr.tvz.cartographers.models.GameState;
import hr.tvz.cartographers.utils.GameStateUtil;
import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class GetLastGameMoveThread extends AbstractGameMoveThread implements Runnable {

    private GridPane secondaryGameGrid;

    @Override
    public void run() {
        Optional<GameState> currentGameState = super.getGameState();
        currentGameState.ifPresent(gameState ->
                Platform.runLater(() -> GameStateUtil.gameStateToGridPane(secondaryGameGrid, gameState)));
    }
}
