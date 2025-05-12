package hr.tvz.cartographers.utils;

import hr.tvz.cartographers.enums.TerrainType;
import hr.tvz.cartographers.models.CellState;
import hr.tvz.cartographers.models.GameState;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Random;
import java.util.Set;

import static hr.tvz.cartographers.utils.CardUtil.drawNextCard;
import static hr.tvz.cartographers.utils.GameUtil.GRID_COLUMNS;
import static hr.tvz.cartographers.utils.GameUtil.GRID_ROWS;
import static hr.tvz.cartographers.utils.PaneUtil.getPaneAt;
import static hr.tvz.cartographers.utils.PaneUtil.updatePaneStyle;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AmbushUtil {

    private static final Random random = new Random();

    public static void handleAmbush() {
        List<List<int[]>> allUniqueShapes = GameUtil.getAllUniqueShapes();

        List<int[]> ambushShape = allUniqueShapes.get(random.nextInt(allUniqueShapes.size()));

        int corner = random.nextInt(4);
        int baseRow = corner < 2 ? 0 : 10;
        int baseCol = corner % 2 == 0 ? 0 : 10;

        tryPlaceAmbush(ambushShape, baseRow, baseCol);
        drawNextCard();
    }

    private static void tryPlaceAmbush(List<int[]> ambushShape, int baseRow, int baseCol) {
        int maxRow = baseRow == 0 ? GRID_ROWS : GRID_ROWS - 1;
        int maxCol = baseCol == 0 ? GRID_COLUMNS : GRID_COLUMNS - 1;
        boolean placed = false;

        for (int row = 0; row < maxRow && !placed; row++) {
            for (int col = 0; col < maxCol && !placed; col++) {
                if (isAmbushPlacementValid(ambushShape, baseRow, baseCol, row, col)) {
                    placeAmbushShape(ambushShape, baseRow, baseCol, row, col);
                    placed = true;
                }
            }
        }
    }

    private static boolean isAmbushPlacementValid(List<int[]> ambushShape, int baseRow, int baseCol, int row, int col) {
        GameState gameState = GameUtil.getGameState();

        for (int[] offset : ambushShape) {
            int newRow = baseRow + row + offset[0];
            int newCol = baseCol + col + offset[1];

            if (newRow < 0 || newRow >= GRID_ROWS || newCol < 0 || newCol >= GRID_COLUMNS)
                return false;

            CellState currentPlayerCellState = gameState.getPrimaryGrid()[newRow][newCol];
            CellState otherPlayerCellState = gameState.getSecondaryGrid()[newRow][newCol];

            if (currentPlayerCellState.getTerrainType().isNotEmptyOrRuins() ||
                    otherPlayerCellState.getTerrainType().isNotEmptyOrRuins())
                return false;
        }

        return true;
    }

    private static void placeAmbushShape(List<int[]> ambushShape, int baseRow, int baseCol, int row, int col) {
        GameState gameState = GameUtil.getGameState();
        GridPane primaryGameGrid = GameUtil.getPrimaryGameGrid();
        GridPane secondaryGameGrid = GameUtil.getSecondaryGameGrid();

        for (int[] offset : ambushShape) {
            int newRow = baseRow + row + offset[0];
            int newCol = baseCol + col + offset[1];
            for (GridPane gridPane : Set.of(primaryGameGrid, secondaryGameGrid)) {
                Pane pane = getPaneAt(gridPane, newRow, newCol);
                CellState currentPlayerCellState = gameState.getPrimaryGrid()[newRow][newCol];
                CellState otherPlayerCellState = gameState.getSecondaryGrid()[newRow][newCol];

                currentPlayerCellState.setTerrainType(TerrainType.MONSTER);
                otherPlayerCellState.setTerrainType(TerrainType.MONSTER);

                updatePaneStyle(currentPlayerCellState, pane);
                updatePaneStyle(otherPlayerCellState, pane);
            }
        }
    }
}
