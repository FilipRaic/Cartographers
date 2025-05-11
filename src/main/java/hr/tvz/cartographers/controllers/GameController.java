package hr.tvz.cartographers.controllers;

import hr.tvz.cartographers.models.GameState;
import hr.tvz.cartographers.shared.enums.Player;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;

import static hr.tvz.cartographers.CartographersApplication.getPlayer;
import static hr.tvz.cartographers.shared.enums.Player.*;
import static hr.tvz.cartographers.utils.ChatUtil.getChatTimeline;
import static hr.tvz.cartographers.utils.ChatUtil.sendChatMessage;
import static hr.tvz.cartographers.utils.GameStateUtil.getLastGameState;
import static hr.tvz.cartographers.utils.GameUtil.*;
import static hr.tvz.cartographers.utils.MenuUtil.returnToMenu;
import static hr.tvz.cartographers.utils.PlayerSynchronizationUtil.saveGameState;
import static javafx.scene.input.KeyCode.ENTER;

@RequiredArgsConstructor
public class GameController {

    private GameState gameState;
    private Timeline chatTimeline;

    @FXML
    private Label primaryPlayerLabel;
    @FXML
    private Label secondaryPlayerLabel;
    @FXML
    private GridPane primaryGameGrid;
    @FXML
    private GridPane secondaryGameGrid;
    @FXML
    private VBox cardDisplay;
    @FXML
    private Label seasonLabel;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label edictLabel;
    @FXML
    private TextField chatTextField;
    @FXML
    private TextArea chatTextArea;
    @FXML
    private AnchorPane chatArea;

    @FXML
    public synchronized void initialize() {
        Player player = getPlayer();

        if (player.equals(SINGLE_PLAYER)) {
            this.chatArea.setVisible(false);
            this.secondaryGameGrid.setVisible(false);
            this.primaryPlayerLabel.setVisible(false);
            this.secondaryPlayerLabel.setVisible(false);
        } else {
            if (player.equals(PLAYER_ONE)) {
                this.primaryPlayerLabel.setText(PLAYER_ONE.getLabel());
                this.secondaryPlayerLabel.setText(PLAYER_TWO.getLabel());
            } else if (player.equals(PLAYER_TWO)) {
                this.primaryPlayerLabel.setText(PLAYER_TWO.getLabel());
                this.secondaryPlayerLabel.setText(PLAYER_ONE.getLabel());
            }

            chatTimeline = getChatTimeline(chatTextArea);
            chatTimeline.play();
        }

        gameState = getLastGameState();
        initializeGame(primaryGameGrid, secondaryGameGrid, cardDisplay, seasonLabel, scoreLabel, edictLabel, gameState);
        saveGameState(gameState);
    }

    @FXML
    protected void onMouseHover(MouseEvent event) {
        highlightShape(event, true);
    }

    @FXML
    protected void onMouseExit(MouseEvent event) {
        highlightShape(event, false);
    }

    @FXML
    protected void onMouseClick(MouseEvent event) {
        boolean placedShape = placeShape(event, gameState);

        if (placedShape) {
            saveGameState(gameState);
        }
    }

    @FXML
    protected void onSendMessage(KeyEvent event) {
        if (event.getCode() == ENTER) {
            sendChatMessage(chatTextField.getText());
            chatTextField.clear();
        }
    }

    @FXML
    protected void onReturnToMenu() {
        if (chatTimeline != null)
            chatTimeline.pause();

        returnToMenu();
    }
}
