package hr.tvz.cartographers;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.util.*;

public class GameController {

    @FXML
    private GridPane gameGrid;

    private final List<int[]> shape = new ArrayList<>();

    private final List<List<int[]>> possibleShapes = List.of(
            // I shape (straight line)
            List.of(new int[]{0, -1}, new int[]{0, 0}, new int[]{0, 1}, new int[]{0, 2}),
            // O shape (square)
            List.of(new int[]{0, 0}, new int[]{0, 1}, new int[]{1, 0}, new int[]{1, 1}),
            // T shape
            List.of(new int[]{0, 0}, new int[]{0, 1}, new int[]{0, -1}, new int[]{1, 0}),
            // S shape
            List.of(new int[]{0, 0}, new int[]{0, 1}, new int[]{1, -1}, new int[]{1, 0}),
            // Z shape
            List.of(new int[]{0, 0}, new int[]{0, -1}, new int[]{1, 0}, new int[]{1, 1}),
            // J shape
            List.of(new int[]{0, 0}, new int[]{1, 0}, new int[]{2, 0}, new int[]{2, -1}),
            // L shape (original shape)
            List.of(new int[]{-1, 0}, new int[]{0, 0}, new int[]{1, 0}, new int[]{1, 1}),
            // Tilted short T
            List.of(new int[]{-1, 0}, new int[]{0, 0}, new int[]{0, 1}, new int[]{1, 0})
    );

    private final Random random = new Random();

    public void initialize() {
        shape.addAll(possibleShapes.get(random.nextInt(possibleShapes.size())));
    }

    @FXML
    protected void onMouseHover(MouseEvent event) {
        Pane hoveredPane = (Pane) event.getSource();
        Integer row = GridPane.getRowIndex(hoveredPane);
        Integer col = GridPane.getColumnIndex(hoveredPane);

        if (row == null || col == null) return;

        drawShape(row, col, true);
    }

    @FXML
    protected void onMouseExit(MouseEvent event) {
        Pane hoveredPane = (Pane) event.getSource();
        Integer row = GridPane.getRowIndex(hoveredPane);
        Integer col = GridPane.getColumnIndex(hoveredPane);

        if (row == null || col == null) return;

        drawShape(row, col, false);
    }

    private void drawShape(int baseRow, int baseCol, boolean highlight) {
        int maxRows = gameGrid.getRowConstraints().size();
        int maxCols = gameGrid.getColumnConstraints().size();

        // Calculate the bounds of the shape
        int minRowOffset = shape.stream().mapToInt(offset -> offset[0]).min().orElse(0);
        int maxRowOffset = shape.stream().mapToInt(offset -> offset[0]).max().orElse(0);
        int minColOffset = shape.stream().mapToInt(offset -> offset[1]).min().orElse(0);
        int maxColOffset = shape.stream().mapToInt(offset -> offset[1]).max().orElse(0);

        // Adjust base position to keep the shape within bounds
        int adjustedBaseRow = baseRow;
        int adjustedBaseCol = baseCol;

        if (baseRow + maxRowOffset >= maxRows) {
            adjustedBaseRow = maxRows - 1 - maxRowOffset;
        }
        if (baseCol + maxColOffset >= maxCols) {
            adjustedBaseCol = maxCols - 1 - maxColOffset;
        }
        if (baseRow + minRowOffset < 0) {
            adjustedBaseRow = -minRowOffset;
        }
        if (baseCol + minColOffset < 0) {
            adjustedBaseCol = -minColOffset;
        }

        // Collect all positions in the shape
        Set<String> shapePositions = new HashSet<>();
        for (int[] offset : shape) {
            int newRow = adjustedBaseRow + offset[0];
            int newCol = adjustedBaseCol + offset[1];
            shapePositions.add(newRow + "," + newCol);
        }

        // Draw the shape
        for (int[] offset : shape) {
            int newRow = adjustedBaseRow + offset[0];
            int newCol = adjustedBaseCol + offset[1];
            Pane pane = getPaneAt(newRow, newCol);
            if (pane != null) {
                if (highlight) {
                    highlightPane(pane, newRow, newCol, shapePositions);
                } else {
                    resetPane(pane);
                }
            }
        }
    }

    private Pane getPaneAt(int row, int col) {
        for (javafx.scene.Node node : gameGrid.getChildren()) {
            Integer nodeRow = GridPane.getRowIndex(node);
            Integer nodeCol = GridPane.getColumnIndex(node);
            if (nodeRow != null && nodeCol != null && nodeRow == row && nodeCol == col) {
                return (Pane) node;
            }
        }
        return null;
    }

    private void highlightPane(Pane pane, int row, int col, Set<String> shapePositions) {
        String top = (row - 1) + "," + col;
        String right = row + "," + (col + 1);
        String bottom = (row + 1) + "," + col;
        String left = row + "," + (col - 1);

        StringBuilder style = new StringBuilder("-fx-border-color: #ff6347; -fx-border-width: ");
        StringBuilder borderWidth = new StringBuilder();

        // Top border: highlight if no shape cell above
        borderWidth.append(!shapePositions.contains(top) ? "2 " : "0 ");
        // Right border: highlight if no shape cell to the right
        borderWidth.append(!shapePositions.contains(right) ? "2 " : "0 ");
        // Bottom border: highlight if no shape cell below
        borderWidth.append(!shapePositions.contains(bottom) ? "2 " : "0 ");
        // Left border: highlight if no shape cell to the left
        borderWidth.append(!shapePositions.contains(left) ? "2" : "0");

        style.append(borderWidth.toString());
        pane.setStyle(style.toString());
    }

    private void resetPane(Pane pane) {
        pane.setStyle("");
    }
}