package hr.tvz.cartographers.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TerrainType {

    EMPTY("transparent"),
    FOREST("#228B22"),
    VILLAGE("#DAA520"),
    FARM("#FFD700"),
    WATER("#00BFFF"),
    MONSTER("#8B0000"),
    MOUNTAIN("#A9A9A9");

    private final String color;
}
