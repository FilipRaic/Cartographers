package hr.tvz.cartographers.controllers;

import hr.tvz.cartographers.CartographersApplication;
import hr.tvz.cartographers.documentation.DocumentationGenerator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class MenuController {

    @FXML
    protected void onStartNewGameButtonClicked() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CartographersApplication.class.getResource("screens/game.fxml"));
            Scene gameScene = new Scene(fxmlLoader.load());
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