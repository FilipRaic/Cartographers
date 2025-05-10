package hr.tvz.cartographers.controllers;

import hr.tvz.cartographers.models.GameState;
import hr.tvz.cartographers.shared.enums.Player;
import hr.tvz.cartographers.shared.exception.CustomException;
import hr.tvz.cartographers.shared.thread.GetLastGameStateThread;
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
import static hr.tvz.cartographers.utils.GameStateUtil.getLastGameStateTimeline;
import static hr.tvz.cartographers.utils.GameUtil.*;
import static hr.tvz.cartographers.utils.MenuUtil.returnToMenu;
import static hr.tvz.cartographers.utils.PlayerSynchronizationUtil.saveGameState;
import static hr.tvz.cartographers.utils.PlayerSynchronizationUtil.startServerThreads;
import static javafx.scene.input.KeyCode.ENTER;

@RequiredArgsConstructor
public class GameController {

    private GameState gameState;
    private Timeline chatTimeline;
    private Timeline gameStateTimeline;

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
    public void initialize() {
        Player player = getPlayer();
        gameState = new GameState();

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

        startServerThreads(gameState);
        gameStateTimeline = getLastGameStateTimeline(gameState);
        gameStateTimeline.play();

        GetLastGameStateThread getLastGameStateThread = new GetLastGameStateThread(gameState);
        gameState = getLastGameStateThread.getGameState().orElseThrow(() -> new CustomException("Game state not found"));

        initializeGame(primaryGameGrid, secondaryGameGrid, cardDisplay, seasonLabel, scoreLabel, edictLabel, gameState);
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
        placeShape(event, gameState);
        saveGameState(gameState);
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
            chatTimeline.stop();

        if (gameStateTimeline != null)
            gameStateTimeline.stop();

        returnToMenu();
    }
}
