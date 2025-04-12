package hr.tvz.cartographers.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Season {
    SPRING(0, 8, "Spring"),
    SUMMER(1, 7, "Summer"),
    FALL(2, 6, "Fall"),
    WINTER(3, 5, "Winter"),
    END(4, null, "End");

    private final int position;
    private final Integer threshold;
    private final String label;

    public Season incrementSeason() {
        if (this.equals(END))
            return this;

        return Season.values()[this.position + 1];
    }

    public boolean isFinished() {
        return this.equals(END);
    }
}
