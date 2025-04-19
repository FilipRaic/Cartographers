package hr.tvz.cartographers.utils;

import hr.tvz.cartographers.models.GameState;
import hr.tvz.cartographers.shared.thread.GetLastGameMoveThread;
import hr.tvz.cartographers.shared.thread.SaveLastGameMoveThread;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameMoveUtil {

    public static Timeline getLastGameMoveTimeline(GridPane secondaryGameGrid) {
        Timeline theLastGameMoveTimeline = new Timeline(new KeyFrame(Duration.millis(1000), (ActionEvent _) -> {
            GetLastGameMoveThread getLastGameMoveThread = new GetLastGameMoveThread(secondaryGameGrid);
            Thread runner = new Thread(getLastGameMoveThread);
            runner.start();
        }), new KeyFrame(Duration.seconds(1)));
        theLastGameMoveTimeline.setCycleCount(Animation.INDEFINITE);
        return theLastGameMoveTimeline;
    }

    public static void startSaveLastGameMoveThread(GameState currentGameState) {
        SaveLastGameMoveThread saveLastGameMoveThread = new SaveLastGameMoveThread(currentGameState);
        Thread runner = new Thread(saveLastGameMoveThread);
        runner.start();
    }
}
