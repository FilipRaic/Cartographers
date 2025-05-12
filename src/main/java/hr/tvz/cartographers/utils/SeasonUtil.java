package hr.tvz.cartographers.utils;

import hr.tvz.cartographers.enums.Season;
import hr.tvz.cartographers.enums.TerrainType;
import hr.tvz.cartographers.models.CellState;
import hr.tvz.cartographers.models.Edict;
import hr.tvz.cartographers.models.GameState;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

import static hr.tvz.cartographers.utils.CardUtil.drawNextCard;
import static hr.tvz.cartographers.utils.CardUtil.initializeExploreDeck;
import static hr.tvz.cartographers.utils.DisplayUtil.updateUI;
import static hr.tvz.cartographers.utils.GameUtil.GRID_COLUMNS;
import static hr.tvz.cartographers.utils.GameUtil.GRID_ROWS;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SeasonUtil {

    public static void endSeason() {
        int score = GameUtil.getScore();
        Season currentSeason = GameUtil.getCurrentSeason();
        List<Edict> edicts = GameUtil.getEdicts();

        int seasonScore = calculateSeasonScore();
        GameUtil.setScore(score + seasonScore);
        GameUtil.setCurrentSeason(currentSeason.incrementSeason());
        if (!currentSeason.isEnd()) {
            GameUtil.setCurrentEdict(edicts.get(currentSeason.getPosition() % 2));
            GameUtil.setCurrentTime(0);
            initializeExploreDeck();
            drawNextCard();
            updateUI();
        } else {
            disableGame();
            updateUI();
        }
    }

    private static int calculateSeasonScore() {
        GameState gameState = GameUtil.getGameState();
        Edict currentEdict = GameUtil.getCurrentEdict();

        CellState[][] currentPlayerGrid = gameState.getPrimaryGrid();
        int seasonScore = currentEdict.score(currentPlayerGrid);
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLUMNS; col++) {
                if (currentPlayerGrid[row][col].getTerrainType() == TerrainType.MONSTER) {
                    seasonScore -= countAdjacentEmpty(currentPlayerGrid, row, col);
                }
            }
        }

        return seasonScore;
    }

    private static int countAdjacentEmpty(CellState[][] grid, int row, int col) {
        int count = 0;

        if (row > 0 && grid[row - 1][col].getTerrainType() == TerrainType.EMPTY) count++;
        if (row < 10 && grid[row + 1][col].getTerrainType() == TerrainType.EMPTY) count++;
        if (col > 0 && grid[row][col - 1].getTerrainType() == TerrainType.EMPTY) count++;
        if (col < 10 && grid[row][col + 1].getTerrainType() == TerrainType.EMPTY) count++;

        return count;
    }

    private static void disableGame() {
        VBox cardDisplay = GameUtil.getCardDisplay();
        GridPane primaryGameGrid = GameUtil.getPrimaryGameGrid();

        cardDisplay.getChildren().clear();
        for (javafx.scene.Node node : primaryGameGrid.getChildren()) {
            if (node instanceof Pane) {
                node.setDisable(true);
            }
        }
    }
}
