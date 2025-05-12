package hr.tvz.cartographers.utils;

import hr.tvz.cartographers.enums.Season;
import hr.tvz.cartographers.enums.Shape;
import hr.tvz.cartographers.enums.TerrainType;
import hr.tvz.cartographers.models.CellState;
import hr.tvz.cartographers.models.Edict;
import hr.tvz.cartographers.models.ExploreCard;
import hr.tvz.cartographers.models.GameState;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static hr.tvz.cartographers.utils.CardUtil.*;
import static hr.tvz.cartographers.utils.DisplayUtil.updateMapVisuals;
import static hr.tvz.cartographers.utils.DisplayUtil.updateUI;
import static hr.tvz.cartographers.utils.GameStateUtil.gameStateToGridPane;
import static hr.tvz.cartographers.utils.ShapeUtil.checkCanPlaceShapeOnGrid;
import static hr.tvz.cartographers.utils.ShapeUtil.highlightShapeOnGrid;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameUtil {

    public static final Integer GRID_ROWS = 11;
    public static final Integer GRID_COLUMNS = 11;

    @Getter
    private static final List<Edict> edicts = EdictUtil.getEdicts();
    @Getter
    private static final List<List<int[]>> allUniqueShapes = Shape.getAllPermutationsForAllShapes();

    @Getter
    private static GridPane primaryGameGrid;
    @Getter
    private static GridPane secondaryGameGrid;
    @Getter
    private static VBox cardDisplay;
    @Getter
    private static Label seasonLabel;
    @Getter
    private static Label scoreLabel;
    @Getter
    private static Label edictLabel;
    @Getter
    private static GameState gameState;

    @Setter
    @Getter
    private static int score = 0;
    @Setter
    @Getter
    private static Edict currentEdict;
    @Setter
    @Getter
    private static int currentTime = 0;
    @Setter
    @Getter
    private static ExploreCard currentCard;
    @Setter
    @Getter
    private static List<int[]> currentShape;
    @Setter
    @Getter
    private static TerrainType currentTerrain;
    @Getter
    private static boolean ruinsPending = false;
    @Setter
    @Getter
    private static List<ExploreCard> exploreDeck;
    @Setter
    @Getter
    private static Season currentSeason = Season.SPRING;

    private static final Random random = new Random();

    public static void initializeGame(GridPane primaryGameGridInput, GridPane secondaryGameGridInput, VBox cardDisplayInput,
                                      Label seasonLabelInput, Label scoreLabelInput, Label edictLabelInput, GameState gameStateInput) {
        primaryGameGrid = primaryGameGridInput;
        secondaryGameGrid = secondaryGameGridInput;
        cardDisplay = cardDisplayInput;
        seasonLabel = seasonLabelInput;
        scoreLabel = scoreLabelInput;
        edictLabel = edictLabelInput;
        gameState = gameStateInput;

        if (gameState.isLoadedGameState()) {
            gameStateToGridPane(primaryGameGrid, gameState.getPrimaryGrid());
            gameStateToGridPane(secondaryGameGrid, gameState.getSecondaryGrid());
            currentSeason = gameState.getCurrentSeason();
            score = gameState.getCurrentPlayerGameState().getScore();
            exploreDeck = new ArrayList<>(gameState.getExploreDeck());
            currentEdict = gameState.getCurrentEdict();
            currentCard = gameState.getCurrentCard();
            currentShape = gameState.getCurrentShape();
            ruinsPending = gameState.isRuinsPending();

            if (currentCard != null)
                updateCardDisplay();
        } else {
            placeInitialTerrain(TerrainType.MOUNTAIN);
            placeInitialTerrain(TerrainType.RUINS);
            Collections.shuffle(edicts);
            currentEdict = edicts.getFirst();
            currentSeason = gameState.getCurrentSeason();
            score = gameState.getCurrentPlayerGameState().getScore();
            initializeExploreDeck();
            drawNextCard();

            saveGameState(gameState);
        }

        updateMapVisuals();
        updateUI();
    }

    public static void updateGameState(GameState refreshedGameState) {
        if (secondaryGameGrid == null)
            return;

        gameState.updateGameState(refreshedGameState);
        gameStateToGridPane(secondaryGameGrid, gameState.getSecondaryGrid());
        currentSeason = gameState.getCurrentSeason();
        score = gameState.getCurrentPlayerGameState().getScore();
        exploreDeck = new ArrayList<>(gameState.getExploreDeck());
        currentCard = gameState.getCurrentCard();
        currentShape = gameState.getCurrentShape();
        ruinsPending = gameState.isRuinsPending();
        Collections.shuffle(edicts);
        currentEdict = gameState.getCurrentEdict();

        if (currentCard != null)
            updateCardDisplay();

        updateMapVisuals();
        updateUI();
    }

    public static void highlightShape(MouseEvent event, boolean shouldHighlight) {
        highlightShapeOnGrid(event, shouldHighlight);
    }

    public static boolean placeShape(MouseEvent event, GameState gameState) {
        boolean success = checkCanPlaceShapeOnGrid(event, gameState);

        if (success) {
            if (ruinsPending)
                ruinsPending = false;

            gameState.setRuinsPending(false);
            gameState.addPlayerToTurn();

            if (gameState.haveBothPlayersPlayed()) {
                drawNextCard();
                saveGameState(gameState);
            }
        }

        return success;
    }

    private static void placeInitialTerrain(TerrainType terrainType) {
        CellState[][] currentPlayerGrid = gameState.getPrimaryGrid();
        CellState[][] otherPlayerGrid = gameState.getSecondaryGrid();
        int placed = 0;

        while (placed < 5) {
            int row = random.nextInt(GRID_ROWS);
            int col = random.nextInt(GRID_COLUMNS);

            if (currentPlayerGrid[row][col].getTerrainType() == TerrainType.EMPTY) {
                currentPlayerGrid[row][col].setTerrainType(terrainType);
                otherPlayerGrid[row][col].setTerrainType(terrainType);
                placed++;
            }
        }
    }

    private static void saveGameState(GameState changedGameState) {
        changedGameState.setCurrentSeason(currentSeason);
        changedGameState.setExploreDeck(exploreDeck);
        changedGameState.setCurrentTime(currentTime);
        changedGameState.setCurrentCard(currentCard);
        changedGameState.setCurrentShape(currentShape);
        changedGameState.setCurrentEdict(currentEdict);
        changedGameState.setEdicts(edicts);
        changedGameState.setRuinsPending(false);
        changedGameState.getPlayerTurn().clear();
    }
}
