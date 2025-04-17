package hr.tvz.cartographers.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

import static hr.tvz.cartographers.utils.GameUtil.GRID_COLUMNS;
import static hr.tvz.cartographers.utils.GameUtil.GRID_ROWS;

@Getter
@Setter
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
}
