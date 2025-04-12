package hr.tvz.cartographers.utils;

import hr.tvz.cartographers.CartographersApplication;
import hr.tvz.cartographers.shared.enums.PlayerType;
import hr.tvz.cartographers.shared.enums.ScreenConfiguration;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MenuUtil {

    public static void startNewGame() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CartographersApplication.class.getResource(ScreenConfiguration.GAME_SCREEN.getValue()));
            Scene gameScene;
            Parent root = fxmlLoader.load();

            if (!CartographersApplication.getPlayerType().equals(PlayerType.SINGLE_PLAYER)) {
                setGameBackgroundImage(root, ScreenConfiguration.GAME_BACKGROUND_WIDE_IMAGE);
                gameScene = new Scene(root, 1580.0, 720.0);
            } else {
                setGameBackgroundImage(root, ScreenConfiguration.GAME_BACKGROUND_STANDARD_IMAGE);
                gameScene = new Scene(root);
            }

            Stage stage = CartographersApplication.getApplicationStage();
            stage.setScene(gameScene);
            stage.show();
        } catch (IOException e) {
            log.error("Error occurred when starting new game: ", e);
        }
    }

    public static void quitGame() {
        Platform.exit();
    }

    private static void setGameBackgroundImage(Parent root, ScreenConfiguration screenConfiguration) {
        if (root instanceof Region rootRegion) {
            URL imagePath = CartographersApplication.class.getResource(screenConfiguration.getValue());

            if (imagePath != null)
                rootRegion.setStyle("-fx-background-image: url('" + imagePath.toExternalForm() + "'); ");
            else
                log.error("Image resource not found");
        } else {
            log.warn("Root node is not a Region, cannot set background image");
        }
    }
}
