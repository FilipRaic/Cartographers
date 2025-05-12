package hr.tvz.cartographers.models;

import hr.tvz.cartographers.enums.ScoringType;
import hr.tvz.cartographers.enums.TerrainType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

import static hr.tvz.cartographers.utils.GameUtil.GRID_COLUMNS;
import static hr.tvz.cartographers.utils.GameUtil.GRID_ROWS;

@AllArgsConstructor
@Getter
public class Edict implements Serializable {

    private String name;
    private TerrainType targetTerrain;
    private TerrainType secondaryTerrain;
    private ScoringType scoringType;
    private int points;
    private int count;

    public int score(CellState[][] playerGrid) {
        return switch (scoringType) {
            case ROW_COLUMN_COUNT -> scoreRowColumnCount(playerGrid);
            case ADJACENT -> scoreAdjacent(playerGrid);
            case SQUARE -> scoreSquare(playerGrid);
            case BASE_PLUS_BONUS -> scoreBasePlusBonus(playerGrid);
        };
    }

    private int scoreRowColumnCount(CellState[][] playerGrid) {
        return scoreRows(playerGrid) + scoreColumns(playerGrid);
    }

    private int scoreRows(CellState[][] playerGrid) {
        int totalPoints = 0;
        for (int row = 0; row < GRID_ROWS; row++) {
            int terrainCount = countTerrainInRow(playerGrid, row);

            if (terrainCount == count) {
                totalPoints += points;
            }
        }

        return totalPoints;
    }

    private int scoreColumns(CellState[][] playerGrid) {
        int totalPoints = 0;
        for (int col = 0; col < GRID_COLUMNS; col++) {
            int terrainCount = countTerrainInColumn(playerGrid, col);

            if (terrainCount == count) {
                totalPoints += points;
            }
        }

        return totalPoints;
    }

    private int countTerrainInRow(CellState[][] playerGrid, int row) {
        int terrainCount = 0;
        for (int col = 0; col < GRID_COLUMNS; col++) {
            if (playerGrid[row][col].getTerrainType() == targetTerrain) {
                terrainCount++;
            }
        }

        return terrainCount;
    }

    private int countTerrainInColumn(CellState[][] playerGrid, int col) {
        int terrainCount = 0;
        for (int row = 0; row < GRID_ROWS; row++) {
            if (playerGrid[row][col].getTerrainType() == targetTerrain) {
                terrainCount++;
            }
        }

        return terrainCount;
    }

    private int scoreAdjacent(CellState[][] playerGrid) {
        int totalPoints = 0;
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLUMNS; col++) {
                if (playerGrid[row][col].getTerrainType() == targetTerrain && hasAdjacentSecondary(playerGrid, row, col)) {
                    totalPoints += points;
                }
            }
        }

        return totalPoints;
    }

    private int scoreSquare(CellState[][] playerGrid) {
        int totalPoints = 0;
        for (int row = 0; row < GRID_ROWS - 1; row++) {
            for (int col = 0; col < GRID_COLUMNS - 1; col++) {
                if (isSquarePresent(playerGrid, row, col)) {
                    totalPoints += points;
                }
            }
        }

        return totalPoints;
    }

    private boolean isSquarePresent(CellState[][] playerGrid, int row, int col) {
        return playerGrid[row][col].getTerrainType() == targetTerrain &&
                playerGrid[row][col + 1].getTerrainType() == targetTerrain &&
                playerGrid[row + 1][col].getTerrainType() == targetTerrain &&
                playerGrid[row + 1][col + 1].getTerrainType() == targetTerrain;
    }

    private int scoreBasePlusBonus(CellState[][] playerGrid) {
        int totalPoints = 0;
        for (int row = 0; row < GRID_ROWS - 1; row++) {
            for (int col = 0; col < GRID_COLUMNS - 1; col++) {
                if (playerGrid[row][col].getTerrainType() == targetTerrain) {
                    totalPoints += points;

                    if (hasAdjacentSecondary(playerGrid, row, col)) {
                        totalPoints += 2;
                    }
                }
            }
        }

        return totalPoints;
    }

    private boolean hasAdjacentSecondary(CellState[][] playerGrid, int row, int col) {
        return (row > 0 && playerGrid[row - 1][col].getTerrainType() == secondaryTerrain) ||
                (row < 10 && playerGrid[row + 1][col].getTerrainType() == secondaryTerrain) ||
                (col > 0 && playerGrid[row][col - 1].getTerrainType() == secondaryTerrain) ||
                (col < 10 && playerGrid[row][col + 1].getTerrainType() == secondaryTerrain);
    }
}
