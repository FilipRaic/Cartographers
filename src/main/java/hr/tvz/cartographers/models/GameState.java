package hr.tvz.cartographers.models;

import hr.tvz.cartographers.enums.Season;
import hr.tvz.cartographers.enums.TerrainType;
import hr.tvz.cartographers.shared.enums.Player;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static hr.tvz.cartographers.CartographersApplication.getPlayer;
import static hr.tvz.cartographers.shared.enums.Player.*;
import static hr.tvz.cartographers.utils.GameUtil.GRID_COLUMNS;
import static hr.tvz.cartographers.utils.GameUtil.GRID_ROWS;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class GameState implements Serializable {

    private PlayerGameState playerOneGameState;
    private PlayerGameState playerTwoGameState;
    private Season currentSeason;
    private List<ExploreCard> exploreDeck;
    private int currentTime;
    private ExploreCard currentCard;
    private List<int[]> currentShape;
    private Edict currentEdict;
    private List<Edict> edicts;
    private boolean ruinsPending;
    private final Set<Player> playerTurn;

    public GameState() {
        this.playerOneGameState = new PlayerGameState(PLAYER_ONE);
        this.playerTwoGameState = new PlayerGameState(PLAYER_TWO);
        this.currentSeason = Season.SPRING;
        this.exploreDeck = new ArrayList<>();
        this.currentTime = 0;
        this.currentCard = null;
        this.currentShape = null;
        this.currentEdict = null;
        this.edicts = new ArrayList<>();
        this.ruinsPending = false;
        this.playerTurn = new HashSet<>();
    }

    public CellState[][] getPrimaryGrid() {
        Player player = getPlayer();
        return (player.equals(PLAYER_ONE) || player.equals(SINGLE_PLAYER)) ?
                this.playerOneGameState.getGrid() :
                this.playerTwoGameState.getGrid();
    }

    public CellState[][] getSecondaryGrid() {
        Player player = getPlayer();
        return (player.equals(PLAYER_ONE) || player.equals(SINGLE_PLAYER)) ?
                this.playerTwoGameState.getGrid() :
                this.playerOneGameState.getGrid();
    }

    public PlayerGameState getCurrentPlayerGameState() {
        Player player = getPlayer();

        return (player.equals(PLAYER_ONE) || player.equals(SINGLE_PLAYER)) ?
                this.playerOneGameState :
                this.playerTwoGameState;
    }

    public boolean isLoadedGameState() {
        return !this.equals(new GameState());
    }

    public void addPlayerToTurn() {
        Player currentPlayer = getPlayer();

        this.playerTurn.add(currentPlayer);
    }

    public boolean hasPlayerPlayed() {
        Player currentPlayer = getPlayer();

        return !currentPlayer.equals(SINGLE_PLAYER) && playerTurn.contains(currentPlayer);
    }

    public boolean haveBothPlayersPlayed() {
        if (getPlayer().equals(SINGLE_PLAYER)) {
            return true;
        }

        return this.playerTurn.size() == 2;
    }

    public boolean hasHigherScore() {
        Player currentPlayer = getPlayer();

        return this.playerOneGameState.getScore() > this.playerTwoGameState.getScore() && !currentPlayer.equals(PLAYER_ONE) ||
                this.playerOneGameState.getScore() < this.playerTwoGameState.getScore() && !currentPlayer.equals(PLAYER_TWO);
    }

    public void updateGameState(GameState gameState) {
        this.playerOneGameState = deepCopyPlayerGameState(gameState.getPlayerOneGameState());
        this.playerTwoGameState = deepCopyPlayerGameState(gameState.getPlayerTwoGameState());
        this.currentSeason = gameState.getCurrentSeason();
        this.exploreDeck = deepCopyExploreDeck(gameState.getExploreDeck());
        this.currentTime = gameState.getCurrentTime();
        this.currentCard = deepCopyExploreCard(gameState.getCurrentCard());
        this.currentShape = gameState.getCurrentShape();
        this.currentEdict = gameState.getCurrentEdict();
        this.edicts = gameState.getEdicts();
        this.ruinsPending = gameState.isRuinsPending();
        this.playerTurn.clear();
        this.playerTurn.addAll(gameState.getPlayerTurn());
    }

    private PlayerGameState deepCopyPlayerGameState(PlayerGameState source) {
        if (source == null)
            return null;

        PlayerGameState copy = new PlayerGameState(source.getPlayer());
        copy.setScore(source.getScore());
        copy.setGrid(deepCopyCellStateGrid(source.getGrid()));

        return copy;
    }

    private CellState[][] deepCopyCellStateGrid(CellState[][] source) {
        if (source == null)
            return new CellState[GRID_ROWS][GRID_COLUMNS];

        CellState[][] copy = new CellState[GRID_ROWS][GRID_COLUMNS];
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLUMNS; col++) {
                if (source[row][col] != null) {
                    copy[row][col] = new CellState(source[row][col].getStyle(), source[row][col].getTerrainType());
                } else {
                    copy[row][col] = new CellState();
                }
            }
        }
        return copy;
    }

    private List<ExploreCard> deepCopyExploreDeck(List<ExploreCard> source) {
        if (source == null)
            return new ArrayList<>();

        List<ExploreCard> copy = new ArrayList<>();
        for (ExploreCard card : source) {
            copy.add(deepCopyExploreCard(card));
        }

        return copy;
    }

    private ExploreCard deepCopyExploreCard(ExploreCard source) {
        if (source == null)
            return null;

        List<List<int[]>> shapesCopy = new ArrayList<>();
        if (source.getShapes() != null) {
            for (List<int[]> shapeList : source.getShapes()) {
                List<int[]> shapeListCopy = new ArrayList<>();
                for (int[] coords : shapeList) {
                    shapeListCopy.add(coords != null ? coords.clone() : null);
                }
                shapesCopy.add(shapeListCopy);
            }
        }

        List<TerrainType> terrainsCopy = source.getTerrains() != null ? new ArrayList<>(source.getTerrains()) : null;

        ExploreCard copy = new ExploreCard();
        copy.setName(source.getName());
        copy.setTime(source.getTime());
        copy.setShapes(shapesCopy);
        copy.setTerrains(terrainsCopy);
        copy.setRuins(source.isRuins());
        copy.setAmbush(source.isAmbush());

        return copy;
    }
}
