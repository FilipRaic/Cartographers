package hr.tvz.cartographers.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

import static hr.tvz.cartographers.utils.GameUtil.GRID_COLUMNS;
import static hr.tvz.cartographers.utils.GameUtil.GRID_ROWS;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CoordinateUtil {

    public static int adjustCoordinate(int base, List<int[]> shape, boolean isRow) {
        int minOffset = isRow ?
                shape.stream().mapToInt(offset -> offset[0]).min().orElse(0) :
                shape.stream().mapToInt(offset -> offset[1]).min().orElse(0);
        int maxOffset = isRow ?
                shape.stream().mapToInt(offset -> offset[0]).max().orElse(0) :
                shape.stream().mapToInt(offset -> offset[1]).max().orElse(0);

        int gridSize = isRow ? GRID_ROWS : GRID_COLUMNS;

        int adjusted = base;
        if (adjusted + maxOffset >= gridSize) adjusted = gridSize - 1 - maxOffset;
        if (adjusted + minOffset < 0) adjusted = -minOffset;

        return adjusted;
    }
}
