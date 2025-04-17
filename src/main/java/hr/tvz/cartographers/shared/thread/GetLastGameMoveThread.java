package hr.tvz.cartographers.shared.thread;

import hr.tvz.cartographers.models.GameMove;
import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class GetLastGameMoveThread extends AbstractGameMoveThread implements Runnable {

    private GridPane secondaryGameGrid;

    @Override
    public void run() {
        Optional<GameMove> theLastGameMoveOptional = super.getGameMove();
        theLastGameMoveOptional.ifPresent(gameMove -> Platform.runLater(gameMove::toString));
    }
}
