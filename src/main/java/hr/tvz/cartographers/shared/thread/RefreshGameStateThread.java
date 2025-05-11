package hr.tvz.cartographers.shared.thread;

import hr.tvz.cartographers.models.GameState;
import hr.tvz.cartographers.utils.GameUtil;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RefreshGameStateThread implements Runnable {

    private GameState receivedGameState;

    @Override
    public void run() {
        GameUtil.updateGameState(receivedGameState);
    }
}
