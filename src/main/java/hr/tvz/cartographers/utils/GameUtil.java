package hr.tvz.cartographers.utils;

import hr.tvz.cartographers.enums.Season;
import hr.tvz.cartographers.enums.Shape;
import hr.tvz.cartographers.enums.TerrainType;
import hr.tvz.cartographers.models.Edict;
import hr.tvz.cartographers.models.ExploreCard;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static hr.tvz.cartographers.utils.PaneUtil.*;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameUtil {

    private static GridPane gameGrid;
    private static VBox cardDisplay;
    private static Label seasonLabel;
    private static Label scoreLabel;
    private static Label coinLabel;
    private static Label edictLabel;

    private static final Integer GRID_ROWS = 11;
    private static final Integer GRID_COLUMNS = 11;

    private static final List<Edict> edicts = EdictUtil.generateEdicts();
    private static final List<List<int[]>> allUniqueShapes = Shape.getAllPermutationsForAllShapes();

    private static final List<Edict> currentEdicts = new ArrayList<>();
    private static Season currentSeason = Season.SPRING;
    private static List<int[]> currentShape;
    private static TerrainType currentTerrain;
    private static int coinCount = 0;
    private static int score = 0;

    private static final TerrainType[][] terrainTypeMap = new TerrainType[GRID_ROWS][GRID_COLUMNS];
    private static final boolean[][] isRuins = new boolean[GRID_ROWS][GRID_COLUMNS];
    private static int currentTime = 0;
    private static List<ExploreCard> exploreDeck;
    private static ExploreCard currentCard;
    private static boolean ruinsPending = false;

    private static final Random random = new Random();

    public static void initializeGame(GridPane gameGridInput,
                                      VBox cardDisplayInput,
                                      Label seasonLabelInput,
                                      Label scoreLabelInput,
                                      Label coinLabelInput,
                                      Label edictLabelInput) {
        gameGrid = gameGridInput;
        cardDisplay = cardDisplayInput;
        seasonLabel = seasonLabelInput;
        scoreLabel = scoreLabelInput;
        coinLabel = coinLabelInput;
        edictLabel = edictLabelInput;

        // Initialize map
        for (int i = 0; i < GRID_ROWS; i++) {
            for (int j = 0; j < GRID_COLUMNS; j++) {
                terrainTypeMap[i][j] = TerrainType.EMPTY;
                isRuins[i][j] = false;
            }
        }

        // Randomly place mountains
        int mountainsPlaced = 0;
        while (mountainsPlaced < 5) {
            int row = random.nextInt(GRID_ROWS);
            int col = random.nextInt(GRID_COLUMNS);
            if (terrainTypeMap[row][col] == TerrainType.EMPTY) {
                terrainTypeMap[row][col] = TerrainType.MOUNTAIN;
                mountainsPlaced++;
            }
        }

        // Randomly place ruins
        int ruinsPlaced = 0;
        while (ruinsPlaced < 5) {
            int row = random.nextInt(GRID_ROWS);
            int col = random.nextInt(GRID_COLUMNS);
            if (terrainTypeMap[row][col] == TerrainType.EMPTY && !isRuins[row][col]) {
                isRuins[row][col] = true;
                ruinsPlaced++;
            }
        }

        // Update visual representation
        updateMapVisuals();

        // Initialize edicts for the game
        Collections.shuffle(edicts);
        currentEdicts.add(edicts.get(0)); // A
        currentEdicts.add(edicts.get(1)); // B

        // Initialize explore deck
        initializeExploreDeck();

        // Draw first card
        drawNextCard();

        // Initialize UI
        updateUI();
    }

    public static void highlightShape(MouseEvent event, boolean shouldHighlight) {
        if (currentSeason.isFinished())
            return;

        Pane hoveredPane = (Pane) event.getSource();
        Integer row = GridPane.getRowIndex(hoveredPane);
        Integer col = GridPane.getColumnIndex(hoveredPane);

        if (row == null || col == null || currentShape == null)
            return;

        drawShape(row, col, shouldHighlight);
    }

    public static void placeShape(MouseEvent event) {
        if (currentSeason.isFinished())
            return;

        Pane clickedPane = (Pane) event.getSource();
        Integer row = GridPane.getRowIndex(clickedPane);
        Integer col = GridPane.getColumnIndex(clickedPane);
        if (row == null || col == null || currentShape == null || currentTerrain == null) return;

        int adjustedBaseRow = adjustCoordinate(row, currentShape.stream().mapToInt(o -> o[0]).min().orElse(0), currentShape.stream().mapToInt(o -> o[0]).max().orElse(0));
        int adjustedBaseCol = adjustCoordinate(col, currentShape.stream().mapToInt(o -> o[1]).min().orElse(0), currentShape.stream().mapToInt(o -> o[1]).max().orElse(0));

        boolean isLegal = isPlacementLegal(adjustedBaseRow, adjustedBaseCol);
        if (isLegal) {
            for (int[] offset : currentShape) {
                int newRow = adjustedBaseRow + offset[0];
                int newCol = adjustedBaseCol + offset[1];
                terrainTypeMap[newRow][newCol] = currentTerrain;
                Pane pane = getPaneAt(gameGrid, newRow, newCol);
                if (pane != null) updatePaneStyle(terrainTypeMap, isRuins, pane, newRow, newCol);
            }

            if (ruinsPending)
                ruinsPending = false;

            drawNextCard();
        }
    }

    private static void updateUI() {
        if (!currentSeason.isFinished()) {
            seasonLabel.setText(currentSeason.getLabel());
            edictLabel.setText("Edicts: " + currentEdicts.get(0).getName() + " / " + currentEdicts.get(1).getName());
        } else {
            seasonLabel.setText("Game Over");
            edictLabel.setText("Final Score: " + score);
        }

        scoreLabel.setText("Score: " + score);
        coinLabel.setText("Coins: " + coinCount);
    }

    private static void updateMapVisuals() {
        for (javafx.scene.Node node : gameGrid.getChildren()) {
            if (node instanceof Pane pane) {
                Integer row = GridPane.getRowIndex(pane);
                Integer col = GridPane.getColumnIndex(pane);
                if (row != null && col != null) {
                    updatePaneStyle(terrainTypeMap, isRuins, pane, row, col);
                }
            }
        }
    }

    private static void initializeExploreDeck() {
        exploreDeck = ExploreCard.getExploreCardsDeck(allUniqueShapes);
        Collections.shuffle(exploreDeck);
    }

    private static void drawNextCard() {
        if (currentSeason.equals(Season.END))
            return;

        if (!exploreDeck.isEmpty()) {
            currentCard = exploreDeck.remove(0);
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
        } else {
            endSeason();
        }
    }

    private static void handleAmbush() {
        List<int[]> ambushShape = allUniqueShapes.get(random.nextInt(allUniqueShapes.size()));
        int corner = random.nextInt(4); // 0: TL, 1: TR, 2: BL, 3: BR
        int baseRow = corner < 2 ? 0 : 10;
        int baseCol = corner % 2 == 0 ? 0 : 10;
        boolean placed = false;

        for (int r = 0; r < GRID_ROWS && !placed; r++) {
            for (int c = 0; c < GRID_COLUMNS && !placed; c++) {
                boolean canPlace = true;
                for (int[] offset : ambushShape) {
                    int newRow = baseRow + r + offset[0];
                    int newCol = baseCol + c + offset[1];
                    if (newRow < 0 || newRow >= GRID_ROWS || newCol < 0 || newCol >= GRID_COLUMNS || terrainTypeMap[newRow][newCol] != TerrainType.EMPTY) {
                        canPlace = false;
                        break;
                    }
                }
                if (canPlace) {
                    for (int[] offset : ambushShape) {
                        int newRow = baseRow + r + offset[0];
                        int newCol = baseCol + c + offset[1];
                        terrainTypeMap[newRow][newCol] = TerrainType.MONSTER;
                        Pane pane = getPaneAt(gameGrid, newRow, newCol);
                        if (pane != null) updatePaneStyle(terrainTypeMap, isRuins, pane, newRow, newCol);
                    }
                    placed = true;
                }
            }
        }
        drawNextCard();
    }

    private static void endSeason() {
        int seasonScore = currentEdicts.get(0).score(terrainTypeMap) + currentEdicts.get(1).score(terrainTypeMap);

        for (int r = 0; r < GRID_ROWS; r++) {
            for (int c = 0; c < GRID_COLUMNS; c++) {
                if (terrainTypeMap[r][c] == TerrainType.MONSTER) {
                    if (r > 0 && terrainTypeMap[r - 1][c] == TerrainType.EMPTY) seasonScore--;
                    if (r < 10 && terrainTypeMap[r + 1][c] == TerrainType.EMPTY) seasonScore--;
                    if (c > 0 && terrainTypeMap[r][c - 1] == TerrainType.EMPTY) seasonScore--;
                    if (c < 10 && terrainTypeMap[r][c + 1] == TerrainType.EMPTY) seasonScore--;
                }
            }
        }

        score += seasonScore;
        currentSeason = currentSeason.incrementSeason();

        if (!currentSeason.isFinished()) {
            currentEdicts.clear();
            currentEdicts.add(edicts.get(currentSeason.getPosition() % 2));
            currentEdicts.add(edicts.get(2 + (currentSeason.getPosition() / 2)));
            currentTime = 0;
            initializeExploreDeck();
            drawNextCard();
            updateUI();
        } else {
            cardDisplay.getChildren().clear();
            // Disable all panes in the grid
            for (javafx.scene.Node node : gameGrid.getChildren()) {
                if (node instanceof Pane) {
                    node.setDisable(true); // Disables mouse events and grays out the pane
                }
            }

            updateUI();
        }
    }

    private static void updateCardDisplay() {
        cardDisplay.getChildren().clear();
        if (currentCard != null) {
            Label title = new Label(currentCard.getName() + " (Time: " + currentCard.getTime() + ")");
            cardDisplay.getChildren().add(title);
            for (List<int[]> shape : currentCard.getShapes()) {
                for (TerrainType terrain : currentCard.getTerrains()) {
                    Button option = new Button(terrain.name());
                    option.setOnAction(e -> {
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
            if (newRow < 0 || newRow >= GRID_ROWS || newCol < 0 || newCol >= GRID_COLUMNS || terrainTypeMap[newRow][newCol] != TerrainType.EMPTY) {
                return false;
            }
            if (isRuins[newRow][newCol]) overlapsRuins = true;
        }

        if (ruinsPending && !overlapsRuins) {
            for (int r = 0; r < GRID_ROWS; r++) {
                for (int c = 0; c < GRID_COLUMNS; c++) {
                    boolean canPlace = true;
                    boolean hasRuins = false;
                    for (int[] offset : currentShape) {
                        int nr = r + offset[0];
                        int nc = c + offset[1];
                        if (nr < 0 || nr >= GRID_ROWS || nc < 0 || nc >= GRID_COLUMNS || terrainTypeMap[nr][nc] != TerrainType.EMPTY) {
                            canPlace = false;
                            break;
                        }
                        if (isRuins[nr][nc]) hasRuins = true;
                    }
                    if (canPlace && hasRuins) return false;
                }
            }
        }

        return true;
    }

    private static void drawShape(int baseRow, int baseCol, boolean shouldHighlight) {
        if (currentShape == null) return;
        int adjustedBaseRow = adjustCoordinate(baseRow, currentShape.stream().mapToInt(o -> o[0]).min().orElse(0), currentShape.stream().mapToInt(o -> o[0]).max().orElse(0));
        int adjustedBaseCol = adjustCoordinate(baseCol, currentShape.stream().mapToInt(o -> o[1]).min().orElse(0), currentShape.stream().mapToInt(o -> o[1]).max().orElse(0));

        boolean isLegal = isPlacementLegal(adjustedBaseRow, adjustedBaseCol);
        Set<String> shapePositions = new HashSet<>();
        for (int[] offset : currentShape) {
            int newRow = adjustedBaseRow + offset[0];
            int newCol = adjustedBaseCol + offset[1];
            shapePositions.add(newRow + "," + newCol);
        }

        for (int[] offset : currentShape) {
            int newRow = adjustedBaseRow + offset[0];
            int newCol = adjustedBaseCol + offset[1];
            Pane pane = getPaneAt(gameGrid, newRow, newCol);
            if (pane != null) {
                if (shouldHighlight) {
                    highlightPane(pane, newRow, newCol, shapePositions, isLegal);
                } else {
                    updatePaneStyle(terrainTypeMap, isRuins, pane, newRow, newCol);
                }
            }
        }
    }
}
