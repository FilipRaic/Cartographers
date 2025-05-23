package hr.tvz.cartographers.utils;


import hr.tvz.cartographers.enums.TerrainType;
import hr.tvz.cartographers.models.CellState;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaneUtil {

    public static Pane getPaneAt(GridPane gameGrid, int row, int col) {
        for (javafx.scene.Node node : gameGrid.getChildren()) {
            Integer nodeRow = GridPane.getRowIndex(node);
            Integer nodeCol = GridPane.getColumnIndex(node);
            if (nodeRow != null && nodeCol != null && nodeRow == row && nodeCol == col) {
                return (Pane) node;
            }
        }

        return null;
    }

    public static void highlightPane(Pane pane, int row, int col, Set<String> shapePositions, boolean isLegal) {
        String borderColor = isLegal ? "#00FF00" : "#FF0000";
        StringBuilder style = new StringBuilder("-fx-border-color: " + borderColor + "; -fx-border-width: ");
        String top = (row - 1) + "," + col;
        String right = row + "," + (col + 1);
        String bottom = (row + 1) + "," + col;
        String left = row + "," + (col - 1);
        String borderWidth = (!shapePositions.contains(top) ? "2 " : "0 ") +
                (!shapePositions.contains(right) ? "2 " : "0 ") +
                (!shapePositions.contains(bottom) ? "2 " : "0 ") +
                (!shapePositions.contains(left) ? "2" : "0");
        style.append(borderWidth);

        pane.setStyle(style.toString());
    }

    public static void updatePaneStyle(CellState cellState, Pane pane) {
        TerrainType terrain = cellState.getTerrainType();
        String style = "-fx-background-color: " + terrain.getColor() + ";";

        if (terrain == TerrainType.RUINS) {
            style += "-fx-border-color: black; -fx-border-width: 2;";
        }

        pane.setStyle(style);
        cellState.setStyle(style);
    }
}
