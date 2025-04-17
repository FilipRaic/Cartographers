package hr.tvz.cartographers.shared.thread;

import hr.tvz.cartographers.models.GameState;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RefreshGameStateThread implements Runnable {

    private GameState gameStateToRefresh;
    private GameState currentGameState;

    @Override
    public void run() {
        currentGameState.setPrimaryGrid(gameStateToRefresh.getPrimaryGrid());
        currentGameState.setSecondaryGrid(gameStateToRefresh.getSecondaryGrid());
    }
}
