package hr.tvz.cartographers.controllers;

import hr.tvz.cartographers.enums.Shape;
import hr.tvz.cartographers.enums.TerrainType;
import hr.tvz.cartographers.models.Edict;
import hr.tvz.cartographers.models.ExploreCard;
import hr.tvz.cartographers.utils.EdictGenerator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GameController {

    private static final Integer GRID_ROWS = 11;
    private static final Integer GRID_COLUMNS = 11;

    @FXML
    private GridPane gameGrid;
    @FXML
    private VBox cardDisplay;
    @FXML
    private Label seasonLabel;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label coinLabel;
    @FXML
    private Label edictLabel;

    private static final List<Edict> edicts = EdictGenerator.generateEdicts();
    private static final List<Shape> shapes = Shape.list();

    private final TerrainType[][] terrainTypeMap = new TerrainType[GRID_ROWS][GRID_COLUMNS];
    private final boolean[][] isRuins = new boolean[GRID_ROWS][GRID_COLUMNS];
    private int coinCount = 0;
    private int score = 0;
    private int currentSeason = 0;
    private final String[] seasons = {"Spring", "Summer", "Fall", "Winter"};
    private final int[] seasonThresholds = {8, 7, 6, 5};
    private int currentTime = 0;

    private List<List<int[]>> allUniqueShapes;
    private List<int[]> currentShape;
    private TerrainType currentTerrain;
    private boolean ruinsPending = false;

    private final Random random = new Random();

    private List<ExploreCard> exploreDeck;
    private ExploreCard currentCard;

    private final List<Edict> currentEdicts = new ArrayList<>();

    @FXML
    public void initialize() {
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

        // Generate all unique shape orientations
        allUniqueShapes = generateUniqueShapes();

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

    @FXML
    protected void onMouseHover(MouseEvent event) {
        if (currentSeason >= 4) return; // Ignore if game is over
        Pane hoveredPane = (Pane) event.getSource();
        Integer row = GridPane.getRowIndex(hoveredPane);
        Integer col = GridPane.getColumnIndex(hoveredPane);
        if (row == null || col == null || currentShape == null) return;
        drawShape(row, col, true);
    }

    @FXML
    protected void onMouseExit(MouseEvent event) {
        if (currentSeason >= 4) return; // Ignore if game is over
        Pane hoveredPane = (Pane) event.getSource();
        Integer row = GridPane.getRowIndex(hoveredPane);
        Integer col = GridPane.getColumnIndex(hoveredPane);
        if (row == null || col == null || currentShape == null) return;
        drawShape(row, col, false);
    }

    @FXML
    protected void onMouseClick(MouseEvent event) {
        if (currentSeason >= 4) return; // Ignore if game is over
        Pane clickedPane = (Pane) event.getSource();
        Integer row = GridPane.getRowIndex(clickedPane);
        Integer col = GridPane.getColumnIndex(clickedPane);
        if (row == null || col == null || currentShape == null || currentTerrain == null) return;

        int adjustedBaseRow = adjustCoordinate(row, currentShape.stream().mapToInt(o -> o[0]).min().orElse(0), currentShape.stream().mapToInt(o -> o[0]).max().orElse(0));
        int adjustedBaseCol = adjustCoordinate(col, currentShape.stream().mapToInt(o -> o[1]).min().orElse(0), currentShape.stream().mapToInt(o -> o[1]).max().orElse(0));

        boolean isLegal = isPlacementLegal(adjustedBaseRow, adjustedBaseCol);
        if (isLegal) {
            placeShape(adjustedBaseRow, adjustedBaseCol);
            if (ruinsPending) ruinsPending = false;
            drawNextCard();
        }
    }

    private void updateMapVisuals() {
        for (javafx.scene.Node node : gameGrid.getChildren()) {
            if (node instanceof Pane pane) {
                Integer row = GridPane.getRowIndex(pane);
                Integer col = GridPane.getColumnIndex(pane);
                if (row != null && col != null) {
                    updatePaneStyle(pane, row, col);
                }
            }
        }
    }

    private void updateUI() {
        if (currentSeason < 4) {
            seasonLabel.setText(seasons[currentSeason]);
            edictLabel.setText("Edicts: " + currentEdicts.get(0).getName() + " / " + currentEdicts.get(1).getName());
        } else {
            seasonLabel.setText("Game Over");
            edictLabel.setText("Final Score: " + score);
        }
        scoreLabel.setText("Score: " + score);
        coinLabel.setText("Coins: " + coinCount);
    }

    private List<List<int[]>> generateUniqueShapes() {
        Set<String> seen = new HashSet<>();
        List<List<int[]>> uniqueShapes = new ArrayList<>();

        for (Shape shape : shapes) {
            List<int[]> current = shape.getBlocks();
            for (int rot = 0; rot < 4; rot++) {
                List<int[]> rotated = current;
                List<int[]> normalizedRotated = normalize(rotated);
                String serialized = serialize(normalizedRotated);
                if (!seen.contains(serialized)) {
                    seen.add(serialized);
                    uniqueShapes.add(new ArrayList<>(rotated));
                }

                List<int[]> flipped = flipHorizontal(rotated);
                List<int[]> normalizedFlipped = normalize(flipped);
                serialized = serialize(normalizedFlipped);
                if (!seen.contains(serialized)) {
                    seen.add(serialized);
                    uniqueShapes.add(new ArrayList<>(flipped));
                }

                current = rotate90(current);
            }
        }
        return uniqueShapes;
    }

    private List<int[]> rotate90(List<int[]> shape) {
        List<int[]> rotated = new ArrayList<>();
        for (int[] offset : shape) {
            rotated.add(new int[]{offset[1], -offset[0]});
        }
        return rotated;
    }

    private List<int[]> flipHorizontal(List<int[]> shape) {
        List<int[]> flipped = new ArrayList<>();
        for (int[] offset : shape) {
            flipped.add(new int[]{offset[0], -offset[1]});
        }
        return flipped;
    }

    private List<int[]> normalize(List<int[]> shape) {
        int minRow = shape.stream().mapToInt(o -> o[0]).min().orElse(0);
        int minCol = shape.stream().mapToInt(o -> o[1]).min().orElse(0);
        return shape.stream()
                .map(o -> new int[]{o[0] - minRow, o[1] - minCol})
                .sorted((a, b) -> a[0] != b[0] ? Integer.compare(a[0], b[0]) : Integer.compare(a[1], b[1]))
                .toList();
    }

    private String serialize(List<int[]> shape) {
        return shape.stream()
                .map(o -> o[0] + "," + o[1])
                .collect(Collectors.joining(";"));
    }

    private void initializeExploreDeck() {
        exploreDeck = new ArrayList<>();

        // Sample explore cards (based on game deck)
        exploreDeck.add(new ExploreCard("Borderlands", List.of(allUniqueShapes.get(0)), List.of(TerrainType.FARM, TerrainType.VILLAGE), 1, false, false));
        exploreDeck.add(new ExploreCard("Farmstead", List.of(allUniqueShapes.get(2)), List.of(TerrainType.FARM), 2, false, false));
        exploreDeck.add(new ExploreCard("Woodland", List.of(allUniqueShapes.get(3)), List.of(TerrainType.FOREST), 2, false, false));
        exploreDeck.add(new ExploreCard("Hamlet", List.of(allUniqueShapes.get(1)), List.of(TerrainType.VILLAGE), 1, false, false));
        exploreDeck.add(new ExploreCard("Riverside", List.of(allUniqueShapes.get(4)), List.of(TerrainType.WATER), 3, false, false));
        exploreDeck.add(new ExploreCard("Crossroads", List.of(allUniqueShapes.get(5)), List.of(TerrainType.FOREST, TerrainType.VILLAGE), 2, false, false));
        exploreDeck.add(new ExploreCard("Lost Barony", null, null, 0, true, false)); // Ruins
        exploreDeck.add(new ExploreCard("Goblin Attack", null, null, 0, false, true)); // Ambush

        Collections.shuffle(exploreDeck);
    }

    private void drawNextCard() {
        if (currentSeason >= 4) {
            return; // Game is over, no more cards to draw
        }
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
                if (currentTime >= seasonThresholds[currentSeason]) {
                    endSeason();
                }
            }
        } else {
            endSeason();
        }
    }

    private void handleAmbush() {
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
                        Pane pane = getPaneAt(newRow, newCol);
                        if (pane != null) updatePaneStyle(pane, newRow, newCol);
                    }
                    placed = true;
                }
            }
        }
        drawNextCard();
    }

    private void endSeason() {
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
        currentSeason++;

        if (currentSeason < 4) {
            currentEdicts.clear();
            currentEdicts.add(edicts.get(currentSeason % 2));
            currentEdicts.add(edicts.get(2 + (currentSeason / 2)));
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

    private void updateCardDisplay() {
        cardDisplay.getChildren().clear();
        if (currentCard != null) {
            Label title = new Label(currentCard.getName() + " (Time: " + currentCard.getName() + ")");
            cardDisplay.getChildren().add(title);
            for (List<int[]> shape : currentCard.getShapes()) {
                for (TerrainType terrain : currentCard.getTerrains()) {
                    Button option = new Button(terrain + " - Shape " + allUniqueShapes.indexOf(shape));
                    option.setOnAction(e -> {
                        currentShape = shape;
                        currentTerrain = terrain;
                    });
                    cardDisplay.getChildren().add(option);
                }
            }
        }
    }

    private void updatePaneStyle(Pane pane, int row, int col) {
        TerrainType terrain = terrainTypeMap[row][col];
        String style = "-fx-background-color: " + terrain.getColor() + ";";
        if (isRuins[row][col] && terrain == TerrainType.EMPTY) {
            style += "-fx-border-color: black; -fx-border-width: 2;";
        }
        pane.setStyle(style);
    }

    private int adjustCoordinate(int base, int minOffset, int maxOffset) {
        if (base + maxOffset >= 11) return 11 - 1 - maxOffset;
        if (base + minOffset < 0) return -minOffset;
        return base;
    }

    private boolean isPlacementLegal(int baseRow, int baseCol) {
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

    private void placeShape(int baseRow, int baseCol) {
        for (int[] offset : currentShape) {
            int newRow = baseRow + offset[0];
            int newCol = baseCol + offset[1];
            terrainTypeMap[newRow][newCol] = currentTerrain;
            Pane pane = getPaneAt(newRow, newCol);
            if (pane != null) updatePaneStyle(pane, newRow, newCol);
        }
    }

    private void drawShape(int baseRow, int baseCol, boolean highlight) {
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
            Pane pane = getPaneAt(newRow, newCol);
            if (pane != null) {
                if (highlight) {
                    highlightPane(pane, newRow, newCol, shapePositions, isLegal);
                } else {
                    updatePaneStyle(pane, newRow, newCol);
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

    private void highlightPane(Pane pane, int row, int col, Set<String> shapePositions, boolean isLegal) {
        String borderColor = isLegal ? "#00FF00" : "#FF0000";
        StringBuilder style = new StringBuilder("-fx-border-color: " + borderColor + "; -fx-border-width: ");
        StringBuilder borderWidth = new StringBuilder();
        String top = (row - 1) + "," + col;
        String right = row + "," + (col + 1);
        String bottom = (row + 1) + "," + col;
        String left = row + "," + (col - 1);
        borderWidth.append(!shapePositions.contains(top) ? "2 " : "0 ");
        borderWidth.append(!shapePositions.contains(right) ? "2 " : "0 ");
        borderWidth.append(!shapePositions.contains(bottom) ? "2 " : "0 ");
        borderWidth.append(!shapePositions.contains(left) ? "2" : "0");
        style.append(borderWidth);
        pane.setStyle(style.toString());
    }
}