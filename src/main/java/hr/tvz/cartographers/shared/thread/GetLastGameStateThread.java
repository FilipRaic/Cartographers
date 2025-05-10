package hr.tvz.cartographers.shared.thread;

import hr.tvz.cartographers.models.GameState;
import javafx.application.Platform;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class GetLastGameStateThread extends AbstractGameStateThread implements Runnable {

    private GameState gameState;

    @Override
    public void run() {
        Optional<GameState> currentGameStateOptional = super.getGameState();
        currentGameStateOptional.ifPresent(currentGameState -> Platform.runLater(() -> gameState = currentGameState));
    }
}
