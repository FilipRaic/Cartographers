package hr.tvz.cartographers.controllers;

import hr.tvz.cartographers.CartographersApplication;
import hr.tvz.cartographers.shared.enums.Player;
import hr.tvz.cartographers.utils.ChatUtil;
import hr.tvz.cartographers.utils.GameMoveUtil;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;

import static hr.tvz.cartographers.utils.GameUtil.*;
import static hr.tvz.cartographers.utils.PlayerSynchronizationUtil.saveMove;
import static hr.tvz.cartographers.utils.PlayerSynchronizationUtil.startServerThreads;

@RequiredArgsConstructor
public class GameController {

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
        if (CartographersApplication.getPlayer().equals(Player.SINGLE_PLAYER)) {
            this.chatArea.setVisible(false);
        } else {
            Timeline chatMessagesTimeline = ChatUtil.getChatTimeline(chatTextArea);
            chatMessagesTimeline.play();
        }

        initializeGame(primaryGameGrid, secondaryGameGrid, cardDisplay, seasonLabel, scoreLabel, coinLabel, edictLabel);
        startServerThreads();

        Timeline theLastGameMoveTimeline = GameMoveUtil.getLastGameMoveTimeline(secondaryGameGrid);
        theLastGameMoveTimeline.play();
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
    protected void sendChatMessage() {
        ChatUtil.sendChatMessage(chatTextField.getText());
        chatTextField.clear();
    }
}
