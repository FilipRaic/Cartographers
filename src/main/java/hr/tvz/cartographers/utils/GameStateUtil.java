package hr.tvz.cartographers.utils;

import hr.tvz.cartographers.models.CellState;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameStateUtil {

    public static CellState[][] gridPaneToCellState(GridPane gridPane) {
        CellState[][] grid = new CellState[11][11];
        for (int row = 0; row < 11; row++) {
            for (int col = 0; col < 11; col++) {
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
}
