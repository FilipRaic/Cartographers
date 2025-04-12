package hr.tvz.cartographers.shared.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum PlayerType {
    SINGLE_PLAYER("Single player"),
    PLAYER_ONE("Player one"),
    PLAYER_TWO("Player two");

    private final String label;
}
