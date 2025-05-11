package hr.tvz.cartographers.utils;

import hr.tvz.cartographers.models.CellState;
import hr.tvz.cartographers.models.GameState;
import hr.tvz.cartographers.shared.thread.GetLastGameStateThread;
import hr.tvz.cartographers.shared.thread.SaveLastGameStateThread;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static hr.tvz.cartographers.utils.GameUtil.GRID_COLUMNS;
import static hr.tvz.cartographers.utils.GameUtil.GRID_ROWS;
import static hr.tvz.cartographers.utils.ReplayUtil.saveCurrentGameStateToGameReplay;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameStateUtil {

    public static synchronized GameState getLastGameState() {
        GetLastGameStateThread getLastGameMoveThread = new GetLastGameStateThread();

        return getLastGameMoveThread.getGameState();
    }

    public static void startNewGameSaveGameStateThread() {
        saveCurrentGameStateToGameReplay();
        startSaveGameStateThread(null);
    }

    public static void startSaveGameStateThread(GameState currentGameState) {
        SaveLastGameStateThread saveLastGameMoveThread = new SaveLastGameStateThread(currentGameState);
        Thread runner = new Thread(saveLastGameMoveThread);
        runner.start();
    }

    public static void gameStateToGridPane(GridPane gridPane, CellState[][] grid) {
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLUMNS; col++) {
                int finalRow = row;
                int finalCol = col;

                Pane pane = (Pane) gridPane.getChildren().stream()
                        .filter(node -> GridPane.getRowIndex(node) == finalRow && GridPane.getColumnIndex(node) == finalCol)
                        .findFirst()
                        .orElse(null);

                if (pane != null) {
                    pane.setStyle(grid[row][col].getStyle());
                }
            }
        }
    }
}
