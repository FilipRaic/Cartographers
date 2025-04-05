package hr.tvz.cartographers.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Shape {

    SINGLE(List.of(new int[]{0, 0})),
    DOUBLE(List.of(new int[]{0, 0}, new int[]{0, 1})),
    TRIPLE(List.of(new int[]{0, 0}, new int[]{0, 1}, new int[]{0, 2})),
    SQUARE(List.of(new int[]{0, 0}, new int[]{0, 1}, new int[]{1, 0}, new int[]{1, 1})),
    T_SHAPE(List.of(new int[]{0, 0}, new int[]{0, 1}, new int[]{0, -1}, new int[]{1, 0})),
    S_SHAPE(List.of(new int[]{0, 0}, new int[]{0, 1}, new int[]{1, -1}, new int[]{1, 0})),
    Z_SHAPE(List.of(new int[]{0, 0}, new int[]{0, -1}, new int[]{1, 0}, new int[]{1, 1})),
    L_SHAPE(List.of(new int[]{0, 0}, new int[]{1, 0}, new int[]{2, 0}, new int[]{2, -1}));

    private final List<int[]> blocks;

    public static List<Shape> list() {
        return new ArrayList<>(List.of(Shape.values()));
    }
}
