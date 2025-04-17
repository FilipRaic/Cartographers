package hr.tvz.cartographers.utils;

import hr.tvz.cartographers.models.GameMove;
import hr.tvz.cartographers.models.GameState;
import hr.tvz.cartographers.shared.thread.PlayerClientThread;
import hr.tvz.cartographers.shared.thread.PlayerServerThread;
import javafx.scene.layout.GridPane;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static hr.tvz.cartographers.utils.GameStateUtil.gridPaneToCellState;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerSynchronizationUtil {

    @Getter
    private static GameState currentGameState;

    public static void startServerThreads() {
        currentGameState = new GameState();

        PlayerServerThread playerServerThread = new PlayerServerThread(currentGameState);
        Thread thread = new Thread(playerServerThread);
        thread.start();
    }

    public static void saveMove(GridPane primaryGameGrid, GridPane secondaryGameGrid) {
        GameMove theLastGameMove = new GameMove(gridPaneToCellState(primaryGameGrid));
        GameMoveUtil.startSaveLastGameMoveThread(theLastGameMove);

        GameState currentGameState = PlayerSynchronizationUtil.getCurrentGameState();

        currentGameState.setPrimaryGrid(gridPaneToCellState(primaryGameGrid));
        currentGameState.setSecondaryGrid(gridPaneToCellState(secondaryGameGrid));

        PlayerClientThread playerClientThread = new PlayerClientThread(currentGameState);
        Thread thread = new Thread(playerClientThread);
        thread.start();
    }
}
