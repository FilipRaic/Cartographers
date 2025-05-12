package hr.tvz.cartographers.utils;

import hr.tvz.cartographers.enums.Season;
import hr.tvz.cartographers.models.CellState;
import hr.tvz.cartographers.models.Edict;
import hr.tvz.cartographers.models.GameState;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static hr.tvz.cartographers.CartographersApplication.getPlayer;
import static hr.tvz.cartographers.shared.enums.Player.SINGLE_PLAYER;
import static hr.tvz.cartographers.utils.PaneUtil.updatePaneStyle;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DisplayUtil {

    public static void updateUI() {
        Label seasonLabel = GameUtil.getSeasonLabel();
        Label scoreLabel = GameUtil.getScoreLabel();
        Label edictLabel = GameUtil.getEdictLabel();
        int score = GameUtil.getScore();

        seasonLabel.setText(getSeasonText());
        scoreLabel.setText("Score: " + score);
        edictLabel.setText(getEdictText());
    }

    public static void updateMapVisuals() {
        GameState gameState = GameUtil.getGameState();
        GridPane primaryGameGrid = GameUtil.getPrimaryGameGrid();
        GridPane secondaryGameGrid = GameUtil.getSecondaryGameGrid();

        updateGridVisuals(primaryGameGrid, gameState.getPrimaryGrid());
        updateGridVisuals(secondaryGameGrid, gameState.getSecondaryGrid());
    }

    private static String getSeasonText() {
        Season currentSeason = GameUtil.getCurrentSeason();

        if (currentSeason.isEnd())
            return "Game Over";

        return currentSeason.getLabel();
    }

    private static String getEdictText() {
        Season currentSeason = GameUtil.getCurrentSeason();
        GameState gameState = GameUtil.getGameState();
        int score = GameUtil.getScore();
        Edict currentEdict = GameUtil.getCurrentEdict();

        if (currentSeason.isEnd()) {
            String playerResult = "\nYou " + (gameState.hasHigherScore() ? "won" : "lost") + "!";
            return "Final Score: " + score + (!getPlayer().equals(SINGLE_PLAYER) ? playerResult : " DRAW!");
        }

        return "Edict: " + currentEdict.getName();
    }

    private static void updateGridVisuals(GridPane gridPane, CellState[][] grid) {
        for (javafx.scene.Node node : gridPane.getChildren()) {
            if (node instanceof Pane pane) {
                Integer row = GridPane.getRowIndex(pane);
                Integer col = GridPane.getColumnIndex(pane);
                CellState cellState = grid[row][col];
                updatePaneStyle(cellState, pane);
            }
        }
    }
}
