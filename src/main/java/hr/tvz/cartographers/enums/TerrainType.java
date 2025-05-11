package hr.tvz.cartographers.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum TerrainType {

    EMPTY("transparent"),
    FOREST("#228B22"),
    VILLAGE("#DAA520"),
    FARM("#FFD700"),
    WATER("#00BFFF"),
    MONSTER("#8B0000"),
    MOUNTAIN("#A9A9A9"),
    RUINS("transparent");

    private final String color;

    public boolean isNotEmptyOrRuins() {
        return !this.isIn(EMPTY, RUINS);
    }

    public boolean isIn(TerrainType... terrainTypes) {
        for (TerrainType terrainType : terrainTypes) {
            if (this.equals(terrainType)) {
                return true;
            }
        }

        return false;
    }
}
