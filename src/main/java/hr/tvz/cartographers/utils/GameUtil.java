package hr.tvz.cartographers.utils;

import hr.tvz.cartographers.enums.Season;
import hr.tvz.cartographers.enums.Shape;
import hr.tvz.cartographers.enums.TerrainType;
import hr.tvz.cartographers.models.CellState;
import hr.tvz.cartographers.models.Edict;
import hr.tvz.cartographers.models.ExploreCard;
import hr.tvz.cartographers.models.GameState;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static hr.tvz.cartographers.CartographersApplication.getPlayer;
import static hr.tvz.cartographers.shared.enums.Player.SINGLE_PLAYER;
import static hr.tvz.cartographers.utils.GameStateUtil.gameStateToGridPane;
import static hr.tvz.cartographers.utils.PaneUtil.*;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameUtil {

    public static final Integer GRID_ROWS = 11;
    public static final Integer GRID_COLUMNS = 11;
    private static final Integer NUMBER_OF_SPECIAL_TERRAIN_TYPES = 5;

    @Getter
    private static final List<Edict> edicts = EdictUtil.getEdicts();
    @Getter
    private static final List<List<int[]>> allUniqueShapes = Shape.getAllPermutationsForAllShapes();

    @Getter
    private static GridPane primaryGameGrid;
    @Getter
    private static GridPane secondaryGameGrid;
    private static VBox cardDisplay;
    private static Label seasonLabel;
    private static Label scoreLabel;
    private static Label edictLabel;
    private static GameState gameState;

    @Getter
    private static int score = 0;
    @Getter
    private static Edict currentEdict;
    @Getter
    private static int currentTime = 0;
    @Getter
    private static ExploreCard currentCard;
    @Getter
    private static List<int[]> currentShape;
    @Getter
    private static TerrainType currentTerrain;
    @Getter
    private static boolean ruinsPending = false;
    @Getter
    private static List<ExploreCard> exploreDeck;
    @Getter
    private static Season currentSeason = Season.SPRING;

    private static final Random random = new Random();

    public static void initializeGame(GridPane primaryGameGridInput, GridPane secondaryGameGridInput, VBox cardDisplayInput,
                                      Label seasonLabelInput, Label scoreLabelInput, Label edictLabelInput, GameState gameStateInput) {
        setGameComponents(primaryGameGridInput, secondaryGameGridInput, cardDisplayInput, seasonLabelInput, scoreLabelInput, edictLabelInput, gameStateInput);
        if (gameState.isLoadedGameState()) {
            loadSavedGameState();
        } else {
            setupNewGame();
            saveGameState(gameState);
        }
        updateMapVisuals();
        updateUI();
    }

    private static void setGameComponents(GridPane primaryGameGridInput, GridPane secondaryGameGridInput, VBox cardDisplayInput,
                                          Label seasonLabelInput, Label scoreLabelInput, Label edictLabelInput, GameState gameStateInput) {
        primaryGameGrid = primaryGameGridInput;
        secondaryGameGrid = secondaryGameGridInput;
        cardDisplay = cardDisplayInput;
        seasonLabel = seasonLabelInput;
        scoreLabel = scoreLabelInput;
        edictLabel = edictLabelInput;
        gameState = gameStateInput;
    }

    private static void loadSavedGameState() {
        gameStateToGridPane(primaryGameGrid, gameState.getPrimaryGrid());
        gameStateToGridPane(secondaryGameGrid, gameState.getSecondaryGrid());
        currentSeason = gameState.getCurrentSeason();
        score = gameState.getCurrentPlayerGameState().getScore();
        exploreDeck = new ArrayList<>(gameState.getExploreDeck());
        currentEdict = gameState.getCurrentEdict();
        currentCard = gameState.getCurrentCard();
        currentShape = gameState.getCurrentShape();
        ruinsPending = gameState.isRuinsPending();
        if (currentCard != null) {
            updateCardDisplay();
        }
    }

    private static void setupNewGame() {
        placeInitialTerrain(TerrainType.MOUNTAIN);
        placeInitialTerrain(TerrainType.RUINS);
        Collections.shuffle(edicts);
        currentEdict = edicts.getFirst();
        currentSeason = gameState.getCurrentSeason();
        score = gameState.getCurrentPlayerGameState().getScore();
        initializeExploreDeck();
        drawNextCard();
    }

    private static void placeInitialTerrain(TerrainType terrainType) {
        CellState[][] currentPlayerGrid = gameState.getPrimaryGrid();
        CellState[][] otherPlayerGrid = gameState.getSecondaryGrid();
        int placed = 0;
        while (placed < NUMBER_OF_SPECIAL_TERRAIN_TYPES) {
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

    public static void updateGameState(GameState refreshedGameState) {
        if (secondaryGameGrid == null) {
            return;
        }
        gameState.updateGameState(refreshedGameState);
        gameStateToGridPane(secondaryGameGrid, gameState.getSecondaryGrid());
        updateGameStateValues();
        if (currentCard != null) {
            updateCardDisplay();
        }
        updateMapVisuals();
        updateUI();
    }

    private static void updateGameStateValues() {
        currentSeason = gameState.getCurrentSeason();
        score = gameState.getCurrentPlayerGameState().getScore();
        exploreDeck = new ArrayList<>(gameState.getExploreDeck());
        currentCard = gameState.getCurrentCard();
        currentShape = gameState.getCurrentShape();
        ruinsPending = gameState.isRuinsPending();
        Collections.shuffle(edicts);
        currentEdict = gameState.getCurrentEdict();
    }

    public static void highlightShape(MouseEvent event, boolean shouldHighlight) {
        if (isHighlightInvalid()) {
            return;
        }

        Pane hoveredPane = (Pane) event.getSource();
        Integer row = GridPane.getRowIndex(hoveredPane);
        Integer col = GridPane.getColumnIndex(hoveredPane);

        if (row == null || col == null || currentShape == null) {
            return;
        }

        drawShape(row, col, shouldHighlight);
    }

    private static boolean isHighlightInvalid() {
        return currentSeason.isEnd() || gameState.hasPlayerPlayed();
    }

    public static boolean placeShape(MouseEvent event, GameState gameState) {
        if (currentSeason.isEnd() || gameState.hasPlayerPlayed()) {
            return false;
        }

        Pane clickedPane = (Pane) event.getSource();
        Integer row = GridPane.getRowIndex(clickedPane);
        Integer col = GridPane.getColumnIndex(clickedPane);

        if (row == null || col == null || currentShape == null || currentTerrain == null) {
            return false;
        }

        int adjustedBaseRow = adjustCoordinate(row, currentShape.stream().mapToInt(o -> o[0]).min().orElse(0), currentShape.stream().mapToInt(o -> o[0]).max().orElse(0));
        int adjustedBaseCol = adjustCoordinate(col, currentShape.stream().mapToInt(o -> o[1]).min().orElse(0), currentShape.stream().mapToInt(o -> o[1]).max().orElse(0));

        if (!isPlacementLegal(adjustedBaseRow, adjustedBaseCol)) {
            return false;
        }

        placeShapeOnGrid(adjustedBaseRow, adjustedBaseCol);
        updateGameStateAfterPlacement(gameState);

        return true;
    }

    private static void placeShapeOnGrid(int baseRow, int baseCol) {
        for (int[] offset : currentShape) {
            int newRow = baseRow + offset[0];
            int newCol = baseCol + offset[1];
            CellState cellState = gameState.getPrimaryGrid()[newRow][newCol];
            cellState.setTerrainType(currentTerrain);
            Pane pane = getPaneAt(primaryGameGrid, newRow, newCol);
            updatePaneStyle(cellState, pane);
        }
    }

    private static void updateGameStateAfterPlacement(GameState gameState) {
        if (ruinsPending) {
            ruinsPending = false;
        }

        gameState.setRuinsPending(false);
        gameState.addPlayerToTurn();
        if (gameState.haveBothPlayersPlayed()) {
            drawNextCard();
            saveGameState(gameState);
        }
    }

    private static void updateUI() {
        seasonLabel.setText(getSeasonText());
        scoreLabel.setText("Score: " + score);
        edictLabel.setText(getEdictText());
    }

    private static String getSeasonText() {
        if (currentSeason.isEnd()) {
            return "Game Over";
        }
        return currentSeason.getLabel();
    }

    private static String getEdictText() {
        if (currentSeason.isEnd()) {
            String playerResult = "\nYou " + (gameState.hasHigherScore() ? "won" : "lost") + "!";
            return "Final Score: " + score + (!getPlayer().equals(SINGLE_PLAYER) ? playerResult : " DRAW!");
        }
        return "Edict: " + currentEdict.getName();
    }

    private static void updateMapVisuals() {
        updateGridVisuals(primaryGameGrid, gameState.getPrimaryGrid());
        updateGridVisuals(secondaryGameGrid, gameState.getSecondaryGrid());
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

    private static void initializeExploreDeck() {
        exploreDeck = ExploreCard.getExploreCardsDeck(allUniqueShapes);
        Collections.shuffle(exploreDeck);
    }

    private static void drawNextCard() {
        if (currentSeason.equals(Season.END)) {
            return;
        }
        if (exploreDeck.isEmpty()) {
            endSeason();
            return;
        }
        currentCard = exploreDeck.removeFirst();
        if (currentCard.isRuins()) {
            ruinsPending = true;
            drawNextCard();
        } else if (currentCard.isAmbush()) {
            handleAmbush();
        } else {
            updateCardDisplay();
            currentTime += currentCard.getTime();
            if (currentTime >= currentSeason.getThreshold()) {
                endSeason();
            }
        }
    }

    private static void handleAmbush() {
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
        for (int row = 0; row < maxRow; row++) {
            for (int col = 0; col < maxCol; col++) {
                if (isAmbushPlacementValid(ambushShape, baseRow, baseCol, row, col)) {
                    placeAmbushShape(ambushShape, baseRow, baseCol, row, col);
                }
            }
        }
    }

    private static boolean isAmbushPlacementValid(List<int[]> ambushShape, int baseRow, int baseCol, int row, int col) {
        for (int[] offset : ambushShape) {
            int newRow = baseRow + row + offset[0];
            int newCol = baseCol + col + offset[1];
            if (newRow < 0 || newRow >= GRID_ROWS || newCol < 0 || newCol >= GRID_COLUMNS) {
                return false;
            }
            CellState currentPlayerCellState = gameState.getPrimaryGrid()[newRow][newCol];
            CellState otherPlayerCellState = gameState.getSecondaryGrid()[newRow][newCol];
            if (currentPlayerCellState.getTerrainType().isNotEmptyOrRuins() ||
                    otherPlayerCellState.getTerrainType().isNotEmptyOrRuins()) {
                return false;
            }
        }
        return true;
    }

    private static void placeAmbushShape(List<int[]> ambushShape, int baseRow, int baseCol, int row, int col) {
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

    private static void endSeason() {
        int seasonScore = calculateSeasonScore();
        score += seasonScore;
        currentSeason = currentSeason.incrementSeason();
        if (!currentSeason.isEnd()) {
            currentEdict = edicts.get(currentSeason.getPosition() % 2);
            currentTime = 0;
            initializeExploreDeck();
            drawNextCard();
            updateUI();
        } else {
            disableGame();
            updateUI();
        }
    }

    private static int calculateSeasonScore() {
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
        cardDisplay.getChildren().clear();
        for (javafx.scene.Node node : primaryGameGrid.getChildren()) {
            if (node instanceof Pane) {
                node.setDisable(true);
            }
        }
    }

    private static void updateCardDisplay() {
        cardDisplay.getChildren().clear();
        if (currentCard != null) {
            cardDisplay.getChildren().add(new Label(currentCard.getName() + " (Time: " + currentCard.getTime() + ")"));
            for (List<int[]> shape : currentCard.getShapes()) {
                for (TerrainType terrain : currentCard.getTerrains()) {
                    Button option = new Button(terrain.name());
                    option.setOnAction(_ -> {
                        currentShape = shape;
                        currentTerrain = terrain;
                    });
                    cardDisplay.getChildren().add(option);
                }
            }
        }
    }

    private static int adjustCoordinate(int base, int minOffset, int maxOffset) {
        if (base + maxOffset >= 11) return 11 - 1 - maxOffset;
        if (base + minOffset < 0) return -minOffset;
        return base;
    }

    private static boolean isPlacementLegal(int baseRow, int baseCol) {
        boolean overlapsRuins = false;
        for (int[] offset : currentShape) {
            int newRow = baseRow + offset[0];
            int newCol = baseCol + offset[1];

            CellState currentPlayerCellState = gameState.getPrimaryGrid()[newRow][newCol];

            if (newRow >= GRID_ROWS ||
                    newCol >= GRID_ROWS ||
                    currentPlayerCellState.getTerrainType().isNotEmptyOrRuins()
            ) {
                return false;
            }

            if (currentPlayerCellState.getTerrainType() == TerrainType.RUINS)
                overlapsRuins = true;
        }

        if (ruinsPending && !overlapsRuins) {
            for (int row = 0; row < GRID_ROWS; row++) {
                for (int col = 0; col < GRID_COLUMNS; col++) {
                    boolean canPlace = true;
                    boolean hasRuins = false;
                    for (int[] offset : currentShape) {
                        int newRow = row + offset[0];
                        int newCol = col + offset[1];

                        CellState currentPlayerCellState = gameState.getPrimaryGrid()[newRow][newCol];

                        if (newRow >= GRID_ROWS ||
                                newCol >= GRID_COLUMNS ||
                                currentPlayerCellState.getTerrainType().isNotEmptyOrRuins()
                        ) {
                            canPlace = false;
                            break;
                        }

                        if (currentPlayerCellState.getTerrainType() == TerrainType.RUINS)
                            hasRuins = true;
                    }

                    if (canPlace && hasRuins)
                        return false;
                }
            }
        }

        return true;
    }

    private static void drawShape(int baseRow, int baseCol, boolean shouldHighlight) {
        if (currentShape == null) {
            return;
        }

        int adjustedBaseRow = adjustCoordinate(baseRow, currentShape.stream().mapToInt(o -> o[0]).min().orElse(0), currentShape.stream().mapToInt(o -> o[0]).max().orElse(0));
        int adjustedBaseCol = adjustCoordinate(baseCol, currentShape.stream().mapToInt(o -> o[1]).min().orElse(0), currentShape.stream().mapToInt(o -> o[1]).max().orElse(0));
        boolean isLegal = isPlacementLegal(adjustedBaseRow, adjustedBaseCol);
        Set<String> shapePositions = getShapePositions(adjustedBaseRow, adjustedBaseCol);
        drawShapePositions(adjustedBaseRow, adjustedBaseCol, shouldHighlight, isLegal, shapePositions);
    }

    private static Set<String> getShapePositions(int baseRow, int baseCol) {
        Set<String> shapePositions = new HashSet<>();
        for (int[] offset : currentShape) {
            int newRow = baseRow + offset[0];
            int newCol = baseCol + offset[1];
            shapePositions.add(newRow + "," + newCol);
        }
        return shapePositions;
    }

    private static void drawShapePositions(int baseRow, int baseCol, boolean shouldHighlight, boolean isLegal, Set<String> shapePositions) {
        for (int[] offset : currentShape) {
            int newRow = baseRow + offset[0];
            int newCol = baseCol + offset[1];
            Pane pane = getPaneAt(primaryGameGrid, newRow, newCol);
            if (pane != null) {
                if (shouldHighlight) {
                    highlightPane(pane, newRow, newCol, shapePositions, isLegal);
                } else {
                    CellState cellState = gameState.getPrimaryGrid()[newRow][newCol];
                    updatePaneStyle(cellState, pane);
                }
            }
        }
    }
}
