package hr.tvz.cartographers.models;

import hr.tvz.cartographers.enums.TerrainType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class ExploreCard implements Serializable {

    private String name;
    private List<List<int[]>> shapes;
    private List<TerrainType> terrains;
    private int time;
    private boolean isRuins;
    private boolean isAmbush;

    public static List<ExploreCard> getExploreCardsDeck(List<List<int[]>> allUniqueShapes) {
        List<ExploreCard> exploreDeck = new ArrayList<>();

        exploreDeck.add(new ExploreCard("Borderlands", List.of(allUniqueShapes.get(0)), List.of(TerrainType.FARM, TerrainType.VILLAGE), 1, false, false));
        exploreDeck.add(new ExploreCard("Farmstead", List.of(allUniqueShapes.get(2)), List.of(TerrainType.FARM), 2, false, false));
        exploreDeck.add(new ExploreCard("Woodland", List.of(allUniqueShapes.get(3)), List.of(TerrainType.FOREST), 2, false, false));
        exploreDeck.add(new ExploreCard("Hamlet", List.of(allUniqueShapes.get(1)), List.of(TerrainType.VILLAGE), 1, false, false));
        exploreDeck.add(new ExploreCard("Riverside", List.of(allUniqueShapes.get(4)), List.of(TerrainType.WATER), 3, false, false));
        exploreDeck.add(new ExploreCard("Crossroads", List.of(allUniqueShapes.get(5)), List.of(TerrainType.FOREST, TerrainType.VILLAGE), 2, false, false));
        exploreDeck.add(new ExploreCard("Lost Barony", null, null, 0, true, false));
        exploreDeck.add(new ExploreCard("Goblin Attack", null, null, 0, false, true));

        return exploreDeck;
    }
}
