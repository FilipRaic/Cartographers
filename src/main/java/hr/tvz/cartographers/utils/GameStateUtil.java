package hr.tvz.cartographers.utils;

import hr.tvz.cartographers.models.CellState;
import hr.tvz.cartographers.models.GameState;
import hr.tvz.cartographers.shared.thread.GetLastGameStateThread;
import hr.tvz.cartographers.shared.thread.SaveLastGameStateThread;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static hr.tvz.cartographers.utils.GameUtil.GRID_COLUMNS;
import static hr.tvz.cartographers.utils.GameUtil.GRID_ROWS;
import static hr.tvz.cartographers.utils.ReplayUtil.saveCurrentGameStateToGameReplay;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameStateUtil {

    public static Timeline getLastGameStateTimeline(GameState gameState) {
        Timeline theLastGameMoveTimeline = new Timeline(new KeyFrame(Duration.millis(1000), (ActionEvent _) -> {
            GetLastGameStateThread getLastGameMoveThread = new GetLastGameStateThread(gameState);
            Thread runner = new Thread(getLastGameMoveThread);
            runner.start();
        }), new KeyFrame(Duration.seconds(1)));
        theLastGameMoveTimeline.setCycleCount(Animation.INDEFINITE);
        return theLastGameMoveTimeline;
    }

    public static void startNewGameSaveGameStateThread() {
        saveCurrentGameStateToGameReplay();
        startSaveGameStateThread(new GameState());
    }

    public static void startSaveGameStateThread(GameState currentGameState) {
        SaveLastGameStateThread saveLastGameMoveThread = new SaveLastGameStateThread(currentGameState);
        Thread runner = new Thread(saveLastGameMoveThread);
        runner.start();
    }

    public static CellState[][] gridPaneToCellState(GridPane gridPane) {
        CellState[][] grid = new CellState[GRID_ROWS][GRID_COLUMNS];
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLUMNS; col++) {
                int finalRow = row;
                int finalCol = col;

                Pane pane = (Pane) gridPane.getChildren().stream()
                        .filter(node -> GridPane.getRowIndex(node) == finalRow && GridPane.getColumnIndex(node) == finalCol)
                        .findFirst()
                        .orElse(null);
                grid[row][col] = new CellState();

                if (pane != null) {
                    grid[row][col].setStyle(pane.getStyle());
                    grid[row][col].setTerrainType(GameUtil.getTerrainTypeMap()[row][col]);
                    grid[row][col].setRuins(GameUtil.getIsRuins()[row][col]);
                }
            }
        }

        return grid;
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
