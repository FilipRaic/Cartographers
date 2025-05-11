package hr.tvz.cartographers.utils;

import hr.tvz.cartographers.enums.TerrainType;
import hr.tvz.cartographers.models.Edict;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static hr.tvz.cartographers.enums.ScoringType.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EdictUtil {

    public static List<Edict> getEdicts() {
        return new ArrayList<>(edicts);
    }

    private static final List<Edict> edicts = List.of(
            new Edict("A - The Queen's Arbors",
                    TerrainType.FOREST,
                    null,
                    ROW_COLUMN_COUNT,
                    2, // 2 points per row/column
                    3  // Exactly 3 forest spaces
            ),
            new Edict("B - Canal Lake",
                    TerrainType.WATER,
                    TerrainType.VILLAGE,
                    ADJACENT,
                    3, // 3 points per water space adjacent to village
                    0  // No count needed
            ),
            new Edict("C - Wildholds",
                    TerrainType.VILLAGE,
                    null,
                    SQUARE,
                    4, // 4 points per 2x2 village square
                    4  // 4 spaces in a 2x2 square
            ),
            new Edict("D - Greengold Plains",
                    TerrainType.FARM,
                    TerrainType.WATER,
                    BASE_PLUS_BONUS,
                    1, // 1 point per farm space
                    0  // No count needed, +2 bonus for adjacent water
            )
    );
}
