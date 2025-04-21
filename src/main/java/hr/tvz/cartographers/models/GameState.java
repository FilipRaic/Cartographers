package hr.tvz.cartographers.models;

import hr.tvz.cartographers.shared.enums.Player;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

import static hr.tvz.cartographers.CartographersApplication.getPlayer;
import static hr.tvz.cartographers.shared.enums.Player.PLAYER_ONE;
import static hr.tvz.cartographers.shared.enums.Player.SINGLE_PLAYER;
import static hr.tvz.cartographers.utils.GameUtil.GRID_COLUMNS;
import static hr.tvz.cartographers.utils.GameUtil.GRID_ROWS;

@Setter
@ToString
@AllArgsConstructor
public class GameState implements Serializable {

    private CellState[][] primaryGrid;
    private CellState[][] secondaryGrid;

    public GameState() {
        primaryGrid = new CellState[GRID_ROWS][GRID_COLUMNS];
        secondaryGrid = new CellState[GRID_ROWS][GRID_COLUMNS];

        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLUMNS; col++) {
                primaryGrid[row][col] = new CellState();
                secondaryGrid[row][col] = new CellState();
            }
        }
    }

    public CellState[][] getPrimaryGrid() {
        Player player = getPlayer();

        if (player.equals(PLAYER_ONE) || player.equals(SINGLE_PLAYER)) {
            return primaryGrid;
        } else {
            return secondaryGrid;
        }
    }

    public CellState[][] getSecondaryGrid() {
        Player player = getPlayer();

        if (player.equals(PLAYER_ONE) || player.equals(SINGLE_PLAYER)) {
            return secondaryGrid;
        } else {
            return primaryGrid;
        }
    }
}
