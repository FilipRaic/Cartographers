package hr.tvz.cartographers.controllers;

import hr.tvz.cartographers.CartographersApplication;
import hr.tvz.cartographers.documentation.DocumentationGenerator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;

public class MenuController {

    @FXML
    protected void onStartNewGameButtonClicked() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CartographersApplication.class.getResource("screens/game.fxml"));
            Scene gameScene = new Scene(fxmlLoader.load());

            CartographersApplication.applicationStage.setScene(gameScene);
            CartographersApplication.applicationStage.show();
        } catch (IOException e) {
            e.printStackTrace();
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