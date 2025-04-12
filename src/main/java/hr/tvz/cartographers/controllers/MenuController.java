package hr.tvz.cartographers.controllers;

import hr.tvz.cartographers.CartographersApplication;
import hr.tvz.cartographers.shared.documentation.DocumentationGenerator;
import hr.tvz.cartographers.shared.enums.PlayerType;
import hr.tvz.cartographers.shared.enums.ScreenConfiguration;
import hr.tvz.cartographers.utils.GameUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class MenuController {

    @FXML
    protected void onStartNewGameButtonClicked() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CartographersApplication.class.getResource(ScreenConfiguration.GAME_SCREEN.getValue()));
            Scene gameScene;
            Parent root = fxmlLoader.load();

            if (!CartographersApplication.getPlayerType().equals(PlayerType.SINGLE_PLAYER)) {
                GameUtil.setGameBackgroundImage(root, ScreenConfiguration.GAME_BACKGROUND_WIDE_IMAGE);
                gameScene = new Scene(root, 1580.0, 720.0);
            } else {
                GameUtil.setGameBackgroundImage(root, ScreenConfiguration.GAME_BACKGROUND_STANDARD_IMAGE);
                gameScene = new Scene(root);
            }

            Stage stage = CartographersApplication.getApplicationStage();
            stage.setScene(gameScene);
            stage.show();
        } catch (IOException e) {
            log.error("Error occurred when starting new game: ", e);
        }
    }

    @FXML
    protected void onGenerateDocumentation() {
        DocumentationGenerator.generateDocumentation();
    }

    @FXML
    protected void onQuitGameButtonClicked() {
        Platform.exit();
    }
}