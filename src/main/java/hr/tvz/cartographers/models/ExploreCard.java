package hr.tvz.cartographers.models;

import hr.tvz.cartographers.enums.TerrainType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ExploreCard {

    private String name;
    private List<List<int[]>> shapes;
    private List<TerrainType> terrains;
    private int time;
    private boolean isRuins;
    private boolean isAmbush;
}
