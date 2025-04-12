package hr.tvz.cartographers.controllers;

import hr.tvz.cartographers.shared.documentation.DocumentationGenerator;
import hr.tvz.cartographers.utils.MenuUtil;
import javafx.fxml.FXML;

public class MenuController {

    @FXML
    protected void onStartNewGameButtonClicked() {
        MenuUtil.startNewGame();
    }

    @FXML
    protected void onGenerateDocumentation() {
        DocumentationGenerator.generateDocumentation();
    }

    @FXML
    protected void onQuitGameButtonClicked() {
        MenuUtil.quitGame();
    }
}
