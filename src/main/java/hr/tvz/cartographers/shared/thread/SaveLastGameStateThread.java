package hr.tvz.cartographers.shared.thread;

import hr.tvz.cartographers.models.GameState;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SaveLastGameStateThread extends AbstractGameStateThread implements Runnable {

    private GameState currentGameState;

    @Override
    public void run() {
        super.saveGameState(currentGameState);
    }
}
