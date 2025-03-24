package hr.tvz.cartographers.models;

import hr.tvz.cartographers.enums.TerrainType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ExploreCard {

    String name;
    List<List<int[]>> shapes;
    List<TerrainType> terrains;
    int time;
    boolean isRuins;
    boolean isAmbush;
}
