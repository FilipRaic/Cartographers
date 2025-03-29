package hr.tvz.cartographers.utils;

import hr.tvz.cartographers.enums.TerrainType;
import hr.tvz.cartographers.models.Edict;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EdictGenerator {

    public static List<Edict> generateEdicts() {
        return edicts;
    }

    private static final List<Edict> edicts = new ArrayList<>(List.of(
            new Edict("A - The Queen's Arbors", map -> {
                int points = 0;
                boolean[][] visited = new boolean[11][11]; // To avoid modifying the map
                for (int i = 0; i < 11; i++) {
                    for (int j = 0; j < 11; j++) {
                        if (map[i][j] == TerrainType.FOREST && !visited[i][j]) {
                            int size = getClusterSize(i, j, TerrainType.FOREST, map, visited);
                            points += size <= 3 ? size : 0;
                        }
                    }
                }
                return points;
            }),
            new Edict("B - Canal Lake", map -> {
                int points = 0;
                for (int i = 0; i < 11; i++) {
                    for (int j = 0; j < 11; j++) {
                        if (map[i][j] == TerrainType.WATER) {
                            if ((i > 0 && map[i - 1][j] == TerrainType.FARM) ||
                                    (i < 10 && map[i + 1][j] == TerrainType.FARM) ||
                                    (j > 0 && map[i][j - 1] == TerrainType.FARM) ||
                                    (j < 10 && map[i][j + 1] == TerrainType.FARM)) {
                                points += 2;
                            }
                        }
                    }
                }
                return points;
            }),
            new Edict("C - Wildholds", map -> {
                int points = 0;
                boolean[][] visited = new boolean[11][11];
                for (int i = 0; i < 11; i++) {
                    for (int j = 0; j < 11; j++) {
                        if (map[i][j] == TerrainType.VILLAGE && !visited[i][j]) {
                            int size = getClusterSize(i, j, TerrainType.VILLAGE, map, visited);
                            if (size >= 6) points += 8; // Per rulebook: 8 points per cluster of 6+ village spaces
                        }
                    }
                }
                return points;
            }),
            new Edict("D - Greengold Plains", map -> {
                int points = 0;
                for (int i = 0; i < 11; i++) {
                    for (int j = 0; j < 11; j++) {
                        if (map[i][j] != TerrainType.EMPTY && map[i][j] != TerrainType.MOUNTAIN) {
                            Set<TerrainType> adjacentTypes = new HashSet<>();
                            if (i > 0 && map[i - 1][j] != TerrainType.EMPTY) adjacentTypes.add(map[i - 1][j]);
                            if (i < 10 && map[i + 1][j] != TerrainType.EMPTY) adjacentTypes.add(map[i + 1][j]);
                            if (j > 0 && map[i][j - 1] != TerrainType.EMPTY) adjacentTypes.add(map[i][j - 1]);
                            if (j < 10 && map[i][j + 1] != TerrainType.EMPTY) adjacentTypes.add(map[i][j + 1]);
                            if (adjacentTypes.size() >= 3)
                                points += 3; // 3 points per space adjacent to 3+ terrain types
                        }
                    }
                }
                return points;
            })
    ));

    private static int getClusterSize(int row, int col, TerrainType type, TerrainType[][] map, boolean[][] visited) {
        if (row < 0 || row >= 11 || col < 0 || col >= 11 || map[row][col] != type || visited[row][col]) return 0;
        visited[row][col] = true;
        int size = 1;
        size += getClusterSize(row - 1, col, type, map, visited);
        size += getClusterSize(row + 1, col, type, map, visited);
        size += getClusterSize(row, col - 1, type, map, visited);
        size += getClusterSize(row, col + 1, type, map, visited);
        return size;
    }
}
