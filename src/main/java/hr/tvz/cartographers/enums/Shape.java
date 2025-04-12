package hr.tvz.cartographers.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public static List<List<int[]>> getAllPermutationsForAllShapes() {
        Set<String> seen = new HashSet<>();
        List<List<int[]>> uniqueShapes = new ArrayList<>();

        for (Shape shape : values()) {
            List<int[]> current = shape.getBlocks();
            for (int rot = 0; rot < 4; rot++) {
                List<int[]> rotated = current;
                List<int[]> normalizedRotated = normalize(rotated);
                String serialized = serialize(normalizedRotated);

                if (!seen.contains(serialized)) {
                    seen.add(serialized);
                    uniqueShapes.add(new ArrayList<>(rotated));
                }

                List<int[]> flipped = flipHorizontal(rotated);
                List<int[]> normalizedFlipped = normalize(flipped);
                serialized = serialize(normalizedFlipped);

                if (!seen.contains(serialized)) {
                    seen.add(serialized);
                    uniqueShapes.add(new ArrayList<>(flipped));
                }

                current = rotate90(current);
            }
        }

        return uniqueShapes;
    }

    private static List<int[]> rotate90(List<int[]> shape) {
        List<int[]> rotated = new ArrayList<>();
        for (int[] offset : shape) {
            rotated.add(new int[]{offset[1], -offset[0]});
        }

        return rotated;
    }

    private static List<int[]> flipHorizontal(List<int[]> shape) {
        List<int[]> flipped = new ArrayList<>();
        for (int[] offset : shape) {
            flipped.add(new int[]{offset[0], -offset[1]});
        }

        return flipped;
    }

    private static List<int[]> normalize(List<int[]> shape) {
        int minRow = shape.stream().mapToInt(o -> o[0]).min().orElse(0);
        int minCol = shape.stream().mapToInt(o -> o[1]).min().orElse(0);

        return shape.stream()
                .map(o -> new int[]{o[0] - minRow, o[1] - minCol})
                .sorted((a, b) -> a[0] != b[0] ? Integer.compare(a[0], b[0]) : Integer.compare(a[1], b[1]))
                .toList();
    }

    private static String serialize(List<int[]> shape) {
        return shape.stream()
                .map(o -> o[0] + "," + o[1])
                .collect(Collectors.joining(";"));
    }
}
