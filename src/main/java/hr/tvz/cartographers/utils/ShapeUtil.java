package hr.tvz.cartographers.utils;

import hr.tvz.cartographers.enums.Season;
import hr.tvz.cartographers.enums.TerrainType;
import hr.tvz.cartographers.models.CellState;
import hr.tvz.cartographers.models.GameState;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static hr.tvz.cartographers.utils.CoordinateUtil.adjustCoordinate;
import static hr.tvz.cartographers.utils.GameUtil.GRID_COLUMNS;
import static hr.tvz.cartographers.utils.GameUtil.GRID_ROWS;
import static hr.tvz.cartographers.utils.PaneUtil.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ShapeUtil {

    public static boolean checkCanPlaceShapeOnGrid(MouseEvent event, GameState gameState) {
        Season season = GameUtil.getCurrentSeason();
        List<int[]> shape = GameUtil.getCurrentShape();
        TerrainType terrain = GameUtil.getCurrentTerrain();

        if (season.isEnd() || gameState.hasPlayerPlayed()) return false;

        Pane clickedPane = (Pane) event.getSource();
        Integer row = GridPane.getRowIndex(clickedPane);
        Integer col = GridPane.getColumnIndex(clickedPane);

        if (row == null || col == null || shape == null || terrain == null) return false;

        int adjustedRow = adjustCoordinate(row, shape, true);
        int adjustedCol = adjustCoordinate(col, shape, false);

        if (!isPlacementLegal(gameState, shape, adjustedRow, adjustedCol)) return false;

        placeShapeOnGrid(gameState, adjustedRow, adjustedCol);

        return true;
    }

    public static void highlightShapeOnGrid(MouseEvent event, boolean shouldHighlight) {
        GameState gameState = GameUtil.getGameState();
        Season season = GameUtil.getCurrentSeason();
        List<int[]> shape = GameUtil.getCurrentShape();

        if (season.isEnd() || gameState.hasPlayerPlayed()) return;

        Pane hoveredPane = (Pane) event.getSource();
        Integer row = GridPane.getRowIndex(hoveredPane);
        Integer col = GridPane.getColumnIndex(hoveredPane);

        if (row == null || col == null || shape == null) return;

        int adjustedRow = adjustCoordinate(row, shape, true);
        int adjustedCol = adjustCoordinate(col, shape, false);

        drawShape(gameState, shape, adjustedRow, adjustedCol, shouldHighlight);
    }

    private static void placeShapeOnGrid(GameState gameState, int row, int col) {
        List<int[]> shape = GameUtil.getCurrentShape();
        TerrainType terrain = GameUtil.getCurrentTerrain();
        GridPane gridPane = GameUtil.getPrimaryGameGrid();

        if (isShapeOutsideBounds(shape, row, col)) return;

        for (int[] offset : shape) {
            int newRow = row + offset[0];
            int newCol = col + offset[1];
            if (!isWithinGrid(newRow, newCol)) continue;
            CellState cellState = gameState.getPrimaryGrid()[newRow][newCol];
            cellState.setTerrainType(terrain);
            Pane pane = getPaneAt(gridPane, newRow, newCol);
            updatePaneStyle(cellState, pane);
        }
    }

    private static Set<String> getShapePositions(List<int[]> shape, int row, int col) {
        Set<String> shapePositions = new HashSet<>();
        if (isShapeOutsideBounds(shape, row, col)) return shapePositions;

        for (int[] offset : shape) {
            int newRow = row + offset[0];
            int newCol = col + offset[1];
            if (isWithinGrid(newRow, newCol))
                shapePositions.add(newRow + "," + newCol);
        }

        return shapePositions;
    }

    private static void drawShapePosition(GameState gameState, int row, int col,
                                          boolean shouldHighlight, boolean isLegal, Set<String> shapePositions) {
        GridPane gridPane = GameUtil.getPrimaryGameGrid();

        if (!isWithinGrid(row, col)) return;

        Pane pane = getPaneAt(gridPane, row, col);
        if (pane != null) {
            if (shouldHighlight) {
                highlightPane(pane, row, col, shapePositions, isLegal);
            } else {
                CellState cellState = gameState.getPrimaryGrid()[row][col];
                updatePaneStyle(cellState, pane);
            }
        }
    }

    private static boolean isPlacementLegal(GameState gameState, List<int[]> shape, int row, int col) {
        boolean ruinsPending = GameUtil.isRuinsPending();

        if (isShapeOutsideBounds(shape, row, col)) return false;

        boolean overlapsRuins = false;
        for (int[] offset : shape) {
            int newRow = row + offset[0];
            int newCol = col + offset[1];
            if (!isWithinGrid(newRow, newCol)) continue;

            CellState cellState = gameState.getPrimaryGrid()[newRow][newCol];
            TerrainType terrainType = cellState.getTerrainType();

            if (terrainType != TerrainType.EMPTY && terrainType != TerrainType.RUINS) return false;

            if (terrainType == TerrainType.RUINS) {
                overlapsRuins = true;
            }
        }

        return !(ruinsPending && !overlapsRuins && canPlaceTerrain(shape, gameState));
    }

    private static boolean canPlaceTerrain(List<int[]> shape, GameState gameState) {
        for (int row = 0; row < GRID_ROWS; row++)
            for (int col = 0; col < GRID_COLUMNS; col++)
                if (isValidPlacementWithRuins(gameState, shape, row, col))
                    return true;

        return false;
    }

    private static void drawShape(GameState gameState, List<int[]> shape, int row, int col, boolean shouldHighlight) {
        if (shape == null) return;

        boolean isLegal = isPlacementLegal(gameState, shape, row, col);
        Set<String> shapePositions = getShapePositions(shape, row, col);

        for (int[] offset : shape) {
            int newRow = row + offset[0];
            int newCol = col + offset[1];
            if (isWithinGrid(newRow, newCol))
                drawShapePosition(gameState, newRow, newCol, shouldHighlight, isLegal, shapePositions);
        }
    }

    private static boolean isShapeOutsideBounds(List<int[]> shape, int baseRow, int baseCol) {
        int minRowOffset = shape.stream().mapToInt(offset -> offset[0]).min().orElse(0);
        int maxRowOffset = shape.stream().mapToInt(offset -> offset[0]).max().orElse(0);
        int minColOffset = shape.stream().mapToInt(offset -> offset[1]).min().orElse(0);
        int maxColOffset = shape.stream().mapToInt(offset -> offset[1]).max().orElse(0);

        return baseRow + minRowOffset < 0 || baseRow + maxRowOffset >= GRID_ROWS ||
                baseCol + minColOffset < 0 || baseCol + maxColOffset >= GRID_COLUMNS;
    }

    private static boolean isWithinGrid(int row, int col) {
        return row >= 0 && row < GRID_ROWS && col >= 0 && col < GRID_COLUMNS;
    }

    private static boolean isValidPlacementWithRuins(GameState gameState, List<int[]> shape, int baseRow, int baseCol) {
        if (isShapeOutsideBounds(shape, baseRow, baseCol)) {
            return false;
        }

        boolean hasRuins = false;
        for (int[] offset : shape) {
            int newRow = baseRow + offset[0];
            int newCol = baseCol + offset[1];
            if (!isWithinGrid(newRow, newCol)) return false;
            CellState currentPlayerCellState = gameState.getPrimaryGrid()[newRow][newCol];
            if (currentPlayerCellState.getTerrainType() != TerrainType.EMPTY &&
                    currentPlayerCellState.getTerrainType() != TerrainType.RUINS) {
                return false;
            }
            if (currentPlayerCellState.getTerrainType() == TerrainType.RUINS) {
                hasRuins = true;
            }
        }

        return hasRuins;
    }
}
