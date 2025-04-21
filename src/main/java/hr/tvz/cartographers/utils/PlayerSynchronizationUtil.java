package hr.tvz.cartographers.utils;

import hr.tvz.cartographers.models.GameState;
import hr.tvz.cartographers.shared.thread.PlayerClientThread;
import hr.tvz.cartographers.shared.thread.PlayerServerThread;
import javafx.scene.layout.GridPane;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static hr.tvz.cartographers.utils.GameStateUtil.gridPaneToCellState;
import static hr.tvz.cartographers.utils.GameStateUtil.startSaveGameStateThread;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerSynchronizationUtil {

    private static final GameState currentGameState = new GameState();

    public static void startServerThreads() {
        PlayerServerThread playerServerThread = new PlayerServerThread(currentGameState);
        Thread thread = new Thread(playerServerThread);
        thread.start();
    }

    public static void saveMove(GridPane primaryGameGrid, GridPane secondaryGameGrid) {
        currentGameState.setPrimaryGrid(gridPaneToCellState(primaryGameGrid));
        currentGameState.setSecondaryGrid(gridPaneToCellState(secondaryGameGrid));

        startSaveGameStateThread(currentGameState);

        PlayerClientThread playerClientThread = new PlayerClientThread(currentGameState);
        Thread thread = new Thread(playerClientThread);
        thread.start();
    }
}
