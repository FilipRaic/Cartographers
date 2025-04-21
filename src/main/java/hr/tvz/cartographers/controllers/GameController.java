package hr.tvz.cartographers.controllers;

import hr.tvz.cartographers.models.GameState;
import hr.tvz.cartographers.shared.enums.Player;
import hr.tvz.cartographers.shared.thread.GetLastGameStateThread;
import hr.tvz.cartographers.utils.GameStateUtil;
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

import java.util.Optional;

import static hr.tvz.cartographers.CartographersApplication.getPlayer;
import static hr.tvz.cartographers.shared.enums.Player.*;
import static hr.tvz.cartographers.utils.ChatUtil.getChatTimeline;
import static hr.tvz.cartographers.utils.ChatUtil.sendChatMessage;
import static hr.tvz.cartographers.utils.GameStateUtil.getLastGameStateTimeline;
import static hr.tvz.cartographers.utils.GameUtil.*;
import static hr.tvz.cartographers.utils.MenuUtil.returnToMenu;
import static hr.tvz.cartographers.utils.PlayerSynchronizationUtil.saveMove;
import static hr.tvz.cartographers.utils.PlayerSynchronizationUtil.startServerThreads;
import static javafx.scene.input.KeyCode.ENTER;

@RequiredArgsConstructor
public class GameController {

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
    private Label coinLabel;
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

            Timeline chatMessagesTimeline = getChatTimeline(chatTextArea);
            chatMessagesTimeline.play();
        }

        startServerThreads();

        Timeline theLastGameStateTimeline = getLastGameStateTimeline(secondaryGameGrid);
        theLastGameStateTimeline.play();

        GetLastGameStateThread getLastGameStateThread = new GetLastGameStateThread(secondaryGameGrid);
        Optional<GameState> currentGameStateOptional = getLastGameStateThread.getGameState();

        if (currentGameStateOptional.isPresent()) {
            GameState currentGameState = currentGameStateOptional.get();

            GameStateUtil.gameStateToGridPane(primaryGameGrid, currentGameState.getPrimaryGrid());
            GameStateUtil.gameStateToGridPane(secondaryGameGrid, currentGameState.getSecondaryGrid());
        } else {
            initializeGame(primaryGameGrid, cardDisplay, seasonLabel, scoreLabel, coinLabel, edictLabel);
        }
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
        placeShape(event);
        saveMove(primaryGameGrid, secondaryGameGrid);
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
        returnToMenu();
    }
}
