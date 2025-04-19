package hr.tvz.cartographers.shared.thread;

import hr.tvz.cartographers.models.GameState;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SaveLastGameMoveThread extends AbstractGameMoveThread implements Runnable {

    private GameState currentGameState;

    @Override
    public void run() {
        super.saveGameMove(currentGameState);
    }
}
