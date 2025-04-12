package hr.tvz.cartographers.shared.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ScreenConfiguration {

    CARTOGRAPHERS("Cartographers"),
    MENU_SCREEN("screens/menu.fxml"),
    GAME_SCREEN("screens/game.fxml"),
    GAME_BACKGROUND_WIDE_IMAGE("/hr/tvz/cartographers/screens/styles/game-background-wide.png"),
    GAME_BACKGROUND_STANDARD_IMAGE("/hr/tvz/cartographers/screens/styles/game-background.png");

    private final String value;
}
