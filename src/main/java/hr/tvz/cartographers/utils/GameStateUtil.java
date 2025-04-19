package hr.tvz.cartographers.utils;

import hr.tvz.cartographers.models.CellState;
import hr.tvz.cartographers.models.GameState;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static hr.tvz.cartographers.utils.GameUtil.GRID_COLUMNS;
import static hr.tvz.cartographers.utils.GameUtil.GRID_ROWS;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameStateUtil {

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
                }
            }
        }

        return grid;
    }

    public static void gameStateToGridPane(GridPane gridPane, GameState gameState) {
        CellState[][] grid = gameState.getSecondaryGrid();

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
