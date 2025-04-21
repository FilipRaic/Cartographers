package hr.tvz.cartographers.controllers;

import javafx.fxml.FXML;

import static hr.tvz.cartographers.shared.documentation.DocumentationGenerator.generateDocumentation;
import static hr.tvz.cartographers.utils.MenuUtil.*;

public class MenuController {

    @FXML
    protected void onStartNewGameButtonClicked() {
        startNewGame();
    }

    @FXML
    protected void onLoadGameButtonClicked() {
        loadGame();
    }

    @FXML
    protected void onGenerateDocumentation() {
        generateDocumentation();
    }

    @FXML
    protected void onQuitGameButtonClicked() {
        quitGame();
    }
}
