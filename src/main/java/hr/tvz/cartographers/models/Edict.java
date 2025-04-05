package hr.tvz.cartographers.models;

import hr.tvz.cartographers.enums.TerrainType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.ToIntFunction;

@AllArgsConstructor
public class Edict {

    @Getter
    private String name;
    private ToIntFunction<TerrainType[][]> scoringLogic;

    public int score(TerrainType[][] map) {
        return scoringLogic.applyAsInt(map);
    }
}
