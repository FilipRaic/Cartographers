package hr.tvz.cartographers.models;

import hr.tvz.cartographers.shared.enums.Player;
import lombok.*;

import java.io.Serializable;

import static hr.tvz.cartographers.utils.GameUtil.GRID_COLUMNS;
import static hr.tvz.cartographers.utils.GameUtil.GRID_ROWS;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class PlayerGameState implements Serializable {

    private int score;
    private CellState[][] grid;

    private final Player player;

    public PlayerGameState(Player player) {
        this.score = 0;
        this.player = player;
        this.grid = new CellState[GRID_ROWS][GRID_COLUMNS];

        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLUMNS; col++) {
                this.grid[row][col] = new CellState();
            }
        }
    }
}
