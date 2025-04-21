package hr.tvz.cartographers.utils;

import hr.tvz.cartographers.CartographersApplication;
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

import static hr.tvz.cartographers.CartographersApplication.getApplicationStage;
import static hr.tvz.cartographers.CartographersApplication.getPlayer;
import static hr.tvz.cartographers.shared.enums.Player.SINGLE_PLAYER;
import static hr.tvz.cartographers.shared.enums.ScreenConfiguration.*;
import static hr.tvz.cartographers.utils.GameStateUtil.startNewGameSaveGameStateThread;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MenuUtil {

    public static void startNewGame() {
        try {
            startNewGameSaveGameStateThread();
            setGameSceneToStage();
        } catch (IOException e) {
            log.error("Error occurred when starting new game: ", e);
        }
    }

    public static void loadGame() {
        try {
            setGameSceneToStage();
        } catch (IOException e) {
            log.error("Error occurred when loading existing game: ", e);
        }
    }

    public static void quitGame() {
        Platform.exit();
    }

    public static void returnToMenu() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CartographersApplication.class.getResource(ScreenConfiguration.MENU_SCREEN.getValue()));
            Scene scene = new Scene(fxmlLoader.load());

            Stage stage = getApplicationStage();
            stage.setTitle(ScreenConfiguration.CARTOGRAPHERS.getValue());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            log.error("Error occurred when returning to menu: ", e);
        }
    }

    private static void setGameSceneToStage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CartographersApplication.class.getResource(GAME_SCREEN.getValue()));
        Scene gameScene;
        Parent root = fxmlLoader.load();

        if (!getPlayer().equals(SINGLE_PLAYER)) {
            setGameBackgroundImage(root, GAME_BACKGROUND_WIDE_IMAGE);
            gameScene = new Scene(root, 1580.0, 720.0);
        } else {
            setGameBackgroundImage(root, GAME_BACKGROUND_STANDARD_IMAGE);
            gameScene = new Scene(root);
        }

        Stage stage = getApplicationStage();
        stage.setScene(gameScene);
        stage.show();
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
