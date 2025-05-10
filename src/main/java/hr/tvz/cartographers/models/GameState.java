package hr.tvz.cartographers.models;

import hr.tvz.cartographers.enums.Season;
import hr.tvz.cartographers.enums.TerrainType;
import hr.tvz.cartographers.shared.enums.Player;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static hr.tvz.cartographers.CartographersApplication.getPlayer;
import static hr.tvz.cartographers.shared.enums.Player.PLAYER_ONE;
import static hr.tvz.cartographers.shared.enums.Player.SINGLE_PLAYER;
import static hr.tvz.cartographers.utils.GameUtil.GRID_COLUMNS;
import static hr.tvz.cartographers.utils.GameUtil.GRID_ROWS;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class GameState implements Serializable {

    private CellState[][] primaryGrid;
    private CellState[][] secondaryGrid;
    private Season currentSeason;
    private int score;
    private int currentTime;
    private List<ExploreCard> exploreDeck;
    private ExploreCard currentCard;
    private List<int[]> currentShape;
    private TerrainType currentTerrain;
    private boolean ruinsPending;

    public GameState() {
        primaryGrid = new CellState[GRID_ROWS][GRID_COLUMNS];
        secondaryGrid = new CellState[GRID_ROWS][GRID_COLUMNS];

        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLUMNS; col++) {
                primaryGrid[row][col] = new CellState();
                secondaryGrid[row][col] = new CellState();
            }
        }

        currentSeason = Season.SPRING;
        score = 0;
        currentTime = 0;
        exploreDeck = new ArrayList<>();
        currentCard = null;
        currentShape = null;
        currentTerrain = null;
        ruinsPending = false;
    }

    public CellState[][] getPrimaryGrid() {
        Player player = getPlayer();

        return (player.equals(PLAYER_ONE) || player.equals(SINGLE_PLAYER)) ? primaryGrid : secondaryGrid;
    }

    public void setPrimaryGrid(CellState[][] primaryGrid) {
        Player player = getPlayer();

        if (player.equals(PLAYER_ONE) || player.equals(SINGLE_PLAYER))
            this.primaryGrid = primaryGrid;
        else
            this.secondaryGrid = primaryGrid;
    }

    public CellState[][] getSecondaryGrid() {
        Player player = getPlayer();

        return (player.equals(PLAYER_ONE) || player.equals(SINGLE_PLAYER)) ? secondaryGrid : primaryGrid;
    }

    public void setSecondaryGrid(CellState[][] secondaryGrid) {
        Player player = getPlayer();

        if (player.equals(PLAYER_ONE) || player.equals(SINGLE_PLAYER))
            this.secondaryGrid = secondaryGrid;
        else
            this.primaryGrid = secondaryGrid;
    }

    public boolean isLoadedGameState() {
        return !this.equals(new GameState());
    }
}
