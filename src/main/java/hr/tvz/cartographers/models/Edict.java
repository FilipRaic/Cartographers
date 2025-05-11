package hr.tvz.cartographers.models;

import hr.tvz.cartographers.enums.ScoringType;
import hr.tvz.cartographers.enums.TerrainType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

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
        for (int i = 0; i < 11; i++) {
            int terrainCount = countTerrainInRow(playerGrid, i);

            if (terrainCount == count) {
                totalPoints += points;
            }
        }

        return totalPoints;
    }

    private int scoreColumns(CellState[][] playerGrid) {
        int totalPoints = 0;
        for (int j = 0; j < 11; j++) {
            int terrainCount = countTerrainInColumn(playerGrid, j);

            if (terrainCount == count) {
                totalPoints += points;
            }
        }

        return totalPoints;
    }

    private int countTerrainInRow(CellState[][] playerGrid, int row) {
        int terrainCount = 0;
        for (int j = 0; j < 11; j++) {
            if (playerGrid[row][j].getTerrainType() == targetTerrain) {
                terrainCount++;
            }
        }

        return terrainCount;
    }

    private int countTerrainInColumn(CellState[][] playerGrid, int col) {
        int terrainCount = 0;
        for (int i = 0; i < 11; i++) {
            if (playerGrid[i][col].getTerrainType() == targetTerrain) {
                terrainCount++;
            }
        }

        return terrainCount;
    }

    private int scoreAdjacent(CellState[][] playerGrid) {
        int totalPoints = 0;
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                if (playerGrid[i][j].getTerrainType() == targetTerrain && hasAdjacentSecondary(playerGrid, i, j)) {
                    totalPoints += points;
                }
            }
        }

        return totalPoints;
    }

    private int scoreSquare(CellState[][] playerGrid) {
        int totalPoints = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (isSquarePresent(playerGrid, i, j)) {
                    totalPoints += points;
                }
            }
        }

        return totalPoints;
    }

    private boolean isSquarePresent(CellState[][] playerGrid, int i, int j) {
        return playerGrid[i][j].getTerrainType() == targetTerrain &&
                playerGrid[i][j + 1].getTerrainType() == targetTerrain &&
                playerGrid[i + 1][j].getTerrainType() == targetTerrain &&
                playerGrid[i + 1][j + 1].getTerrainType() == targetTerrain;
    }

    private int scoreBasePlusBonus(CellState[][] playerGrid) {
        int totalPoints = 0;
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                if (playerGrid[i][j].getTerrainType() == targetTerrain) {
                    totalPoints += points;

                    if (hasAdjacentSecondary(playerGrid, i, j)) {
                        totalPoints += 2;
                    }
                }
            }
        }

        return totalPoints;
    }

    private boolean hasAdjacentSecondary(CellState[][] playerGrid, int i, int j) {
        return (i > 0 && playerGrid[i - 1][j].getTerrainType() == secondaryTerrain) ||
                (i < 10 && playerGrid[i + 1][j].getTerrainType() == secondaryTerrain) ||
                (j > 0 && playerGrid[i][j - 1].getTerrainType() == secondaryTerrain) ||
                (j < 10 && playerGrid[i][j + 1].getTerrainType() == secondaryTerrain);
    }
}