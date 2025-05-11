package hr.tvz.cartographers.models;


import hr.tvz.cartographers.enums.TerrainType;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CellState implements Serializable {

    private String style;
    private TerrainType terrainType = TerrainType.EMPTY;
}
